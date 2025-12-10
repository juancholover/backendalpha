package upeu.edu.pe.core.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar (soft delete) una persona.
 * 
 * Reglas de negocio:
 * - La persona debe existir
 */
@ApplicationScoped
public class EliminarPersonaUseCase {

    @Inject
    PersonaRepository personaRepository;

    @Transactional
    public void execute(Long personaId) {

        // 1. Buscar persona
        Persona persona = personaRepository.findByIdOptional(personaId)
                .filter(Persona::getActive)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada con ID: " + personaId));

        // 2. Soft delete
        persona.setActive(false);
        personaRepository.persist(persona);
    }
}
