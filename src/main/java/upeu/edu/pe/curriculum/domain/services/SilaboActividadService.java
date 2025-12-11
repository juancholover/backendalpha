package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.SilaboActividadResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.SilaboActividadMapper;
import upeu.edu.pe.curriculum.domain.entities.SilaboActividad;
import upeu.edu.pe.curriculum.domain.repositories.SilaboActividadRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de Actividades de Sílabo.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class SilaboActividadService {

    @Inject
    SilaboActividadRepository silaboActividadRepository;

    @Inject
    SilaboActividadMapper silaboActividadMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public SilaboActividadResponseDTO buscarPorId(Long id) {
        SilaboActividad actividad = silaboActividadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Actividad no encontrada con ID: " + id));
        return silaboActividadMapper.toResponseDTO(actividad);
    }

    public List<SilaboActividadResponseDTO> listarPorUnidad(Long unidadId) {
        return silaboActividadRepository.findByUnidad(unidadId).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SilaboActividadResponseDTO> listarPorUnidadYTipo(Long unidadId, String tipo) {
        return silaboActividadRepository.findByUnidadAndTipo(unidadId, tipo).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SilaboActividadResponseDTO> listarSumativasPorUnidad(Long unidadId) {
        return silaboActividadRepository.findSumativasByUnidad(unidadId).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SilaboActividadResponseDTO> listarFormativasPorUnidad(Long unidadId) {
        return silaboActividadRepository.findFormativasByUnidad(unidadId).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<SilaboActividadResponseDTO> buscarPorSemana(Long silaboId, Integer semana) {
        return silaboActividadRepository.findBySemana(silaboId, semana).stream()
                .map(silaboActividadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal calcularPonderacionTotalUnidad(Long unidadId) {
        return silaboActividadRepository.sumPonderacionesByUnidad(unidadId);
    }

    public BigDecimal calcularPonderacionTotalSilabo(Long silaboId) {
        return silaboActividadRepository.sumPonderacionesBySilabo(silaboId);
    }

    public SilaboActividad getEntityById(Long id) {
        return silaboActividadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Actividad no encontrada con ID: " + id));
    }
}
