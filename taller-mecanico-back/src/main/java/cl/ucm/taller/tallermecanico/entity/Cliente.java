package cl.ucm.taller.tallermecanico.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100) // No puede ser nulo, máximo 100 caracteres
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 12) // No nulo, y no pueden existir 2 ruts iguales
    private String rut;

    @Column(length = 15)
    private String telefono;

    @Column(nullable = false, unique = true)
    private String email;

}
