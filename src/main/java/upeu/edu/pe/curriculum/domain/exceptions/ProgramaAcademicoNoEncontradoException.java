package upeu.edu.pe.curriculum.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un programa académico.
 */
public class ProgramaAcademicoNoEncontradoException extends NotFoundException {

    private final Long id;

    public ProgramaAcademicoNoEncontradoException(Long id) {
        super(String.format("Programa académico con ID %d no encontrado", id));
        this.id = id;
    }

    public ProgramaAcademicoNoEncontradoException(String campo, String valor) {
        super(String.format("Programa académico no encontrado con %s: %s", campo, valor));
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
