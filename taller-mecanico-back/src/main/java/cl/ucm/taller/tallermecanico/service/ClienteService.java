package cl.ucm.taller.tallermecanico.service;

import cl.ucm.taller.tallermecanico.entity.Cliente;
import java.util.List;

// La rúbrica exige que los servicios sean "Interfaz + clase de implementación"
public interface ClienteService {
    List<Cliente> obtenerTodos();
    Cliente buscarPorId(Long id);
    Cliente guardar(Cliente cliente);
    Cliente actualizar(Long id, Cliente cliente);
    void eliminar(Long id);
}
