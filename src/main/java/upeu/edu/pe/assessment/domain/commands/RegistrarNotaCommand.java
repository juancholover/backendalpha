package upeu.edu.pe.assessment.domain.commands;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Comando para registrar una nota de evaluación.
 */
public record RegistrarNotaCommand(
        Long matriculaId,
        Long criterioId,
        BigDecimal nota,
        String observacion,
        LocalDateTime fechaEvaluacion) {
    /**
     * Constructor compacto con validaciones básicas.
     */
    public RegistrarNotaCommand {
        if (matriculaId == null) {
            throw new IllegalArgumentException("El ID de matrícula es obligatorio");
        }
        if (criterioId == null) {
            throw new IllegalArgumentException("El ID del criterio es obligatorio");
        }
        if (nota == null) {
            throw new IllegalArgumentException("La nota es obligatoria");
        }
        if (nota.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La nota no puede ser negativa");
        }
    }

    /**
     * Constructor simplificado sin observación.
     */
    public static RegistrarNotaCommand of(Long matriculaId, Long criterioId, BigDecimal nota) {
        return new RegistrarNotaCommand(matriculaId, criterioId, nota, null, null);
    }
}
