package upeu.edu.pe.people.domain.commands;

import java.time.LocalDate;

/**
 * Comando para crear una autoridad.
 */
public record CrearAutoridadCommand(
        Long personaId,
        Long tipoAutoridadId,
        Long unidadOrganizativaId,
        Long programaAcademicoId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String resolucionDesignacion,
        String observaciones) {
    public CrearAutoridadCommand {
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de persona es obligatorio");
        }
        if (tipoAutoridadId == null) {
            throw new IllegalArgumentException("El ID de tipo de autoridad es obligatorio");
        }
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
    }
}
