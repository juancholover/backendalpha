package upeu.edu.pe.academic.domain.commands;

/**
 * Command para agregar una unidad a un sílabo
 */
public record AgregarUnidadSilaboCommand(
    Long silaboId,
    Integer numeroUnidad,
    String titulo,
    Integer semanaInicio,
    Integer semanaFin,
    String contenidos,
    String logroAprendizaje
) {}
