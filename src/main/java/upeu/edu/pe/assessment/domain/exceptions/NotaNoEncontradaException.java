package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra una nota de evaluación.
 */
public class NotaNoEncontradaException extends NotFoundException {

    private final Long id;

    public NotaNoEncontradaException(Long id) {
        super(String.format("Nota de evaluación con ID %d no encontrada", id));
        this.id = id;
    }

    public NotaNoEncontradaException(Long matriculaId, Long criterioId) {
        super(String.format("Nota no encontrada para la matrícula %d y criterio %d", matriculaId, criterioId));
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
