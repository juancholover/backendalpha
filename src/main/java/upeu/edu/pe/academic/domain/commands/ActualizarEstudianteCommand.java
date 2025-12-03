package upeu.edu.pe.academic.domain.commands;

import java.math.BigDecimal;
import java.util.Objects;


public record ActualizarEstudianteCommand(
    Long id,
    Long programaAcademicoId,
    String codigoEstudiante,
    Integer cicloActual,
    Integer creditosAprobados,
    Integer creditosCursando,
    Integer creditosObligatoriosAprobados,
    Integer creditosElectivosAprobados,
    BigDecimal promedioPonderado,
    String estadoAcademico,
    String tipoEstudiante
) {
    public ActualizarEstudianteCommand {
        Objects.requireNonNull(id, "ID es requerido para actualización");
        
        if (cicloActual != null && cicloActual < 1) {
            throw new IllegalArgumentException("Ciclo actual debe ser mayor a 0");
        }
        
        if (creditosAprobados != null && creditosAprobados < 0) {
            throw new IllegalArgumentException("Créditos aprobados no pueden ser negativos");
        }
        
        if (promedioPonderado != null && 
            (promedioPonderado.compareTo(BigDecimal.ZERO) < 0 || 
             promedioPonderado.compareTo(new BigDecimal("20.00")) > 0)) {
            throw new IllegalArgumentException("Promedio debe estar entre 0 y 20");
        }
    }
}
