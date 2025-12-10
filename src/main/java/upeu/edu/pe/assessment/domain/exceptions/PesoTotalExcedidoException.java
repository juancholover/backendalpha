package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.BusinessException;

/**
 * Excepción lanzada cuando el peso total de los criterios excede el 100%.
 */
public class PesoTotalExcedidoException extends BusinessException {

    private final Integer pesoActual;
    private final Integer pesoNuevo;
    private final Integer total;

    public PesoTotalExcedidoException(Integer pesoActual, Integer pesoNuevo) {
        super(String.format(
                "El peso total no puede exceder 100%%. Peso actual: %d%%, intentando agregar: %d%% (total: %d%%)",
                pesoActual, pesoNuevo, pesoActual + pesoNuevo));
        this.pesoActual = pesoActual;
        this.pesoNuevo = pesoNuevo;
        this.total = pesoActual + pesoNuevo;
    }

    public Integer getPesoActual() {
        return pesoActual;
    }

    public Integer getPesoNuevo() {
        return pesoNuevo;
    }

    public Integer getTotal() {
        return total;
    }
}
