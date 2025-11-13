package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import upeu.edu.pe.academic.application.dto.PersonaRequestDTO;
import upeu.edu.pe.academic.application.dto.PersonaResponseDTO;
import upeu.edu.pe.academic.application.mapper.PersonaMapper;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PersonaService {

    @Inject
    PersonaRepository personaRepository;

    @Inject
    PersonaMapper personaMapper;

    /**
     * Listar todas las personas activas
     */
    public List<PersonaResponseDTO> findAll() {
        return personaRepository.findAllActive()
                .stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar persona por ID
     */
    public PersonaResponseDTO findById(Long id) {
        Persona persona = personaRepository.findByIdOptional(id)
                .filter(p -> p.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
        
        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Buscar persona por número de documento
     */
    public PersonaResponseDTO findByNumeroDocumento(String numeroDocumento) {
        Persona persona = personaRepository.findByNumeroDocumento(numeroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "numeroDocumento", numeroDocumento));
        
        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Buscar persona por email
     */
    public PersonaResponseDTO findByEmail(String email) {
        Persona persona = personaRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "email", email));
        
        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Buscar personas por nombres (búsqueda parcial)
     */
    public List<PersonaResponseDTO> searchByNombres(String searchTerm) {
        return personaRepository.searchByNombres(searchTerm)
                .stream()
                .map(personaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear nueva persona
     */
    @Transactional
    public PersonaResponseDTO create(@Valid PersonaRequestDTO dto) {
        // Validar que no exista el número de documento
        if (personaRepository.existsByNumeroDocumento(dto.getNumeroDocumento())) {
            throw new DuplicateResourceException("Persona", "numeroDocumento", dto.getNumeroDocumento());
        }

        // Validar que no exista el email si se proporcionó
        if (dto.getEmail() != null && personaRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Persona", "email", dto.getEmail());
        }

        // Convertir DTO a entidad y persistir
        Persona persona = personaMapper.toEntity(dto);
        personaRepository.persist(persona);

        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Actualizar persona existente
     */
    @Transactional
    public PersonaResponseDTO update(Long id, @Valid PersonaRequestDTO dto) {
        // Buscar la persona
        Persona persona = personaRepository.findByIdOptional(id)
                .filter(p -> p.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));

        // Validar que no exista otro registro con el mismo número de documento
        if (personaRepository.existsByNumeroDocumentoAndIdNot(dto.getNumeroDocumento(), id)) {
            throw new DuplicateResourceException("Persona", "numeroDocumento", dto.getNumeroDocumento());
        }

        // Validar que no exista otro registro con el mismo email
        if (dto.getEmail() != null && personaRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException("Persona", "email", dto.getEmail());
        }

        // Actualizar la entidad
        personaMapper.updateEntityFromDto(dto, persona);
        personaRepository.persist(persona);

        return personaMapper.toResponseDTO(persona);
    }

    /**
     * Eliminar persona (borrado lógico)
     */
    @Transactional
    public void delete(Long id) {
        Persona persona = personaRepository.findByIdOptional(id)
                .filter(p -> p.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));

        persona.setActive(false);
        personaRepository.persist(persona);
    }

    /**
     * Verificar si existe una persona por ID
     */
    public boolean existsById(Long id) {
        return personaRepository.findByIdOptional(id)
                .map(Persona::getActive)
                .orElse(false);
    }

    /**
     * Obtener entidad Persona por ID (para uso interno de otros servicios)
     */
    public Persona getEntityById(Long id) {
        return personaRepository.findByIdOptional(id)
                .filter(Persona::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", "id", id));
    }
}
