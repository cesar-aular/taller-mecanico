package cl.ucm.taller.tallermecanico.repository;

import cl.ucm.taller.tallermecanico.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Indica que este componente interactúa con la base de datos
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Al respetar la convención de nombres "findBy[Atributo]", 
    // Spring Data JPA escribe la consulta SQL automáticamente por debajo.
    Optional<Cliente> findByEmail(String email);
    
    Optional<Cliente> findByRut(String rut);
}
