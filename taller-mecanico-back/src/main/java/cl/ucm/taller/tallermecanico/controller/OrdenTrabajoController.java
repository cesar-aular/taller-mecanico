package cl.ucm.taller.tallermecanico.controller;

import cl.ucm.taller.tallermecanico.entity.Mecanico;
import cl.ucm.taller.tallermecanico.entity.OrdenTrabajo;
import cl.ucm.taller.tallermecanico.entity.Repuesto;
import cl.ucm.taller.tallermecanico.repository.OrdenTrabajoRepository;
import cl.ucm.taller.tallermecanico.repository.RepuestoRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// Listado de órdenes de trabajo del taller (solo lectura, exclusivo de ROLE_ADMIN
// según SecurityConfig). Muestra la reparación con su vehículo, mecánico y repuestos.
@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenTrabajoController {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final RepuestoRepository repuestoRepository;

    @GetMapping
    public ResponseEntity<List<OrdenDtoOut>> listar() {
        List<OrdenDtoOut> ordenes = ordenTrabajoRepository.findAllConDetalle().stream()
                .map(this::mapear)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ordenes);
    }

    private OrdenDtoOut mapear(OrdenTrabajo o) {
        OrdenDtoOut dto = new OrdenDtoOut();
        dto.setId(o.getId());
        dto.setDescripcion(o.getDescripcionProblema());
        dto.setEstado(o.getEstado());
        dto.setCostoTotal(o.getCostoTotal());
        dto.setFechaIngreso(o.getFechaIngreso());
        dto.setPatente(o.getVehiculo().getPatente());
        dto.setVehiculo(o.getVehiculo().getMarca() + " " + o.getVehiculo().getModelo());

        Mecanico m = o.getMecanico();
        dto.setMecanico(m != null ? m.getNombreCompleto() : "Sin asignar");

        List<String> repuestos = repuestoRepository.findByOrdenTrabajoId(o.getId()).stream()
                .map(r -> r.getNombre() + " x" + r.getCantidad())
                .collect(Collectors.toList());
        dto.setRepuestos(repuestos);
        return dto;
    }

    @Data
    public static class OrdenDtoOut {
        private Long id;
        private String descripcion;
        private String estado;
        private Double costoTotal;
        private LocalDateTime fechaIngreso;
        private String patente;
        private String vehiculo;
        private String mecanico;
        private List<String> repuestos;
    }
}
