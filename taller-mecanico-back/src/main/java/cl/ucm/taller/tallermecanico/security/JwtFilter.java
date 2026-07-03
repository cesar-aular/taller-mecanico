package cl.ucm.taller.tallermecanico.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        // 1. ¿Tiene header Authorization con "Bearer "?
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // deja pasar sin autenticar
        }

        // Extraer el token
        String jwt = authHeader.split(" ")[1].trim();

        // 2. ¿El JWT es válido y no expiró?
        if (!jwtUtil.isValid(jwt)) {
            filterChain.doFilter(request, response);
            return; // deja pasar sin autenticar
        }

        // 3. Extrae el username del token y carga el usuario completo de la BD
        // Si el usuario del token ya no existe en la BD, seguimos sin autenticar
        // (la petición terminará en 401) en vez de botar el filtro con un 500.
        try {
            String username = jwtUtil.getUsername(jwt);
            UserDetails user = userDetailsService.loadUserByUsername(username);

            // 4. Registra al usuario en SecurityContextHolder
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (UsernameNotFoundException e) {
            SecurityContextHolder.clearContext();
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
