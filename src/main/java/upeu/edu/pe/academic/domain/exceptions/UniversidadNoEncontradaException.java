package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción de dominio lanzada cuando no se encuentra una universidad.
 */
public class UniversidadNoEncontradaException extends RuntimeException {
    
    private final Long id;
    
    public UniversidadNoEncontradaException(Long id) {
        super(String.format("Universidad con ID %d no encontrada", id));
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
}
