package cl.ucm.taller.tallermecanico.repository;

import cl.ucm.taller.tallermecanico.entity.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {
}
