package upeu.edu.pe.finance.domain.commands;

import java.math.BigDecimal;

/**
 * Comando para aplicar un pago a una deuda.
 */
public record AplicarPagoADeudaCommand(
        Long pagoId,
        Long deudaId,
        BigDecimal montoAplicado) {
    public AplicarPagoADeudaCommand {
        if (pagoId == null) {
            throw new IllegalArgumentException("El ID del pago es obligatorio");
        }
        if (deudaId == null) {
            throw new IllegalArgumentException("El ID de la deuda es obligatorio");
        }
        if (montoAplicado == null || montoAplicado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a aplicar debe ser mayor a cero");
        }
    }
}
