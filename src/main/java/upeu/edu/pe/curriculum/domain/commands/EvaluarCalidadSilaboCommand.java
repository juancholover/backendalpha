package upeu.edu.pe.curriculum.domain.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command para evaluar la calidad de un sílabo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluarCalidadSilaboCommand {
    private Long silaboId;
    private String evaluadoPor;
    private Boolean forzarReevaluacion; // true para reevaluar aunque ya tenga evaluación aprobada
}
