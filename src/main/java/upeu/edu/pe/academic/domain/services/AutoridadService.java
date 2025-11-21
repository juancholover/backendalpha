package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.AutoridadDTO;
import upeu.edu.pe.academic.application.dto.CreateAutoridadDTO;
import upeu.edu.pe.academic.application.dto.UpdateAutoridadDTO;
import upeu.edu.pe.academic.application.mapper.AutoridadMapper;
import upeu.edu.pe.academic.domain.entities.Autoridad;
import upeu.edu.pe.academic.domain.repositories.AutoridadRepository;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;
import upeu.edu.pe.academic.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AutoridadService {

    @Inject
    AutoridadRepository autoridadRepository;

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    PersonaRepository personaRepository;

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Inject
    AutoridadMapper mapper;

    public List<AutoridadDTO> findActivasByUniversidadId(Long universidadId) {
        return autoridadRepository.findActivasByUniversidadId(universidadId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AutoridadDTO> findVigentesByUniversidadId(Long universidadId) {
        return autoridadRepository.findVigentesByUniversidadId(universidadId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AutoridadDTO> findByUniversidadId(Long universidadId) {
        return autoridadRepository.findByUniversidadId(universidadId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AutoridadDTO> findByPersonaId(Long personaId) {
        return autoridadRepository.findByPersonaId(personaId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public AutoridadDTO findById(Long id) {
        Autoridad entity = autoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autoridad no encontrada con ID: " + id));
        return mapper.toDTO(entity);
    }

    @Transactional
    public AutoridadDTO create(CreateAutoridadDTO dto) {
        // Validar que la universidad existe
        var universidad = universidadRepository.findByIdOptional(dto.getUniversidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + dto.getUniversidadId()));

        // Validar que la persona existe
        var persona = personaRepository.findByIdOptional(dto.getPersonaId())
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + dto.getPersonaId()));

        // Validar que el tipo de autoridad existe
        var tipoAutoridad = tipoAutoridadRepository.findByIdOptional(dto.getTipoAutoridadId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de autoridad no encontrado con ID: " + dto.getTipoAutoridadId()));

        // Validar que no haya otra autoridad activa del mismo tipo
        if (autoridadRepository.existsActivaByTipoAutoridadId(dto.getTipoAutoridadId(), dto.getUniversidadId())) {
            throw new IllegalArgumentException(
                "Ya existe una autoridad activa para el tipo: " + tipoAutoridad.getNombre() + 
                ". Debe desactivar la anterior antes de crear una nueva.");
        }

        Autoridad entity = mapper.toEntity(dto);
        entity.setUniversidad(universidad);
        entity.setPersona(persona);
        entity.setTipoAutoridad(tipoAutoridad);
        
        autoridadRepository.persist(entity);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public AutoridadDTO update(Long id, UpdateAutoridadDTO dto) {
        Autoridad entity = autoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autoridad no encontrada con ID: " + id));

        // Si se está cambiando la persona
        if (dto.getPersonaId() != null && !dto.getPersonaId().equals(entity.getPersona().getId())) {
            var persona = personaRepository.findByIdOptional(dto.getPersonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada con ID: " + dto.getPersonaId()));
            entity.setPersona(persona);
        }

        // Si se está cambiando el tipo de autoridad
        if (dto.getTipoAutoridadId() != null && !dto.getTipoAutoridadId().equals(entity.getTipoAutoridad().getId())) {
            var tipoAutoridad = tipoAutoridadRepository.findByIdOptional(dto.getTipoAutoridadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Tipo de autoridad no encontrado con ID: " + dto.getTipoAutoridadId()));
            
            // Validar que no haya otra autoridad activa del nuevo tipo
            if (dto.getActivo() != null && dto.getActivo()) {
                var autoridadExistente = autoridadRepository.findActivaByTipoAutoridadId(
                    dto.getTipoAutoridadId(), entity.getUniversidad().getId());
                
                if (autoridadExistente.isPresent() && !autoridadExistente.get().getId().equals(id)) {
                    throw new IllegalArgumentException(
                        "Ya existe otra autoridad activa para el tipo: " + tipoAutoridad.getNombre());
                }
            }
            
            entity.setTipoAutoridad(tipoAutoridad);
        }

        mapper.updateEntityFromDTO(dto, entity);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        Autoridad entity = autoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autoridad no encontrada con ID: " + id));
        
        autoridadRepository.delete(entity);
    }

    @Transactional
    public AutoridadDTO finalizarAutoridad(Long id, LocalDate fechaFin) {
        Autoridad entity = autoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autoridad no encontrada con ID: " + id));
        
        entity.setFechaFin(fechaFin);
        entity.setActivo(false);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public void desactivarAutoridadesAnteriores(Long tipoAutoridadId, Long universidadId) {
        var autoridadActual = autoridadRepository.findActivaByTipoAutoridadId(tipoAutoridadId, universidadId);
        autoridadActual.ifPresent(autoridad -> {
            autoridad.setFechaFin(LocalDate.now());
            autoridad.setActivo(false);
        });
    }
}
