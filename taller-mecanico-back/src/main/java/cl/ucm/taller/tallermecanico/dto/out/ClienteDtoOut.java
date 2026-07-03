package cl.ucm.taller.tallermecanico.dto.out;

import lombok.Data;

@Data
public class ClienteDtoOut {
    // Este objeto es el que verá el navegador o Frontend en formato JSON.
    // Solo contiene los datos que consideramos "seguros" de mostrar.
    private Long id;
    private String nombreCompleto;
    private String rut;
    private String telefono;
    private String email;
}
