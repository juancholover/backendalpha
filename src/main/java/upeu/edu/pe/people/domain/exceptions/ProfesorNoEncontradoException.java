package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un profesor.
 */
public class ProfesorNoEncontradoException extends NotFoundException {

    private final Long id;

    public ProfesorNoEncontradoException(Long id) {
        super(String.format("Profesor con ID %d no encontrado", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
