package cl.ucm.taller.tallermecanico.controller;

import cl.ucm.taller.tallermecanico.dto.in.ClienteDtoIn;
import cl.ucm.taller.tallermecanico.dto.out.ClienteDtoOut;
import cl.ucm.taller.tallermecanico.entity.Cliente;
import cl.ucm.taller.tallermecanico.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Indica que esta clase expone endpoints web (REST) y la respuesta viaja en formato JSON
@RequestMapping("/api/clientes") // Define que la URL para entrar aquí empezará con http://.../api/clientes
@RequiredArgsConstructor
public class ClienteController {

    // Nuevamente Inyección de dependencias: El controlador solo conoce el Servicio, no la Base de datos
    private final ClienteService clienteService;

    // Endpoint 1: Maneja las peticiones GET (para obtener información)
    @GetMapping
    public ResponseEntity<List<ClienteDtoOut>> listarClientes() {
        // 1. Pedimos las Entidades al servicio
        List<Cliente> clientesEntity = clienteService.obtenerTodos();
        
        // 2. Convertimos (Mapeamos) las Entidades a DtoOut
        List<ClienteDtoOut> clientesDto = clientesEntity.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
                
        // 3. Retornamos la respuesta con código 200 (OK)
        return ResponseEntity.ok(clientesDto); 
    }

    // Endpoint: GET /api/clientes/{id} — "Ver detalle" (funcionalidad ROLE_USER).
    // Si el id no existe, el servicio lanza NotFoundException y responde 404.
    @GetMapping("/{id}")
    public ResponseEntity<ClienteDtoOut> obtenerCliente(@PathVariable Long id) {
        return ResponseEntity.ok(mapearADto(clienteService.buscarPorId(id)));
    }

    // Endpoint 2: Maneja las peticiones POST (para crear o enviar nueva información)
    @PostMapping
    public ResponseEntity<ClienteDtoOut> crearCliente(@Valid @RequestBody ClienteDtoIn dtoIn) {
        // @Valid obliga a Spring a revisar las anotaciones (@NotBlank, @Email) antes de continuar.
        // @RequestBody agarra el JSON que envía el front-end y lo convierte en nuestro objeto Java (ClienteDtoIn).

        // 1. Mapeamos de DtoIn a Entidad (Porque el servicio espera Entidades, no DTOs)
        Cliente clienteEntity = new Cliente();
        clienteEntity.setNombreCompleto(dtoIn.getNombreCompleto());
        clienteEntity.setRut(dtoIn.getRut());
        clienteEntity.setTelefono(dtoIn.getTelefono());
        clienteEntity.setEmail(dtoIn.getEmail());

        // 2. Guardamos pasando la lógica de negocio (Servicio)
        Cliente guardado = clienteService.guardar(clienteEntity);
        
        // 3. Mapeamos de Entidad a DtoOut y respondemos con código 201 (Created)
        return new ResponseEntity<>(mapearADto(guardado), HttpStatus.CREATED); 
    }

    // Método auxiliar interno para no repetir código de conversión
    private ClienteDtoOut mapearADto(Cliente entidad) {
        ClienteDtoOut dto = new ClienteDtoOut();
        dto.setId(entidad.getId());
        dto.setNombreCompleto(entidad.getNombreCompleto());
        dto.setRut(entidad.getRut());
        dto.setTelefono(entidad.getTelefono());
        dto.setEmail(entidad.getEmail());
        return dto;
    }

    // Endpoint 3: Maneja peticiones PUT (para actualizar información existente)
    @PutMapping("/{id}")
    public ResponseEntity<ClienteDtoOut> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDtoIn dtoIn) {
        Cliente clienteEntity = new Cliente();
        clienteEntity.setNombreCompleto(dtoIn.getNombreCompleto());
        clienteEntity.setRut(dtoIn.getRut());
        clienteEntity.setTelefono(dtoIn.getTelefono());
        clienteEntity.setEmail(dtoIn.getEmail());

        Cliente actualizado = clienteService.actualizar(id, clienteEntity);
        return ResponseEntity.ok(mapearADto(actualizado));
    }

    // Endpoint 4: Maneja peticiones DELETE (para eliminar registros)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
