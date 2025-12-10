package upeu.edu.pe.enrollment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando se excede el límite de créditos por ciclo.
 */
public class LimiteCreditosExcedidoException extends BusinessException {

    private final Integer creditosActuales;
    private final Integer creditosCurso;
    private final Integer maximo;

    public LimiteCreditosExcedidoException(Integer creditosActuales, Integer creditosCurso, Integer maximo) {
        super(String.format(
                "Se excedería el límite de créditos. Actual: %d, Curso: %d, Máximo: %d",
                creditosActuales, creditosCurso, maximo));
        this.creditosActuales = creditosActuales;
        this.creditosCurso = creditosCurso;
        this.maximo = maximo;
    }

    public Integer getCreditosActuales() {
        return creditosActuales;
    }

    public Integer getCreditosCurso() {
        return creditosCurso;
    }

    public Integer getMaximo() {
        return maximo;
    }
}
