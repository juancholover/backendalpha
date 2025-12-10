package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un criterio de evaluación.
 */
public class CriterioNoEncontradoException extends NotFoundException {

    private final Long id;

    public CriterioNoEncontradoException(Long id) {
        super(String.format("Criterio de evaluación con ID %d no encontrado", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
