package cl.ucm.taller.tallermecanico.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice actúa como un "radar" global: atrapa las excepciones de
// cualquier controlador y las convierte en una respuesta JSON uniforme (ErrorInfo),
// evitando exponer stacktraces al cliente (exigencia de la rúbrica).
@ControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación de los DTO de entrada (@Valid) -> 400 Bad Request
    // Devuelve un mapa {campo: mensaje} para que React pinte el error junto al input.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Recurso no encontrado -> 404 Not Found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorInfo> handleNotFound(HttpServletRequest request, NotFoundException ex) {
        ErrorInfo errorInfo = new ErrorInfo(ex.getMessage(), HttpStatus.NOT_FOUND.value(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.NOT_FOUND);
    }

    // Recurso duplicado (RUT, patente, username) -> 409 Conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorInfo> handleConflict(HttpServletRequest request, ConflictException ex) {
        ErrorInfo errorInfo = new ErrorInfo(ex.getMessage(), HttpStatus.CONFLICT.value(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.CONFLICT);
    }

    // Credenciales inválidas en el login (BadCredentialsException, etc.) -> 401 Unauthorized
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorInfo> handleAuthentication(HttpServletRequest request, AuthenticationException ex) {
        ErrorInfo errorInfo = new ErrorInfo("Usuario o contraseña incorrectos", HttpStatus.UNAUTHORIZED.value(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.UNAUTHORIZED);
    }

    // Cualquier otro error de negocio no clasificado -> 400 Bad Request
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorInfo> handleRuntimeException(HttpServletRequest request, RuntimeException ex) {
        ErrorInfo errorInfo = new ErrorInfo(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI());
        return new ResponseEntity<>(errorInfo, HttpStatus.BAD_REQUEST);
    }
}
