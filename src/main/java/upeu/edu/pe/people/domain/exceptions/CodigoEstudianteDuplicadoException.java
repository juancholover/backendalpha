package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando se intenta crear un estudiante con un código
 * duplicado.
 */
public class CodigoEstudianteDuplicadoException extends DuplicateResourceException {

    private final String codigoEstudiante;

    public CodigoEstudianteDuplicadoException(String codigoEstudiante) {
        super("Estudiante", "código", codigoEstudiante);
        this.codigoEstudiante = codigoEstudiante;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }
}
