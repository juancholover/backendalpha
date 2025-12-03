package upeu.edu.pe.academic.domain.commands;

/**
 * Command para crear un nuevo sílabo
 */
public record CrearSilaboCommand(
    Long cursoId,
    Long universidadId,
    String anioAcademico,
    String competencias,
    String sumilla,
    String bibliografia,
    String metodologia,
    String recursosDidacticos
) {}
