package cl.ucm.taller.tallermecanico.dto.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Genera getters y setters
public class ClienteDtoIn {
    
    // Agregamos anotaciones de validación (Spring Boot Validation)
    // Esto asegura que la petición web no traiga datos basura.
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombreCompleto;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un formato de correo electrónico válido")
    private String email;
}
