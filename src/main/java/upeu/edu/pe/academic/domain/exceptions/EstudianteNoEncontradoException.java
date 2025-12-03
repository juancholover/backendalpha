package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un estudiante.
 */
public class EstudianteNoEncontradoException extends RuntimeException {
    
    private final Long id;
    
    public EstudianteNoEncontradoException(Long id) {
        super(String.format("Estudiante con ID %d no encontrado", id));
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
}
