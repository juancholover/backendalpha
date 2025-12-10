package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

import java.math.BigDecimal;

/**
 * Excepción lanzada cuando la nota está fuera del rango permitido.
 */
public class NotaFueraDeRangoException extends BusinessException {

    private final BigDecimal nota;
    private final BigDecimal minimo;
    private final BigDecimal maximo;

    public NotaFueraDeRangoException(BigDecimal nota, BigDecimal minimo, BigDecimal maximo) {
        super(String.format("La nota %.2f está fuera del rango permitido [%.2f - %.2f]",
                nota, minimo, maximo));
        this.nota = nota;
        this.minimo = minimo;
        this.maximo = maximo;
    }

    public NotaFueraDeRangoException(BigDecimal nota, int notaMaxima) {
        super(String.format("La nota %.2f no puede ser mayor a la nota máxima del criterio (%d)",
                nota, notaMaxima));
        this.nota = nota;
        this.minimo = BigDecimal.ZERO;
        this.maximo = new BigDecimal(notaMaxima);
    }

    public BigDecimal getNota() {
        return nota;
    }

    public BigDecimal getMinimo() {
        return minimo;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }
}
