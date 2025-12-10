package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción de dominio lanzada cuando no se encuentra una universidad.
 */
public class UniversidadNoEncontradaException extends NotFoundException {

    private final Long id;

    public UniversidadNoEncontradaException(Long id) {
        super(String.format("Universidad con ID %d no encontrada", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
