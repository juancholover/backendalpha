package upeu.edu.pe.curriculum.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un sílabo.
 */
public class SilaboNoEncontradoException extends NotFoundException {

    private final Long id;

    public SilaboNoEncontradoException(Long id) {
        super(String.format("Sílabo con ID %d no encontrado", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
