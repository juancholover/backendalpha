package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando se intenta operar con una universidad
 * cuya suscripción ha vencido.
 */
public class SubscripcionVencidaException extends RuntimeException {
    
    private final Long universidadId;
    
    public SubscripcionVencidaException(Long universidadId) {
        super(String.format("La suscripción de la universidad ID %d ha vencido", universidadId));
        this.universidadId = universidadId;
    }
    
    public Long getUniversidadId() {
        return universidadId;
    }
}
