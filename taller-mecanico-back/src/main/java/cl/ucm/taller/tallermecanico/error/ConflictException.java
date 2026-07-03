package cl.ucm.taller.tallermecanico.error;

// Excepción de negocio: el recurso ya existe (ej: RUT, patente o username duplicado).
// El GlobalExceptionHandler la traduce a un HTTP 409 (Conflict).
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
