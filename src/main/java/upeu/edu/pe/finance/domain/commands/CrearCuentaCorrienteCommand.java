package upeu.edu.pe.finance.domain.commands;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Comando para crear una cuenta corriente de alumno (deuda).
 */
public record CrearCuentaCorrienteCommand(
        Long estudianteId,
        String concepto,
        BigDecimal monto,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        String tipoCargo,
        String observaciones) {
    public CrearCuentaCorrienteCommand {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es obligatorio");
        }
        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException("El concepto es obligatorio");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
    }
}
