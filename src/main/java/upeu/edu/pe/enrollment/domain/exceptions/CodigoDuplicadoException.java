package upeu.edu.pe.enrollment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe un registro con el código especificado.
 */
public class CodigoDuplicadoException extends DuplicateResourceException {

    private final String codigo;

    public CodigoDuplicadoException(String codigo) {
        super(String.format("Ya existe un registro con el código: %s", codigo));
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
