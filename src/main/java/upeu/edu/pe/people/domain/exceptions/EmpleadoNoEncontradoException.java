package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Excepción lanzada cuando no se encuentra un empleado.
 */
public class EmpleadoNoEncontradoException extends NotFoundException {

    private final Long id;

    public EmpleadoNoEncontradoException(Long id) {
        super(String.format("Empleado con ID %d no encontrado", id));
        this.id = id;
    }

    public EmpleadoNoEncontradoException(String campo, String valor) {
        super(String.format("Empleado no encontrado con %s: %s", campo, valor));
        this.id = null;
    }

    public Long getId() {
        return id;
    }
}
