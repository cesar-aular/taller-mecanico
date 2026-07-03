package cl.ucm.taller.tallermecanico.config;

import cl.ucm.taller.tallermecanico.entity.Cliente;
import cl.ucm.taller.tallermecanico.entity.Mecanico;
import cl.ucm.taller.tallermecanico.entity.Role;
import cl.ucm.taller.tallermecanico.entity.User;
import cl.ucm.taller.tallermecanico.entity.Vehiculo;
import cl.ucm.taller.tallermecanico.repository.ClienteRepository;
import cl.ucm.taller.tallermecanico.repository.MecanicoRepository;
import cl.ucm.taller.tallermecanico.repository.RoleRepository;
import cl.ucm.taller.tallermecanico.repository.UserRepository;
import cl.ucm.taller.tallermecanico.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // 1. Roles base del sistema
        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(
                    new Role(null, "ROLE_ADMIN"),
                    new Role(null, "ROLE_USER")
            ));
        }

        // 2. Usuarios de prueba: uno por cada rol, para poder demostrar
        //    en la defensa el 401 (sin token), 403 (USER en ruta ADMIN) y 200 (ADMIN).
        if (userRepository.count() == 0) {
            System.out.println("🌱 Insertando usuarios de prueba (admin/admin123 y user/user123)...");
            Role rAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
            Role rUser = roleRepository.findByName("ROLE_USER").orElseThrow();

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@taller.cl");
            admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña encriptada con BCrypt
            admin.setRoles(List.of(rAdmin, rUser));

            User user = new User();
            user.setUsername("user");
            user.setEmail("user@taller.cl");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(List.of(rUser));

            userRepository.saveAll(List.of(admin, user));
        }

        // 3. Datos del dominio para que el frontend tenga algo que mostrar
        if (clienteRepository.count() == 0) {
            System.out.println("🌱 Base de datos vacía. Insertando Mocks de Clientes y Vehículos...");
            Cliente c1 = new Cliente();
            c1.setNombreCompleto("Juan Pérez");
            c1.setRut("11111111-1");
            c1.setEmail("juan@mail.com");
            c1.setTelefono("+56912345678");
            clienteRepository.save(c1);

            Vehiculo v1 = new Vehiculo();
            v1.setPatente("ABCD-12");
            v1.setMarca("Toyota");
            v1.setModelo("Yaris");
            v1.setAnio(2019);
            v1.setCliente(c1);
            vehiculoRepository.save(v1);
        }

        if (mecanicoRepository.count() == 0) {
            System.out.println("🌱 Base de datos vacía. Insertando Mocks de Mecánicos...");
            Mecanico m1 = new Mecanico();
            m1.setNombreCompleto("Carlos El Tuercas");
            m1.setEspecialidad("Frenos y Suspensión");
            mecanicoRepository.save(m1);
        }

        System.out.println("✅ Datos Mocks listos para que el Frontend los consuma.");
    }
}
