package cl.ucm.taller.tallermecanico.service;

import cl.ucm.taller.tallermecanico.dto.in.VehiculoDtoIn;
import cl.ucm.taller.tallermecanico.entity.Vehiculo;
import java.util.List;

// Interfaz + implementación, igual que ClienteService (exigencia de la rúbrica)
public interface VehiculoService {
    List<Vehiculo> obtenerTodos();
    Vehiculo buscarPorId(Long id);
    Vehiculo guardar(VehiculoDtoIn dtoIn);
    Vehiculo actualizar(Long id, VehiculoDtoIn dtoIn);
    void eliminar(Long id);
}
