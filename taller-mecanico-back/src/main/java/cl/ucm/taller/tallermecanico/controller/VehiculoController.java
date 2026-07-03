package cl.ucm.taller.tallermecanico.controller;

import cl.ucm.taller.tallermecanico.dto.in.VehiculoDtoIn;
import cl.ucm.taller.tallermecanico.dto.out.VehiculoDtoOut;
import cl.ucm.taller.tallermecanico.entity.Vehiculo;
import cl.ucm.taller.tallermecanico.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    // El controlador solo delega al servicio y mapea Entidad <-> DTO
    private final VehiculoService vehiculoService;

    // GET /api/vehiculos -> 200 OK (ROLE_USER o ROLE_ADMIN)
    @GetMapping
    public ResponseEntity<List<VehiculoDtoOut>> listarVehiculos() {
        List<VehiculoDtoOut> vehiculos = vehiculoService.obtenerTodos().stream()
                .map(this::mapearADto)
                .toList();
        return ResponseEntity.ok(vehiculos);
    }

    // GET /api/vehiculos/{id} -> 200 OK o 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDtoOut> obtenerVehiculo(@PathVariable Long id) {
        return ResponseEntity.ok(mapearADto(vehiculoService.buscarPorId(id)));
    }

    // POST /api/vehiculos -> 201 Created (solo ROLE_ADMIN)
    @PostMapping
    public ResponseEntity<VehiculoDtoOut> crearVehiculo(@Valid @RequestBody VehiculoDtoIn dtoIn) {
        Vehiculo guardado = vehiculoService.guardar(dtoIn);
        return new ResponseEntity<>(mapearADto(guardado), HttpStatus.CREATED);
    }

    // PUT /api/vehiculos/{id} -> 200 OK (solo ROLE_ADMIN)
    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDtoOut> actualizarVehiculo(@PathVariable Long id, @Valid @RequestBody VehiculoDtoIn dtoIn) {
        return ResponseEntity.ok(mapearADto(vehiculoService.actualizar(id, dtoIn)));
    }

    // DELETE /api/vehiculos/{id} -> 204 No Content (solo ROLE_ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private VehiculoDtoOut mapearADto(Vehiculo entidad) {
        VehiculoDtoOut dto = new VehiculoDtoOut();
        dto.setId(entidad.getId());
        dto.setPatente(entidad.getPatente());
        dto.setMarca(entidad.getMarca());
        dto.setModelo(entidad.getModelo());
        dto.setAnio(entidad.getAnio());
        dto.setClienteId(entidad.getCliente().getId());
        dto.setClienteNombre(entidad.getCliente().getNombreCompleto());
        return dto;
    }
}
