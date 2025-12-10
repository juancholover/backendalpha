package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe una universidad con el RUC especificado.
 */
public class RucDuplicadoException extends DuplicateResourceException {

    private final String ruc;

    public RucDuplicadoException(String ruc) {
        super("Universidad", "RUC", ruc);
        this.ruc = ruc;
    }

    public String getRuc() {
        return ruc;
    }
}
