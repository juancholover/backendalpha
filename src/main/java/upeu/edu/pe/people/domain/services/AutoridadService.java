package upeu.edu.pe.people.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.people.application.dto.AutoridadDTO;
import upeu.edu.pe.people.application.mapper.AutoridadMapper;
import upeu.edu.pe.people.domain.entities.Autoridad;
import upeu.edu.pe.people.domain.repositories.AutoridadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de autoridades.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class AutoridadService {

    @Inject
    AutoridadRepository autoridadRepository;

    @Inject
    AutoridadMapper mapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<AutoridadDTO> findActivasByUniversidadId(Long universidadId) {
        return autoridadRepository.findActivas()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AutoridadDTO> findVigentesByUniversidadId(Long universidadId) {
        return autoridadRepository.findVigentes()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<AutoridadDTO> findByUniversidadId(Long universidadId) {
        return autoridadRepository.findAllWithDetails()
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
                .orElseThrow(() -> new NotFoundException("Autoridad no encontrada con ID: " + id));
        return mapper.toDTO(entity);
    }

    public Autoridad getEntityById(Long id) {
        return autoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Autoridad no encontrada con ID: " + id));
    }
}
