package upeu.edu.pe.people.domain.commands;

import java.time.LocalDate;

/**
 * Comando para crear un empleado.
 */
public record CrearEmpleadoCommand(
        Long personaId,
        Long unidadOrganizativaId,
        String codigoEmpleado,
        String cargo,
        String tipoContrato,
        LocalDate fechaIngreso,
        String estadoLaboral) {
    public CrearEmpleadoCommand {
        if (personaId == null) {
            throw new IllegalArgumentException("El ID de persona es obligatorio");
        }
        if (codigoEmpleado == null || codigoEmpleado.isBlank()) {
            throw new IllegalArgumentException("El código de empleado es obligatorio");
        }
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria");
        }
    }
}
