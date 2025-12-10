package upeu.edu.pe.curriculum.domain.commands;

/**
 * Command para crear un nuevo sílabo
 */
public record CrearSilaboCommand(
    Long cursoId,
    String anioAcademico,
    String competencias,
    String sumilla,
    String bibliografia,
    String metodologia,
    String recursosDidacticos
) {}
