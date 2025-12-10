package upeu.edu.pe.assessment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.assessment.application.dto.EvaluacionNotaResponseDTO;
import upeu.edu.pe.assessment.application.mapper.EvaluacionNotaMapper;
import upeu.edu.pe.assessment.domain.entities.EvaluacionNota;
import upeu.edu.pe.assessment.domain.repositories.EvaluacionNotaRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas básicas de notas de evaluación.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA (registrar nota, recuperación) se manejan en Use
 * Cases.
 * 
 * @author Sistema UPeU
 */
@ApplicationScoped
public class EvaluacionNotaService {

    @Inject
    EvaluacionNotaRepository notaRepository;

    @Inject
    EvaluacionNotaMapper notaMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Busca notas por matrícula.
     */
    public List<EvaluacionNotaResponseDTO> findByMatricula(Long matriculaId) {
        List<EvaluacionNota> notas = notaRepository.findByMatricula(matriculaId);
        return notaMapper.toResponseDTOList(notas);
    }

    /**
     * Busca notas por criterio.
     */
    public List<EvaluacionNotaResponseDTO> findByCriterio(Long criterioId) {
        List<EvaluacionNota> notas = notaRepository.findByCriterio(criterioId);
        return notaMapper.toResponseDTOList(notas);
    }

    /**
     * Busca una nota específica por matrícula y criterio.
     */
    public EvaluacionNotaResponseDTO findByMatriculaAndCriterio(Long matriculaId, Long criterioId) {
        EvaluacionNota nota = notaRepository.findByMatriculaAndCriterio(matriculaId, criterioId)
                .orElseThrow(() -> new NotFoundException(
                        "Nota no encontrada para la matrícula " + matriculaId + " y criterio " + criterioId));
        return notaMapper.toResponseDTO(nota);
    }

    /**
     * Busca una nota por ID.
     */
    public EvaluacionNotaResponseDTO findById(Long id) {
        EvaluacionNota nota = notaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Nota de evaluación no encontrada con ID: " + id));
        return notaMapper.toResponseDTO(nota);
    }

    /**
     * Busca notas pendientes de calificar por sección.
     */
    public List<EvaluacionNotaResponseDTO> findPendientesBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findPendientesBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    /**
     * Busca notas calificadas por sección.
     */
    public List<EvaluacionNotaResponseDTO> findCalificadasBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findCalificadasBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    /**
     * Busca notas por estudiante y sección.
     */
    public List<EvaluacionNotaResponseDTO> findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findByEstudianteAndSeccion(estudianteId, seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    /**
     * Busca notas con recuperación por sección.
     */
    public List<EvaluacionNotaResponseDTO> findConRecuperacionBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findConRecuperacionBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    /**
     * Busca notas desaprobadas por sección.
     */
    public List<EvaluacionNotaResponseDTO> findDesaprobadasBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findDesaprobadasBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    // =====================================================
    // OPERACIONES DE ESTADÍSTICAS
    // =====================================================

    /**
     * Obtiene el promedio de notas de un criterio.
     */
    public Double getPromedioNotasByCriterio(Long criterioId) {
        return notaRepository.getPromedioNotasByCriterio(criterioId);
    }

    /**
     * Cuenta las notas calificadas de una matrícula.
     */
    public long countCalificadasByMatricula(Long matriculaId) {
        return notaRepository.countCalificadasByMatricula(matriculaId);
    }

    /**
     * Verifica si existe una nota para una matrícula y criterio.
     */
    public boolean existsByMatriculaAndCriterio(Long matriculaId, Long criterioId) {
        return notaRepository.existsByMatriculaAndCriterio(matriculaId, criterioId);
    }
}
