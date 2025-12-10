package upeu.edu.pe.curriculum.domain.commands;

import java.math.BigDecimal;

/**
 * Command para agregar una actividad a una unidad de sílabo
 */
public record AgregarActividadUnidadCommand(
    Long unidadId,
    String tipo,
    String nombre,
    String descripcion,
    BigDecimal ponderacion,
    Integer semanaProgramada,
    String instrumentoEvaluacion,
    String indicadores,
    String criteriosEvaluacion
) {}
