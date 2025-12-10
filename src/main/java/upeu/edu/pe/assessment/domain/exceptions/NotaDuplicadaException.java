package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe una nota registrada para la misma
 * matrícula y criterio.
 */
public class NotaDuplicadaException extends DuplicateResourceException {

    private final Long matriculaId;
    private final Long criterioId;

    public NotaDuplicadaException(Long matriculaId, Long criterioId) {
        super(String.format(
                "Ya existe una nota registrada para la matrícula %d en el criterio %d. Use la opción de actualizar.",
                matriculaId, criterioId));
        this.matriculaId = matriculaId;
        this.criterioId = criterioId;
    }

    public NotaDuplicadaException(Long matriculaId, String criterioNombre) {
        super(String.format(
                "Ya existe una nota registrada para el estudiante en el criterio '%s'. Use la opción de actualizar.",
                criterioNombre));
        this.matriculaId = matriculaId;
        this.criterioId = null;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public Long getCriterioId() {
        return criterioId;
    }
}
