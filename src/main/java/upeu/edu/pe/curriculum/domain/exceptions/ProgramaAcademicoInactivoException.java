package upeu.edu.pe.curriculum.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando el programa académico no está activo.
 */
public class ProgramaAcademicoInactivoException extends BusinessException {

    private final Long programaId;

    public ProgramaAcademicoInactivoException(Long programaId) {
        super(String.format("El programa académico con ID %d no está activo", programaId));
        this.programaId = programaId;
    }

    public Long getProgramaId() {
        return programaId;
    }
}
