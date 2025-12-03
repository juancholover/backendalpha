package upeu.edu.pe.academic.domain.commands;

/**
 * Command para crear una nueva modalidad
 */
public record CrearModalidadCommand(
    Long universidadId,
    String codigo,
    String nombre,
    String descripcion,
    Boolean requiereAula,
    Boolean requierePlataforma,
    Integer porcentajePresencialidad,
    String colorHex
) {}
