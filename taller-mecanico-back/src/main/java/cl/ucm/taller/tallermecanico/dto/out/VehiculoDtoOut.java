package cl.ucm.taller.tallermecanico.dto.out;

import lombok.Data;

@Data
public class VehiculoDtoOut {
    private Long id;
    private String patente;
    private String marca;
    private String modelo;
    private Integer anio;
    // Datos "aplanados" del dueño: evitamos exponer la entidad Cliente completa
    private Long clienteId;
    private String clienteNombre;
}
