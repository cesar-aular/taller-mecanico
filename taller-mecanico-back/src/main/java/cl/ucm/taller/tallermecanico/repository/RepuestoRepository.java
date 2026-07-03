package cl.ucm.taller.tallermecanico.repository;

import cl.ucm.taller.tallermecanico.entity.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {

    // Repuestos usados en una orden de trabajo concreta
    List<Repuesto> findByOrdenTrabajoId(Long ordenTrabajoId);
}
