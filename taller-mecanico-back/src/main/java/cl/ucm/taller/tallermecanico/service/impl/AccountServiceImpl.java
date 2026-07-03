package cl.ucm.taller.tallermecanico.service.impl;

import cl.ucm.taller.tallermecanico.dto.in.LoginDtoIn;
import cl.ucm.taller.tallermecanico.dto.in.RegisterDtoIn;
import cl.ucm.taller.tallermecanico.dto.out.LoginDtoOut;
import cl.ucm.taller.tallermecanico.dto.out.RegisterDtoOut;
import cl.ucm.taller.tallermecanico.entity.Role;
import cl.ucm.taller.tallermecanico.entity.User;
import cl.ucm.taller.tallermecanico.error.ConflictException;
import cl.ucm.taller.tallermecanico.error.NotFoundException;
import cl.ucm.taller.tallermecanico.repository.RoleRepository;
import cl.ucm.taller.tallermecanico.repository.UserRepository;
import cl.ucm.taller.tallermecanico.security.JwtUtil;
import cl.ucm.taller.tallermecanico.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginDtoOut login(LoginDtoIn in) {
        // Se crea un "ticket temporal" y el AuthenticationManager compara la
        // contraseña (hasheada con BCrypt) contra la guardada en la BD.
        // Si el usuario no existe o la contraseña es incorrecta lanza una
        // AuthenticationException, que el GlobalExceptionHandler traduce a 401.
        UsernamePasswordAuthenticationToken login =
                new UsernamePasswordAuthenticationToken(in.getUsername(), in.getPassword());

        Authentication authentication = authenticationManager.authenticate(login);

        // Claim "roles" separado por comas (ej: "ROLE_ADMIN,ROLE_USER"),
        // fácil de parsear en el frontend al leer el payload del JWT.
        // Se filtra por prefijo "ROLE_" porque Spring Security agrega authorities
        // internas (ej: FACTOR_PASSWORD) que no son roles del dominio.
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .collect(Collectors.joining(","));

        String jwt = jwtUtil.create(in.getUsername(), roles);

        return new LoginDtoOut(jwt);
    }

    @Override
    public RegisterDtoOut register(RegisterDtoIn in) {
        // Username duplicado -> 409 Conflict (exigido por la rúbrica de Login/Registro)
        if (userRepository.findByUsername(in.getUsername()).isPresent()) {
            throw new ConflictException("El nombre de usuario ya existe");
        }

        // El registro público SIEMPRE crea usuarios con ROLE_USER: /auth/register es
        // permitAll(), así que si confiáramos en in.getRol() cualquiera podría enviar
        // {"rol":"ROLE_ADMIN"} y auto-promoverse (escalada de privilegios). Un admin ya
        // existente es quien promueve roles desde /api/users (UserController).
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new NotFoundException("El rol ROLE_USER no existe"));

        User newUser = new User();
        newUser.setUsername(in.getUsername());
        newUser.setEmail(in.getEmail());
        newUser.setPassword(passwordEncoder.encode(in.getPassword()));
        newUser.setRoles(List.of(role));

        userRepository.save(newUser);
        
        return new RegisterDtoOut(newUser.getUsername(), newUser.getEmail(), role.getName());
    }
}
