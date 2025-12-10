package upeu.edu.pe.assessment.domain.commands;

import java.math.BigDecimal;

/**
 * Comando para registrar una nota de recuperación.
 */
public record RegistrarNotaRecuperacionCommand(
        Long notaId,
        BigDecimal notaRecuperacion,
        String observacion) {
    /**
     * Constructor compacto con validaciones básicas.
     */
    public RegistrarNotaRecuperacionCommand {
        if (notaId == null) {
            throw new IllegalArgumentException("El ID de la nota es obligatorio");
        }
        if (notaRecuperacion == null) {
            throw new IllegalArgumentException("La nota de recuperación es obligatoria");
        }
        if (notaRecuperacion.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La nota de recuperación no puede ser negativa");
        }
    }

    /**
     * Constructor simplificado.
     */
    public static RegistrarNotaRecuperacionCommand of(Long notaId, BigDecimal notaRecuperacion) {
        return new RegistrarNotaRecuperacionCommand(notaId, notaRecuperacion, null);
    }
}
