package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.SilaboUnidadResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboUnidadMapper;
import upeu.edu.pe.curriculum.domain.entities.SilaboUnidad;
import upeu.edu.pe.curriculum.domain.repositories.SilaboUnidadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de Unidades de Sílabo.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class SilaboUnidadService {

    @Inject
    SilaboUnidadRepository silaboUnidadRepository;

    @Inject
    SilaboUnidadMapper silaboUnidadMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public SilaboUnidadResponseDTO buscarPorId(Long id) {
        SilaboUnidad unidad = silaboUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad no encontrada con ID: " + id));
        return silaboUnidadMapper.toResponseDTO(unidad);
    }

    public List<SilaboUnidadResponseDTO> listarPorSilabo(Long silaboId) {
        return silaboUnidadRepository.findBySilabo(silaboId).stream()
                .map(silaboUnidadMapper::toResponseDTOWithoutActividades)
                .collect(Collectors.toList());
    }

    public SilaboUnidadResponseDTO buscarPorSilaboYNumero(Long silaboId, Integer numeroUnidad) {
        SilaboUnidad unidad = silaboUnidadRepository.findBySilaboAndNumero(silaboId, numeroUnidad)
                .orElseThrow(() -> new NotFoundException(
                        "No se encontró la unidad " + numeroUnidad + " del sílabo " + silaboId));
        return silaboUnidadMapper.toResponseDTO(unidad);
    }

    public List<SilaboUnidadResponseDTO> buscarPorSemana(Long silaboId, Integer semana) {
        return silaboUnidadRepository.findBySemana(silaboId, semana).stream()
                .map(silaboUnidadMapper::toResponseDTOWithoutActividades)
                .collect(Collectors.toList());
    }

    public SilaboUnidad getEntityById(Long id) {
        return silaboUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad no encontrada con ID: " + id));
    }
}
