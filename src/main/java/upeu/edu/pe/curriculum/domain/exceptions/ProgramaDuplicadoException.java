package upeu.edu.pe.curriculum.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe un programa académico con el mismo código.
 */
public class ProgramaDuplicadoException extends DuplicateResourceException {

    public ProgramaDuplicadoException(String codigo) {
        super("ProgramaAcademico", "codigo", codigo);
    }
}
