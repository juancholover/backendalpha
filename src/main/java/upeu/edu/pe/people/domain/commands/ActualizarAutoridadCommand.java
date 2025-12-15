package upeu.edu.pe.people.domain.commands;

import java.time.LocalDate;

/**
 * Comando para actualizar una autoridad.
 */
public record ActualizarAutoridadCommand(
        Long id,
        Long personaId,
        Long tipoAutoridadId,
        Long unidadOrganizativaId,
        Long programaAcademicoId,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        Boolean esVigente,
        String resolucionDesignacion,
        String observaciones) {
    public ActualizarAutoridadCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID de autoridad es obligatorio");
        }
    }
}
