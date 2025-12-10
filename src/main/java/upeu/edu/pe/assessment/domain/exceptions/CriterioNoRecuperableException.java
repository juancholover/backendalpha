package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando se intenta registrar recuperación en un criterio que
 * no lo permite.
 */
public class CriterioNoRecuperableException extends BusinessException {

    private final Long criterioId;
    private final String criterioNombre;

    public CriterioNoRecuperableException(Long criterioId, String criterioNombre) {
        super(String.format("El criterio '%s' (ID: %d) no permite recuperación", criterioNombre, criterioId));
        this.criterioId = criterioId;
        this.criterioNombre = criterioNombre;
    }

    public CriterioNoRecuperableException(String criterioNombre) {
        super(String.format("El criterio '%s' no permite recuperación", criterioNombre));
        this.criterioId = null;
        this.criterioNombre = criterioNombre;
    }

    public Long getCriterioId() {
        return criterioId;
    }

    public String getCriterioNombre() {
        return criterioNombre;
    }
}
