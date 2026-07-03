package cl.ucm.taller.tallermecanico.error;

import lombok.AllArgsConstructor;
import lombok.Data;

// Rúbrica: "error: Clase ErrorInfo con código y mensaje para respuestas de error uniformes"
@Data
@AllArgsConstructor
public class ErrorInfo {
    private String message;
    private int statusCode;
    private String uriRequested;
}
