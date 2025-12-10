package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra una persona.
 */
public class PersonaNoEncontradaException extends NotFoundException {

    private final Long id;

    public PersonaNoEncontradaException(Long id) {
        super(String.format("Persona con ID %d no encontrada", id));
        this.id = id;
    }

    public PersonaNoEncontradaException(String campo, String valor) {
        super(String.format("Persona no encontrada con %s: %s", campo, valor));
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
