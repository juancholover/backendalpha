package upeu.edu.pe.enrollment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando no hay cupo disponible en el curso.
 */
public class SinCupoDisponibleException extends BusinessException {

    private final Long cursoOfertadoId;

    public SinCupoDisponibleException(Long cursoOfertadoId) {
        super(String.format("El curso ofertado %d no tiene cupo disponible", cursoOfertadoId));
        this.cursoOfertadoId = cursoOfertadoId;
    }

    public Long getCursoOfertadoId() {
        return cursoOfertadoId;
    }
}
