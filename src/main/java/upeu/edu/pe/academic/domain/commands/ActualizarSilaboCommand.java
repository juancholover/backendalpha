package upeu.edu.pe.academic.domain.commands;

/**
 * Command para actualizar un sílabo existente
 */
public record ActualizarSilaboCommand(
    Long id,
    String competencias,
    String sumilla,
    String bibliografia,
    String metodologia,
    String recursosDidacticos,
    String observaciones
) {}
