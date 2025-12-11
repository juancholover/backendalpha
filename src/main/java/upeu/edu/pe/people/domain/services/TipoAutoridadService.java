package upeu.edu.pe.people.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.people.application.dto.TipoAutoridadDTO;
import upeu.edu.pe.people.application.mapper.TipoAutoridadMapper;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;
import upeu.edu.pe.people.domain.repositories.TipoAutoridadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de tipos de autoridad.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class TipoAutoridadService {

    @Inject
    TipoAutoridadRepository tipoAutoridadRepository;

    @Inject
    TipoAutoridadMapper mapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<TipoAutoridadDTO> findByUniversidadId(Long universidadId) {
        return tipoAutoridadRepository.findAllOrderByNivel()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public TipoAutoridadDTO findById(Long id) {
        TipoAutoridad entity = tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de autoridad no encontrado con ID: " + id));
        return mapper.toDTO(entity);
    }

    public TipoAutoridadDTO findMaximaAutoridad(Long universidadId) {
        return tipoAutoridadRepository.findMaximaAutoridad()
                .map(mapper::toDTO)
                .orElseThrow(
                        () -> new NotFoundException("No se encontró ningún tipo de autoridad con máxima jerarquía"));
    }

    public TipoAutoridad getEntityById(Long id) {
        return tipoAutoridadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Tipo de autoridad no encontrado con ID: " + id));
    }
}
