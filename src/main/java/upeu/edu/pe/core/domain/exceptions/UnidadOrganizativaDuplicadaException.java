package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe una unidad organizativa con el mismo
 * código o nombre.
 */
public class UnidadOrganizativaDuplicadaException extends DuplicateResourceException {

    public UnidadOrganizativaDuplicadaException(String campo, String valor) {
        super("Unidad Organizativa", campo, valor);
    }

    public UnidadOrganizativaDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
