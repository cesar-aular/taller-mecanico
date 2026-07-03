package cl.ucm.taller.tallermecanico.dto.out;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class RegisterDtoOut {
    private String username;
    private String email;
    private String rol;
}
