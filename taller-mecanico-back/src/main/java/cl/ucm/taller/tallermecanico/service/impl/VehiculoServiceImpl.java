package cl.ucm.taller.tallermecanico.service.impl;

import cl.ucm.taller.tallermecanico.dto.in.VehiculoDtoIn;
import cl.ucm.taller.tallermecanico.entity.Cliente;
import cl.ucm.taller.tallermecanico.entity.Vehiculo;
import cl.ucm.taller.tallermecanico.error.ConflictException;
import cl.ucm.taller.tallermecanico.error.NotFoundException;
import cl.ucm.taller.tallermecanico.repository.ClienteRepository;
import cl.ucm.taller.tallermecanico.repository.VehiculoRepository;
import cl.ucm.taller.tallermecanico.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAllConCliente();
    }

    @Override
    public Vehiculo buscarPorId(Long id) {
        return vehiculoRepository.findByIdConCliente(id)
                .orElseThrow(() -> new NotFoundException("Vehículo no encontrado con ID: " + id));
    }

    @Override
    public Vehiculo guardar(VehiculoDtoIn dtoIn) {
        // Regla de negocio: la patente es única en el sistema -> 409 si se repite
        if (vehiculoRepository.existsByPatente(dtoIn.getPatente())) {
            throw new ConflictException("La patente ya se encuentra registrada en el sistema");
        }

        // Validación de FK: el cliente dueño debe existir -> 404 si no existe
        Cliente cliente = clienteRepository.findById(dtoIn.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con ID: " + dtoIn.getClienteId()));

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(dtoIn.getPatente());
        vehiculo.setMarca(dtoIn.getMarca());
        vehiculo.setModelo(dtoIn.getModelo());
        vehiculo.setAnio(dtoIn.getAnio());
        vehiculo.setCliente(cliente);

        return vehiculoRepository.save(vehiculo);
    }

    @Override
    public Vehiculo actualizar(Long id, VehiculoDtoIn dtoIn) {
        Vehiculo vehiculoExistente = buscarPorId(id); // -> 404 si no existe

        // Si cambia la patente, validamos que la nueva no pertenezca a otro vehículo
        if (!vehiculoExistente.getPatente().equals(dtoIn.getPatente())
                && vehiculoRepository.existsByPatente(dtoIn.getPatente())) {
            throw new ConflictException("La patente ya pertenece a otro vehículo");
        }

        Cliente cliente = clienteRepository.findById(dtoIn.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado con ID: " + dtoIn.getClienteId()));

        vehiculoExistente.setPatente(dtoIn.getPatente());
        vehiculoExistente.setMarca(dtoIn.getMarca());
        vehiculoExistente.setModelo(dtoIn.getModelo());
        vehiculoExistente.setAnio(dtoIn.getAnio());
        vehiculoExistente.setCliente(cliente);

        return vehiculoRepository.save(vehiculoExistente);
    }

    @Override
    public void eliminar(Long id) {
        Vehiculo vehiculo = buscarPorId(id); // -> 404 si no existe
        vehiculoRepository.delete(vehiculo);
    }
}
