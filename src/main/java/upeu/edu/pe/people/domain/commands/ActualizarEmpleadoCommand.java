package upeu.edu.pe.people.domain.commands;

import java.time.LocalDate;

/**
 * Comando para actualizar un empleado.
 */
public record ActualizarEmpleadoCommand(
        Long id,
        Long unidadOrganizativaId,
        String codigoEmpleado,
        String cargo,
        String tipoContrato,
        LocalDate fechaIngreso,
        LocalDate fechaCese,
        String estadoLaboral) {
    public ActualizarEmpleadoCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID del empleado es obligatorio");
        }
        if (codigoEmpleado == null || codigoEmpleado.isBlank()) {
            throw new IllegalArgumentException("El código de empleado es obligatorio");
        }
    }
}
