package upeu.edu.pe.enrollment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando el estudiante ya está matriculado en la sección.
 */
public class MatriculaDuplicadaException extends DuplicateResourceException {

    private final Long estudianteId;
    private final Long seccionId;

    public MatriculaDuplicadaException(Long estudianteId, Long seccionId) {
        super(String.format("El estudiante %d ya está matriculado en la sección %d", estudianteId, seccionId));
        this.estudianteId = estudianteId;
        this.seccionId = seccionId;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public Long getSeccionId() {
        return seccionId;
    }
}
