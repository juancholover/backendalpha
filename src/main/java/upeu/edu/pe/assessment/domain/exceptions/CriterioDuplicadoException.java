package upeu.edu.pe.assessment.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando ya existe un criterio con el mismo nombre en la
 * sección.
 */
public class CriterioDuplicadoException extends DuplicateResourceException {

    private final String nombre;
    private final Long seccionId;

    public CriterioDuplicadoException(String nombre, Long seccionId) {
        super(String.format("Ya existe un criterio con el nombre '%s' en la sección %d", nombre, seccionId));
        this.nombre = nombre;
        this.seccionId = seccionId;
    }

    public CriterioDuplicadoException(String nombre) {
        super(String.format("Ya existe un criterio con el nombre '%s' en esta sección", nombre));
        this.nombre = nombre;
        this.seccionId = null;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getSeccionId() {
        return seccionId;
    }
}
