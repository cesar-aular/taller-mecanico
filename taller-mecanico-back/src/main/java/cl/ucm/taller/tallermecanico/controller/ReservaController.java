package cl.ucm.taller.tallermecanico.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    // Mock para guardar reservas
    @PostMapping
    public ResponseEntity<ReservaDtoOut> crearReserva(@Valid @RequestBody ReservaDtoIn dto) {
        ReservaDtoOut out = new ReservaDtoOut();
        out.setId(System.currentTimeMillis());
        out.setMotivo(dto.getMotivo());
        out.setFecha(dto.getFecha());
        out.setMecanicoId(dto.getMecanicoId());
        out.setEstado("Pendiente");
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
        private String estado;
    }
}
