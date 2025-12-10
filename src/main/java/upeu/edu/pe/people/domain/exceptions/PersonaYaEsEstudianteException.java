package upeu.edu.pe.people.domain.exceptions;

import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Excepción lanzada cuando una persona ya está registrada como estudiante.
 */
public class PersonaYaEsEstudianteException extends DuplicateResourceException {

    private final Long personaId;

    public PersonaYaEsEstudianteException(Long personaId) {
        super(String.format("La persona con ID %d ya está registrada como estudiante", personaId));
        this.personaId = personaId;
    }

    public Long getPersonaId() {
        return personaId;
    }
}
