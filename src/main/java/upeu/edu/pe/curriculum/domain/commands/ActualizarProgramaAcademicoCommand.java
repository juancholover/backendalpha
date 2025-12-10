package upeu.edu.pe.curriculum.domain.commands;

/**
 * Comando para actualizar un programa académico.
 */
public record ActualizarProgramaAcademicoCommand(
        Long id,
        Long unidadOrganizativaId,
        String codigo,
        String nombre,
        String grado,
        String nivelAcademico,
        String modalidad,
        Integer duracionCiclos,
        String descripcion,
        String estado) {
    public ActualizarProgramaAcademicoCommand {
        if (id == null) {
            throw new IllegalArgumentException("El ID del programa es obligatorio");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
