package cl.ucm.taller.tallermecanico.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehiculoDtoIn {

    @NotBlank(message = "La patente es obligatoria")
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    private Integer anio;

    // El frontend envía solo el ID del cliente dueño, no el objeto completo
    @NotNull(message = "Debe indicar el ID del cliente dueño del vehículo")
    private Long clienteId;
}
