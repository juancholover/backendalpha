package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando el ciclo actual excede la duración del programa.
 */
public class CicloInvalidoException extends BusinessException {

    private final Integer cicloActual;
    private final Integer duracionPrograma;

    public CicloInvalidoException(Integer cicloActual, Integer duracionPrograma) {
        super(String.format(
                "El ciclo actual (%d) no puede ser mayor a la duración del programa (%d semestres)",
                cicloActual, duracionPrograma));
        this.cicloActual = cicloActual;
        this.duracionPrograma = duracionPrograma;
    }

    public Integer getCicloActual() {
        return cicloActual;
    }

    public Integer getDuracionPrograma() {
        return duracionPrograma;
    }
}
