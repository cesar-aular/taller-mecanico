package cl.ucm.taller.tallermecanico.controller;

import cl.ucm.taller.tallermecanico.entity.Role;
import cl.ucm.taller.tallermecanico.entity.User;
import cl.ucm.taller.tallermecanico.error.NotFoundException;
import cl.ucm.taller.tallermecanico.repository.RoleRepository;
import cl.ucm.taller.tallermecanico.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream().map(u -> {
            UserDto dto = new UserDto();
            dto.setUsername(u.getUsername());
            dto.setEmail(u.getEmail());
            dto.setRoles(u.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{username}/role")
    public ResponseEntity<UserDto> updateRole(@PathVariable String username, @RequestBody UpdateRoleDto updateRoleDto) {
        User user = userRepository.findById(username).orElseThrow(() -> new NotFoundException("User not found"));
        Role newRole = roleRepository.findByName(updateRoleDto.getRol())
                .orElseThrow(() -> new NotFoundException("Role not found: " + updateRoleDto.getRol()));
        
        user.setRoles(List.of(newRole));
        userRepository.save(user);

        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return ResponseEntity.ok(dto);
    }

    @Data
    public static class UserDto {
        private String username;
        private String email;
        private List<String> roles;
    }

    @Data
    public static class UpdateRoleDto {
        private String rol;
    }
}
