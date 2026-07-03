package cl.ucm.taller.tallermecanico.repository;

import cl.ucm.taller.tallermecanico.entity.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {
}
