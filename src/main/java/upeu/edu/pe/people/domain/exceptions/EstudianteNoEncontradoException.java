package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un estudiante.
 */
public class EstudianteNoEncontradoException extends NotFoundException {

    private final Long id;

    public EstudianteNoEncontradoException(Long id) {
        super(String.format("Estudiante con ID %d no encontrado", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
