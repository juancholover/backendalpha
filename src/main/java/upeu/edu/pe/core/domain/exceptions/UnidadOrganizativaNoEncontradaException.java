package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra una unidad organizativa.
 */
public class UnidadOrganizativaNoEncontradaException extends NotFoundException {

    private final Long id;

    public UnidadOrganizativaNoEncontradaException(Long id) {
        super(String.format("Unidad organizativa con ID %d no encontrada", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
