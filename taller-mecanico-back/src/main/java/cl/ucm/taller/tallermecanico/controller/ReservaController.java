package cl.ucm.taller.tallermecanico.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.security.Principal;
import cl.ucm.taller.tallermecanico.repository.ReservaRepository;
import cl.ucm.taller.tallermecanico.repository.MecanicoRepository;
import cl.ucm.taller.tallermecanico.repository.UserRepository;
import cl.ucm.taller.tallermecanico.repository.OrdenTrabajoRepository;
import cl.ucm.taller.tallermecanico.repository.VehiculoRepository;
import cl.ucm.taller.tallermecanico.entity.Reserva;
import cl.ucm.taller.tallermecanico.entity.Mecanico;
import cl.ucm.taller.tallermecanico.entity.User;
import cl.ucm.taller.tallermecanico.entity.OrdenTrabajo;
import cl.ucm.taller.tallermecanico.entity.Vehiculo;
import cl.ucm.taller.tallermecanico.error.NotFoundException;
import cl.ucm.taller.tallermecanico.error.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final MecanicoRepository mecanicoRepository;
    private final UserRepository userRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final VehiculoRepository vehiculoRepository;

    // GET /api/reservas — el personal del taller (USER=secretaria y ADMIN) ve TODAS
    // las citas agendadas para poder gestionarlas.
    @GetMapping
    public ResponseEntity<List<ReservaDtoOut>> listar() {
        List<ReservaDtoOut> out = reservaRepository.findAll().stream().map(r -> {
            ReservaDtoOut o = new ReservaDtoOut();
            o.setId(r.getId());
            o.setMotivo(r.getMotivo());
            o.setFecha(r.getFecha());
            o.setMecanicoId(r.getMecanico() != null ? r.getMecanico().getId() : null);
            o.setMecanicoNombre(r.getMecanico() != null ? r.getMecanico().getNombreCompleto() : "Sin asignar");
            o.setEstado(r.getEstado());
            o.setCliente(r.getUser().getUsername());
            return o;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PostMapping
    public ResponseEntity<ReservaDtoOut> crearReserva(@Valid @RequestBody ReservaDtoIn dto, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findById(username).orElseThrow(() -> new RuntimeException("User not found"));
        Mecanico mecanico = mecanicoRepository.findById(dto.getMecanicoId())
            .orElseThrow(() -> new RuntimeException("Mecánico no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setMotivo(dto.getMotivo());
        reserva.setFecha(dto.getFecha());
        reserva.setEstado("Pendiente");
        reserva.setMecanico(mecanico);
        reserva.setUser(user);

        reserva = reservaRepository.save(reserva);

        ReservaDtoOut out = new ReservaDtoOut();
        out.setId(reserva.getId());
        out.setMotivo(reserva.getMotivo());
        out.setFecha(reserva.getFecha());
        out.setMecanicoId(mecanico.getId());
        out.setMecanicoNombre(mecanico.getNombreCompleto());
        out.setEstado(reserva.getEstado());
        out.setCliente(user.getUsername());
        return new ResponseEntity<>(out, HttpStatus.CREATED);
    }

    // POST /api/reservas/{id}/convertir — la secretaria (USER) convierte una cita
    // en una ORDEN DE TRABAJO real, asignándole un vehículo. Cae bajo la regla
    // POST /api/reservas/** (permitida a USER y ADMIN) de SecurityConfig.
    @PostMapping("/{id}/convertir")
    public ResponseEntity<OrdenCreadaDtoOut> convertirEnOrden(@PathVariable Long id,
                                                              @Valid @RequestBody ConvertirDtoIn dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cita no encontrada con ID: " + id));

        if ("Convertida".equals(reserva.getEstado())) {
            throw new ConflictException("Esta cita ya fue convertida en una orden de trabajo");
        }

        Vehiculo vehiculo = vehiculoRepository.findById(dto.getVehiculoId())
                .orElseThrow(() -> new NotFoundException("Vehículo no encontrado con ID: " + dto.getVehiculoId()));

        // La cita (motivo/mecánico) se transforma en la orden; el vehículo lo aporta
        // la secretaria al momento de recepcionar el auto.
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setDescripcionProblema(reserva.getMotivo());
        orden.setFechaIngreso(LocalDateTime.now());
        orden.setEstado("Pendiente");
        orden.setCostoTotal(dto.getCostoTotal());
        orden.setVehiculo(vehiculo);
        orden.setMecanico(reserva.getMecanico());
        orden = ordenTrabajoRepository.save(orden);

        // La cita queda marcada para no convertirla dos veces
        reserva.setEstado("Convertida");
        reservaRepository.save(reserva);

        OrdenCreadaDtoOut out = new OrdenCreadaDtoOut();
        out.setOrdenId(orden.getId());
        out.setEstado(orden.getEstado());
        out.setPatente(vehiculo.getPatente());
        out.setMensaje("Cita #" + id + " convertida en la orden de trabajo #" + orden.getId());
        return new ResponseEntity<>(out, HttpStatus.CREATED);
    }

    @Data
    public static class ReservaDtoIn {
        @NotBlank(message = "El motivo no puede estar vacío")
        @Size(min = 10, max = 255, message = "El motivo debe tener entre 10 y 255 caracteres")
        private String motivo;

        @NotNull(message = "La fecha es requerida")
        @Future(message = "La fecha de reserva debe ser en el futuro")
        private LocalDateTime fecha;

        @NotNull(message = "Debes seleccionar un mecánico")
        private Long mecanicoId;
    }

    @Data
    public static class ConvertirDtoIn {
        @NotNull(message = "Debes seleccionar el vehículo que ingresa al taller")
        private Long vehiculoId;

        @NotNull(message = "El costo es requerido")
        @PositiveOrZero(message = "El costo no puede ser negativo")
        private Double costoTotal;
    }

    @Data
    public static class ReservaDtoOut {
        private Long id;
        private String motivo;
        private LocalDateTime fecha;
        private Long mecanicoId;
        private String mecanicoNombre;
        private String estado;
        private String cliente; // username del que agendó la cita
    }

    @Data
    public static class OrdenCreadaDtoOut {
        private Long ordenId;
        private String estado;
        private String patente;
        private String mensaje;
    }
}
