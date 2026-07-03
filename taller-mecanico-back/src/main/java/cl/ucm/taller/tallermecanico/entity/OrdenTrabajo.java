package cl.ucm.taller.tallermecanico.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ordenes_trabajo")
public class OrdenTrabajo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcionProblema;

    private LocalDateTime fechaIngreso;
    
    private String estado; // Pendiente, En reparación, Listo, Entregado
    private Double costoTotal;

    // Relación: Muchas órdenes de trabajo pueden estar asociadas a Un vehículo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    // Relación: Muchas órdenes de trabajo pueden estar asignadas a Un mecánico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mecanico_id") // nullable = true porque puede no tener mecánico asignado al inicio
    private Mecanico mecanico;
}
