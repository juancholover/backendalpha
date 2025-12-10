package upeu.edu.pe.core.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.core.application.dto.PersonaResponseDTO;
import upeu.edu.pe.core.application.mapper.PersonaMapper;
import upeu.edu.pe.core.domain.entities.Persona;
import upeu.edu.pe.core.domain.repositories.PersonaRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de personas.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA (crear, actualizar, eliminar) se manejan en Use
 * Cases.
 */
@ApplicationScoped
public class PersonaService {

    @Inject
    PersonaRepository personaRepository;

    @Inject
    PersonaMapper personaMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Listar todas las personas activas.
     */
    public List<PersonaResponseDTO> findAll() {
        return personaRepository.findAllActive()
                .stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar persona por ID.
     */
    public PersonaResponseDTO findById(Long id) {
        Persona persona = personaRepository.findByIdOptional(id)
                .filter(Persona::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));

        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Buscar persona por número de documento.
     */
    public PersonaResponseDTO findByNumeroDocumento(String numeroDocumento) {
        Persona persona = personaRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "numeroDocumento", numeroDocumento));

        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Buscar persona por email.
     */
    public PersonaResponseDTO findByEmail(String email) {
        Persona persona = personaRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "email", email));

        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Buscar personas por nombres (búsqueda parcial).
     */
    public List<PersonaResponseDTO> searchByNombres(String searchTerm) {
        return personaRepository.searchByNombres(searchTerm)
                .stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Verificar si existe una persona por ID.
     */
    public boolean existsById(Long id) {
        return personaRepository.findByIdOptional(id)
                .map(Persona::getActive)
                .orElse(false);
    }

    /**
     * Obtener entidad Persona por ID (para uso interno de otros servicios).
     */
    public Persona getEntityById(Long id) {
        return personaRepository.findByIdOptional(id)
                .filter(Persona::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
    }
}
