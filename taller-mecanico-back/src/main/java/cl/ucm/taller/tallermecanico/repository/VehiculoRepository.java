package cl.ucm.taller.tallermecanico.repository;



import cl.ucm.taller.tallermecanico.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // Método para validar si ya existe un vehículo con esa patente en BD
    boolean existsByPatente(String patente);

    // Para bloquear el borrado de un cliente que todavía tiene vehículos (FK)
    boolean existsByClienteId(Long clienteId);

    // JPQL con JOIN FETCH (visto en clase): trae el vehículo junto a su cliente
    // en una sola consulta, evitando el problema de la carga LAZY fuera de sesión.
    @Query("SELECT v FROM Vehiculo v JOIN FETCH v.cliente")
    List<Vehiculo> findAllConCliente();

    @Query("SELECT v FROM Vehiculo v JOIN FETCH v.cliente WHERE v.id = :id")
    Optional<Vehiculo> findByIdConCliente(@Param("id") Long id);
}
