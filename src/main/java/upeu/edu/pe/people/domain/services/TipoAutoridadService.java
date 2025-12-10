package upeu.edu.pe.people.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.people.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.people.application.dto.UpdateTipoAutoridadDTO;
import upeu.edu.pe.people.application.mapper.TipoAutoridadMapper;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.core.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TipoAutoridadService {

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    upeu.edu.pe.people.domain.repositories.AutoridadRepository autoridadRepository;

    @Inject
    TipoAutoridadMapper mapper;

    public List<TipoAutoridadDTO> findByUniversidadId(Long universidadId) {
        return tipoAutoridadRepository.findAllOrderByNivel()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public TipoAutoridadDTO findById(Long id) {
        TipoAutoridad entity = tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de autoridad no encontrado con ID: " + id));
        return mapper.toDTO(entity);
    }

    @Transactional
    public TipoAutoridadDTO create(CreateTipoAutoridadDTO dto) {
        // Validar que no exista otro tipo de autoridad con el mismo nombre
        if (tipoAutoridadRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un tipo de autoridad con el nombre: " + dto.getNombre());
        }

        TipoAutoridad entity = mapper.toEntity(dto);
        tipoAutoridadRepository.persist(entity);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public TipoAutoridadDTO update(Long id, UpdateTipoAutoridadDTO dto) {
        TipoAutoridad entity = tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de autoridad no encontrado con ID: " + id));

        mapper.updateEntityFromDTO(dto, entity);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        TipoAutoridad entity = tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de autoridad no encontrado con ID: " + id));

        // Validar que no haya autoridades activas asociadas antes de eliminar
        // Nota: Se necesitaría universidadId para esta validación
        // if (autoridadRepository.existsActivaByTipoAutoridadId(id, universidadId)) { ... }
        
        tipoAutoridadRepository.delete(entity);
    }

    public TipoAutoridadDTO findMaximaAutoridad(Long universidadId) {
        return tipoAutoridadRepository.findMaximaAutoridad()
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No se encontró ningún tipo de autoridad con máxima jerarquía"));
    }
}

