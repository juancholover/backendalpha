package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe un empleado con el mismo código o persona.
 */
public class EmpleadoDuplicadoException extends DuplicateResourceException {

    public EmpleadoDuplicadoException(String campo, String valor) {
        super("Empleado", campo, valor);
    }

    public EmpleadoDuplicadoException(Long personaId) {
        super(String.format("La persona con ID %d ya es empleado", personaId));
    }
}
