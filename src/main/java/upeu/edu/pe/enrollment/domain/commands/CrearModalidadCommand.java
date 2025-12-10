package upeu.edu.pe.enrollment.domain.commands;

/**
 * Command para crear una nueva modalidad
 */
public record CrearModalidadCommand(
    String codigo,
    String nombre,
    String descripcion,
    Boolean requiereAula,
    Boolean requierePlataforma,
    Integer porcentajePresencialidad,
    String colorHex
) {}
