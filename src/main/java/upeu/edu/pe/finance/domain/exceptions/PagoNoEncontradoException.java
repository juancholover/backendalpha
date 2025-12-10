package upeu.edu.pe.finance.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un pago.
 */
public class PagoNoEncontradoException extends NotFoundException {

    private final Long id;

    public PagoNoEncontradoException(Long id) {
        super(String.format("Pago con ID %d no encontrado", id));
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
