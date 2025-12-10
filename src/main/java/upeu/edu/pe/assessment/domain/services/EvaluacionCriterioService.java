package upeu.edu.pe.assessment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.assessment.application.dto.EvaluacionCriterioResponseDTO;
import upeu.edu.pe.assessment.application.mapper.EvaluacionCriterioMapper;
import upeu.edu.pe.assessment.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de dominio para consultas básicas de criterios de evaluación.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA (crear, actualizar, eliminar) se manejan en Use
 * Cases.
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class EvaluacionCriterioService {

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    @Inject
    EvaluacionCriterioMapper criterioMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Busca todos los criterios de una sección.
     */
    public List<EvaluacionCriterioResponseDTO> findBySeccion(Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findBySeccion(seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    /**
     * Busca criterios activos por sección.
     */
    public List<EvaluacionCriterioResponseDTO> findActivosBySeccion(Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findActivosBySeccion(seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    /**
     * Busca criterios por tipo de evaluación.
     */
    public List<EvaluacionCriterioResponseDTO> findByTipoAndSeccion(String tipoEvaluacion, Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findByTipoAndSeccion(tipoEvaluacion, seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    /**
     * Busca criterios recuperables de una sección.
     */
    public List<EvaluacionCriterioResponseDTO> findRecuperablesBySeccion(Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findRecuperablesBySeccion(seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    /**
     * Busca un criterio por ID.
     */
    public EvaluacionCriterioResponseDTO findById(Long id) {
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Criterio de evaluación no encontrado con ID: " + id));
        return criterioMapper.toResponseDTO(criterio);
    }

    // =====================================================
    // OPERACIONES DE VALIDACIÓN Y CÁLCULO
    // =====================================================

    /**
     * Verifica si el peso total de los criterios es exactamente 100%.
     */
    public boolean isPesoTotalValido(Long seccionId) {
        return criterioRepository.isPesoTotalValido(seccionId);
    }

    /**
     * Obtiene la suma de pesos por sección.
     */
    public BigDecimal sumPesoBySeccion(Long seccionId) {
        Integer peso = criterioRepository.sumPesoBySeccion(seccionId);
        return peso != null ? new BigDecimal(peso) : BigDecimal.ZERO;
    }

    /**
     * Cuenta la cantidad de criterios de una sección.
     */
    public long countBySeccion(Long seccionId) {
        return criterioRepository.countBySeccion(seccionId);
    }
}
