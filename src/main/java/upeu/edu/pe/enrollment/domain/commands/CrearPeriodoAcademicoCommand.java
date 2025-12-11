package upeu.edu.pe.enrollment.domain.commands;

import java.time.LocalDate;

/**
 * Comando para crear un periodo académico.
 */
public record CrearPeriodoAcademicoCommand(
        String codigoPeriodo,
        String nombre,
        Integer anio,
        String tipoPeriodo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        LocalDate fechaInicioMatricula,
        LocalDate fechaFinMatricula,
        Boolean esActual) {
    public CrearPeriodoAcademicoCommand {
        if (codigoPeriodo == null || codigoPeriodo.isBlank()) {
            throw new IllegalArgumentException("El código del periodo es obligatorio");
        }
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
    }
}
