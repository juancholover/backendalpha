package upeu.edu.pe.curriculum.domain.commands;

/**
 * Comando para crear un programa académico.
 */
public record CrearProgramaAcademicoCommand(
        Long unidadOrganizativaId,
        String codigo,
        String nombre,
        String grado,
        String nivelAcademico,
        String modalidad,
        Integer duracionCiclos,
        String descripcion) {
    public CrearProgramaAcademicoCommand {
        if (unidadOrganizativaId == null) {
            throw new IllegalArgumentException("La unidad organizativa es obligatoria");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
    }
}
