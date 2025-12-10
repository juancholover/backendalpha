package upeu.edu.pe.core.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe una persona con el documento o email.
 */
public class PersonaDuplicadaException extends DuplicateResourceException {

    public PersonaDuplicadaException(String campo, String valor) {
        super("Persona", campo, valor);
    }
}
