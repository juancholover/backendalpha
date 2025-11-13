package upeu.edu.pe.academic.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para Empleado
 */
public record EmpleadoResponseDTO(
        Long id,
        Long personaId,
        String nombreCompleto,
        Long unidadOrganizativaId,
        String unidadOrganizativaNombre,
        String codigoEmpleado,
        LocalDate fechaIngreso,
        LocalDate fechaCese,
        String cargo,
        String tipoContrato,
        String regimenLaboral,
        Double salario,
        String estadoLaboral,
        Integer aniosServicio,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
