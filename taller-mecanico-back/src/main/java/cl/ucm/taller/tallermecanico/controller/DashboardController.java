package cl.ucm.taller.tallermecanico.controller;

import cl.ucm.taller.tallermecanico.repository.OrdenTrabajoRepository;
import cl.ucm.taller.tallermecanico.repository.VehiculoRepository;
import cl.ucm.taller.tallermecanico.repository.ClienteRepository;
import cl.ucm.taller.tallermecanico.repository.MecanicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final MecanicoRepository mecanicoRepository;

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("totalVehiculos", vehiculoRepository.count());
        metrics.put("totalClientes", clienteRepository.count());
        metrics.put("totalMecanicos", mecanicoRepository.count());
        metrics.put("totalOrdenes", ordenTrabajoRepository.count());
        
        // Simular datos de estado de órdenes de trabajo de la bd mock
        long pendientes = ordenTrabajoRepository.findAll().stream().filter(o -> "Pendiente".equals(o.getEstado())).count();
        long enReparacion = ordenTrabajoRepository.findAll().stream().filter(o -> "En reparación".equals(o.getEstado())).count();
        long listos = ordenTrabajoRepository.findAll().stream().filter(o -> "Listo".equals(o.getEstado())).count();
        long entregados = ordenTrabajoRepository.findAll().stream().filter(o -> "Entregado".equals(o.getEstado())).count();

        metrics.put("ordenesPendientes", pendientes);
        metrics.put("ordenesEnReparacion", enReparacion);
        metrics.put("ordenesListos", listos);
        metrics.put("ordenesEntregados", entregados);

        return ResponseEntity.ok(metrics);
    }
}
