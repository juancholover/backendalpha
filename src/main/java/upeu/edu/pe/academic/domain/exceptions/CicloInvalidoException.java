package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción lanzada cuando el ciclo actual excede la duración del programa.
 */
public class CicloInvalidoException extends RuntimeException {
    
    private final Integer cicloActual;
    private final Integer duracionPrograma;
    
    public CicloInvalidoException(Integer cicloActual, Integer duracionPrograma) {
        super(String.format(
            "El ciclo actual (%d) no puede ser mayor a la duración del programa (%d semestres)",
            cicloActual, duracionPrograma
        ));
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
