package upeu.edu.pe.academic.domain.commands;


import java.time.LocalDate;
import java.util.Objects;


public record CrearEstudianteCommand(
    Long personaId,
    Long universidadId,
    Long programaAcademicoId,
    String codigoEstudiante,
    LocalDate fechaIngreso,
    Integer cicloActual,
    String modalidadIngreso,
    String tipoEstudiante
) {
   
    public CrearEstudianteCommand {
        Objects.requireNonNull(personaId, "ID de persona es requerido");
        Objects.requireNonNull(universidadId, "ID de universidad es requerido");
        Objects.requireNonNull(programaAcademicoId, "ID de programa académico es requerido");
        Objects.requireNonNull(codigoEstudiante, "Código de estudiante es requerido");
        Objects.requireNonNull(fechaIngreso, "Fecha de ingreso es requerida");
        Objects.requireNonNull(cicloActual, "Ciclo actual es requerido");
        
        if (codigoEstudiante.isBlank()) {
            throw new IllegalArgumentException("Código de estudiante no puede estar vacío");
        }
        
        if (cicloActual < 1) {
            throw new IllegalArgumentException("Ciclo actual debe ser mayor a 0");
        }
        
        if (fechaIngreso.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Fecha de ingreso no puede ser futura");
        }
    }
}
