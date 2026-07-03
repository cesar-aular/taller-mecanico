package cl.ucm.taller.tallermecanico.repository;

import cl.ucm.taller.tallermecanico.entity.OrdenTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    // Permite buscar todas las órdenes asociadas a la ID de un vehículo específico
    List<OrdenTrabajo> findByVehiculoId(Long vehiculoId);

    // JOIN FETCH: trae la orden junto a su vehículo (obligatorio) y su mecánico
    // (opcional) en una sola consulta, evitando LazyInitializationException al
    // mapear a DTO fuera de la sesión.
    @Query("SELECT o FROM OrdenTrabajo o JOIN FETCH o.vehiculo LEFT JOIN FETCH o.mecanico ORDER BY o.fechaIngreso DESC")
    List<OrdenTrabajo> findAllConDetalle();
}
