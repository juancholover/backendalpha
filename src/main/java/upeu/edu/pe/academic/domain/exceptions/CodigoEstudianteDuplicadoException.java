package upeu.edu.pe.academic.domain.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un estudiante con un código duplicado.
 */
public class CodigoEstudianteDuplicadoException extends RuntimeException {
    
    private final String codigoEstudiante;
    
    public CodigoEstudianteDuplicadoException(String codigoEstudiante) {
        super(String.format("Ya existe un estudiante con el código: %s", codigoEstudiante));
        this.codigoEstudiante = codigoEstudiante;
    }
    
    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }
}
