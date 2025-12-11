package upeu.edu.pe.curriculum.domain.commands;

import java.time.LocalDate;

/**
 * Comando para crear un plan académico.
 */
public record CrearPlanAcademicoCommand(
        Long programaAcademicoId,
        String codigo,
        String nombre,
        LocalDate fechaVigenciaInicio,
        LocalDate fechaVigenciaFin,
        Integer creditosTotales,
        Integer duracionSemestres,
        String estado) {
    public CrearPlanAcademicoCommand {
        if (programaAcademicoId == null) {
            throw new IllegalArgumentException("El ID del programa académico es obligatorio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
