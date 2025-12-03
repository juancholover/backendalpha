package upeu.edu.pe.academic.domain.commands;

/**
 * Command para actualizar una modalidad existente
 */
public record ActualizarModalidadCommand(
    Long id,
    String codigo,
    String nombre,
    String descripcion,
    Boolean requiereAula,
    Boolean requierePlataforma,
    Integer porcentajePresencialidad,
    String colorHex
) {}
