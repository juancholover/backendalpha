package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando se intenta agregar más estudiantes
 * de los permitidos por el plan de la universidad.
 */
public class LimiteEstudiantesExcedidoException extends RuntimeException {
    
    private final Integer limite;
    private final Integer actual;
    
    public LimiteEstudiantesExcedidoException(Integer limite, Integer actual) {
        super(String.format("Límite de estudiantes excedido. Máximo: %d, Actual: %d", limite, actual));
        this.limite = limite;
        this.actual = actual;
    }
    
    public Integer getLimite() {
        return limite;
    }
    
    public Integer getActual() {
        return actual;
    }
}
