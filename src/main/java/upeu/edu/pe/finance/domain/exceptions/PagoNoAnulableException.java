package upeu.edu.pe.finance.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando no se puede anular un pago.
 */
public class PagoNoAnulableException extends BusinessException {

    public PagoNoAnulableException(String motivo) {
        super("No se puede anular el pago: " + motivo);
    }
}
