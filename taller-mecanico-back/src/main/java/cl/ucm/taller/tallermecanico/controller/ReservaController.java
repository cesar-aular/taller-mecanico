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
import cl.ucm.taller.tallermecanico.entity.Reserva;
import cl.ucm.taller.tallermecanico.entity.Mecanico;
import cl.ucm.taller.tallermecanico.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final MecanicoRepository mecanicoRepository;
    private final UserRepository userRepository;

    // Mock para guardar reservas
    @GetMapping
    public ResponseEntity<List<ReservaDtoOut>> misReservas(Principal principal) {
        String username = principal.getName();
        List<ReservaDtoOut> reservas = reservaRepository.findByUserUsername(username)
            .stream().map(r -> {
                ReservaDtoOut out = new ReservaDtoOut();
                out.setId(r.getId());
                out.setMotivo(r.getMotivo());
                out.setFecha(r.getFecha());
                out.setMecanicoId(r.getMecanico().getId());
                out.setMecanicoNombre(r.getMecanico().getNombreCompleto());
                out.setEstado(r.getEstado());
                return out;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(reservas);
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
    public static class ReservaDtoOut {
        private Long id;
        private String motivo;
        private LocalDateTime fecha;
        private Long mecanicoId;
        private String mecanicoNombre;
        private String estado;
    }
}
