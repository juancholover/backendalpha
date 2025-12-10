package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando una unidad organizativa tiene unidades hijas y no
 * puede eliminarse.
 */
public class UnidadConHijasException extends BusinessException {

    private final Long unidadId;

    public UnidadConHijasException(Long unidadId) {
        super(String.format("No se puede eliminar la unidad organizativa ID %d porque tiene unidades hijas asociadas",
                unidadId));
        this.unidadId = unidadId;
    }

    public Long getUnidadId() {
        return unidadId;
    }
}
