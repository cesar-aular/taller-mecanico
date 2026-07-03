package cl.ucm.taller.tallermecanico.error;

// Excepción de negocio: el recurso pedido no existe en la BD.
// El GlobalExceptionHandler la traduce a un HTTP 404 (Not Found).
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
