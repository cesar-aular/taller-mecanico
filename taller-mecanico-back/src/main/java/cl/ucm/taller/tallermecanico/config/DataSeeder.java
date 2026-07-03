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
import cl.ucm.taller.tallermecanico.repository.OrdenTrabajoRepository;
import cl.ucm.taller.tallermecanico.repository.RepuestoRepository;
import cl.ucm.taller.tallermecanico.entity.OrdenTrabajo;
import cl.ucm.taller.tallermecanico.entity.Repuesto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final MecanicoRepository mecanicoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final RepuestoRepository repuestoRepository;
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
            Cliente c1 = new Cliente(null, "Juan Pérez", "12345678-9", "+5691234567", "juan@tonosmotors.cl");
            Cliente c2 = new Cliente(null, "Empresa Transportes Spa", "98765432-1", "+5698765432", "contacto@transportes.cl");
            Cliente c3 = new Cliente(null, "María González", "11111111-1", "+5691111111", "maria@mail.com");
            Cliente c4 = new Cliente(null, "Pedro Soto", "22222222-2", "+5692222222", "pedro@mail.com");
            clienteRepository.saveAll(List.of(c1, c2, c3, c4));

            Vehiculo v1 = new Vehiculo(null, "AABB-11", "Toyota", "Yaris", 2018, c1);
            Vehiculo v2 = new Vehiculo(null, "CCDD-22", "Hyundai", "Accent", 2020, c2);
            Vehiculo v3 = new Vehiculo(null, "EEFF-33", "Kia", "Morning", 2015, c3);
            Vehiculo v4 = new Vehiculo(null, "GGHH-44", "Chevrolet", "Spark", 2017, c4);
            Vehiculo v5 = new Vehiculo(null, "IIJJ-55", "Nissan", "Versa", 2022, c1);
            vehiculoRepository.saveAll(List.of(v1, v2, v3, v4, v5));
        }

        if (mecanicoRepository.count() == 0) {
            System.out.println("🌱 Insertando Mocks de Mecánicos, Órdenes y Repuestos...");
            Mecanico m1 = new Mecanico(null, "Antonio 'Toño' Ruiz", "Motor y Transmisión");
            Mecanico m2 = new Mecanico(null, "Carlos 'El Tuercas'", "Frenos y Suspensión");
            Mecanico m3 = new Mecanico(null, "Luis Martínez", "Electricidad Automotriz");
            mecanicoRepository.saveAll(List.of(m1, m2, m3));

            Cliente c1 = clienteRepository.findAll().get(0);
            Vehiculo v1 = vehiculoRepository.findAll().get(0);
            Vehiculo v2 = vehiculoRepository.findAll().get(1);
            Vehiculo v3 = vehiculoRepository.findAll().get(2);
            Vehiculo v4 = vehiculoRepository.findAll().get(3);
            Vehiculo v5 = vehiculoRepository.findAll().get(4);

            // Órdenes de trabajo
            OrdenTrabajo o1 = new OrdenTrabajo(null, "Frenos largos", LocalDateTime.now().minusDays(5), "Entregado", 150000.0, v1, m2);
            OrdenTrabajo o2 = new OrdenTrabajo(null, "Cambio de aceite y filtros", LocalDateTime.now().minusDays(2), "Listo", 45000.0, v2, m1);
            OrdenTrabajo o3 = new OrdenTrabajo(null, "Ruido en el motor al acelerar", LocalDateTime.now(), "Pendiente", 0.0, v3, null);
            OrdenTrabajo o4 = new OrdenTrabajo(null, "Revisión eléctrica luces", LocalDateTime.now().minusDays(1), "En reparación", 80000.0, v4, m3);
            OrdenTrabajo o5 = new OrdenTrabajo(null, "Mantención 10.000 km", LocalDateTime.now().minusDays(10), "Entregado", 120000.0, v5, m1);
            OrdenTrabajo o6 = new OrdenTrabajo(null, "Falla en caja de cambios", LocalDateTime.now().minusDays(3), "En reparación", 350000.0, v1, m1);
            OrdenTrabajo o7 = new OrdenTrabajo(null, "Cambio de pastillas traseras", LocalDateTime.now(), "Pendiente", 0.0, v2, null);
            
            ordenTrabajoRepository.saveAll(List.of(o1, o2, o3, o4, o5, o6, o7));

            // Repuestos
            Repuesto r1 = new Repuesto(null, "Pastillas de Freno", 2, 25000.0, o1);
            Repuesto r2 = new Repuesto(null, "Líquido de Frenos", 1, 10000.0, o1);
            Repuesto r3 = new Repuesto(null, "Filtro de Aceite", 1, 15000.0, o2);
            Repuesto r4 = new Repuesto(null, "Aceite 5W30", 4, 8000.0, o2);
            Repuesto r5 = new Repuesto(null, "Fusibles varios", 3, 2000.0, o4);
            Repuesto r6 = new Repuesto(null, "Filtro de Aire", 1, 12000.0, o5);
            Repuesto r7 = new Repuesto(null, "Kit Embrague", 1, 180000.0, o6);
            repuestoRepository.saveAll(List.of(r1, r2, r3, r4, r5, r6, r7));
        }

        System.out.println("✅ Datos Mocks listos para Toño's Motors.");
    }
}
