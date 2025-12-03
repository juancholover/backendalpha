package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción lanzada cuando el programa académico no está activo.
 */
public class ProgramaAcademicoInactivoException extends RuntimeException {
    
    private final Long programaId;
    
    public ProgramaAcademicoInactivoException(Long programaId) {
        super(String.format("El programa académico con ID %d no está activo", programaId));
        this.programaId = programaId;
    }
    
    public Long getProgramaId() {
        return programaId;
    }
}
