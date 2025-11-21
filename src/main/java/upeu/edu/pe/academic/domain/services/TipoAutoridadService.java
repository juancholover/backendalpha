package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.CreateTipoAutoridadDTO;
import upeu.edu.pe.academic.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.academic.application.dto.UpdateTipoAutoridadDTO;
import upeu.edu.pe.academic.application.mapper.TipoAutoridadMapper;
import upeu.edu.pe.academic.domain.entities.TipoAutoridad;
import upeu.edu.pe.academic.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
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
    upeu.edu.pe.academic.domain.repositories.AutoridadRepository autoridadRepository;

    @Inject
    TipoAutoridadMapper mapper;

    public List<TipoAutoridadDTO> findByUniversidadId(Long universidadId) {
        return tipoAutoridadRepository.findByUniversidadIdOrderByNivel(universidadId)
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
        // Validar que la universidad existe
        var universidad = universidadRepository.findByIdOptional(dto.getUniversidadId())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + dto.getUniversidadId()));

        // Validar que no exista otro tipo de autoridad con el mismo nombre
        if (tipoAutoridadRepository.existsByNombreAndUniversidadId(dto.getNombre(), dto.getUniversidadId())) {
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

        // Validar nombre único si se está cambiando
        if (dto.getNombre() != null && !dto.getNombre().equals(entity.getNombre())) {
            if (tipoAutoridadRepository.existsByNombreAndUniversidadIdAndIdNot(
                    dto.getNombre(), entity.getUniversidad().getId(), id)) {
                throw new IllegalArgumentException("Ya existe otro tipo de autoridad con el nombre: " + dto.getNombre());
            }
        }

        mapper.updateEntityFromDTO(dto, entity);
        
        return mapper.toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        TipoAutoridad entity = tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de autoridad no encontrado con ID: " + id));

        // Validar que no haya autoridades activas asociadas antes de eliminar
        if (autoridadRepository.existsActivaByTipoAutoridadId(id, entity.getUniversidad().getId())) {
            throw new IllegalStateException(
                "No se puede eliminar el tipo de autoridad '" + entity.getNombre() + 
                "' porque tiene autoridades activas asociadas. Debe desactivar o eliminar las autoridades primero."
            );
        }
        
        tipoAutoridadRepository.delete(entity);
    }

    public TipoAutoridadDTO findMaximaAutoridad(Long universidadId) {
        return tipoAutoridadRepository.findMaximaAutoridadByUniversidadId(universidadId)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No se encontró ningún tipo de autoridad para la universidad ID: " + universidadId));
    }
}
