package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción de dominio lanzada cuando se intenta operar con una universidad
 * cuya suscripción ha vencido.
 */
public class SubscripcionVencidaException extends BusinessException {

    private final Long universidadId;

    public SubscripcionVencidaException(Long universidadId) {
        super(String.format("La suscripción de la universidad ID %d ha vencido", universidadId));
        this.universidadId = universidadId;
    }

    public Long getUniversidadId() {
        return universidadId;
    }
}
