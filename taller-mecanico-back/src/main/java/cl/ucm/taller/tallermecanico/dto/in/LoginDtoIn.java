package cl.ucm.taller.tallermecanico.dto.in;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDtoIn {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
}
