package upeu.edu.pe.enrollment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra una matrícula.
 */
public class MatriculaNoEncontradaException extends NotFoundException {

    private final Long id;

    public MatriculaNoEncontradaException(Long id) {
        super(String.format("Matrícula con ID %d no encontrada", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
