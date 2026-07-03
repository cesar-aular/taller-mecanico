package cl.ucm.taller.tallermecanico.service.impl;

import cl.ucm.taller.tallermecanico.entity.Cliente;
import cl.ucm.taller.tallermecanico.error.ConflictException;
import cl.ucm.taller.tallermecanico.error.NotFoundException;
import cl.ucm.taller.tallermecanico.repository.ClienteRepository;
import cl.ucm.taller.tallermecanico.service.ClienteService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service // Le dice a Spring que esta clase es un componente de servicio (lógica de negocio)
@RequiredArgsConstructor // Lombok genera un constructor con los atributos 'final', inyectando dependencias
public class ClienteServiceImpl implements ClienteService {

    // Dependencia inyectada: El servicio usa el repositorio, NUNCA usa SQL directo
    private final ClienteRepository clienteRepository;

    @Override
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll(); // Delegamos la búsqueda al repositorio
    }

    @Override
    public Cliente buscarPorId(Long id) {
        // Si el cliente no existe, la excepción se traduce en un HTTP 404
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public Cliente guardar(Cliente cliente) {
        // Lógica de negocio pura: antes de crear, validamos si el RUT ya existe (-> 409)
        if (clienteRepository.findByRut(cliente.getRut()).isPresent()) {
            throw new ConflictException("El RUT del cliente ya se encuentra registrado en el sistema");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        Cliente clienteExistente = buscarPorId(id); // -> 404 si no existe

        // Si intenta cambiar el RUT a uno que ya usa otro cliente -> 409
        clienteRepository.findByRut(clienteActualizado.getRut())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ConflictException("El RUT ya pertenece a otro cliente");
                });

        clienteExistente.setNombreCompleto(clienteActualizado.getNombreCompleto());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setRut(clienteActualizado.getRut());

        return clienteRepository.save(clienteExistente);
    }

    @Override
    public void eliminar(Long id) {
        // deleteById no falla si el id no existe, por eso validamos primero (-> 404)
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }
}
