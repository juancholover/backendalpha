package upeu.edu.pe.finance.domain.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Comando para registrar un nuevo pago.
 */
public record RegistrarPagoCommand(
        Long estudianteId,
        String numeroRecibo,
        BigDecimal montoPagado,
        LocalDateTime fechaPago,
        String metodoPago,
        String cajero,
        String observaciones) {
    public RegistrarPagoCommand {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
        if (numeroRecibo == null || numeroRecibo.isBlank()) {
            throw new IllegalArgumentException("El número de recibo es obligatorio");
        }
        if (montoPagado == null || montoPagado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto pagado debe ser mayor a cero");
        }
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }
    }
}
