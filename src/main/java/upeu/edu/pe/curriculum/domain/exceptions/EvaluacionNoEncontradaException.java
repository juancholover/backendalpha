package upeu.edu.pe.curriculum.domain.exceptions;

/**
 * Excepción lanzada cuando no se encuentra una evaluación de calidad para un sílabo
 */
public class EvaluacionNoEncontradaException extends RuntimeException {
    
    private final Long silaboId;

    public EvaluacionNoEncontradaException(Long silaboId) {
        super(String.format("No se encontró evaluación de calidad para el sílabo ID %d", silaboId));
        this.silaboId = silaboId;
    }

    public Long getSilaboId() {
        return silaboId;
    }
}
