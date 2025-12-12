package upeu.edu.pe.curriculum.domain.exceptions;

/**
 * Excepción lanzada cuando un sílabo no cumple con el estándar de calidad mínimo
 */
public class CalidadInsuficienteException extends RuntimeException {
    
    private final Long silaboId;
    private final Integer puntajeObtenido;
    private final Integer puntajeRequerido;

    public CalidadInsuficienteException(Long silaboId, Integer puntajeObtenido, Integer puntajeRequerido) {
        super(String.format("El sílabo ID %d no cumple con el estándar de calidad. " +
                          "Puntaje obtenido: %d/100, requerido: %d/100", 
                          silaboId, puntajeObtenido, puntajeRequerido));
        this.silaboId = silaboId;
        this.puntajeObtenido = puntajeObtenido;
        this.puntajeRequerido = puntajeRequerido;
    }

    public Long getSilaboId() {
        return silaboId;
    }

    public Integer getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public Integer getPuntajeRequerido() {
        return puntajeRequerido;
    }
}
