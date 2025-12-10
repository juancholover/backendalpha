package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.enrollment.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.MatriculaMapper;
import upeu.edu.pe.enrollment.domain.entities.Matricula;
import upeu.edu.pe.enrollment.domain.repositories.MatriculaRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de matrículas.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class MatriculaService {

    @Inject
    MatriculaRepository matriculaRepository;

    @Inject
    MatriculaMapper matriculaMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<MatriculaResponseDTO> findByEstudiante(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudiante(estudianteId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findBySeccion(Long seccionId) {
        List<Matricula> matriculas = matriculaRepository.findBySeccion(seccionId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByPeriodoAcademico(Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findByPeriodoAcademico(periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudianteAndPeriodo(estudianteId, periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByEstadoMatricula(String estadoMatricula) {
        List<Matricula> matriculas = matriculaRepository.findByEstadoMatricula(estadoMatricula);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findMatriculasActivas(Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findMatriculasActivas(periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findMatriculasAprobadas(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findMatriculasAprobadas(estudianteId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByTipoMatricula(String tipoMatricula, Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findByTipoMatricula(tipoMatricula, periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public MatriculaResponseDTO findById(Long id) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + id));
        return matriculaMapper.toResponseDTO(matricula);
    }

    public MatriculaResponseDTO findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        Matricula matricula = matriculaRepository.findByEstudianteAndSeccion(estudianteId, seccionId)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada para el estudiante y sección"));
        return matriculaMapper.toResponseDTO(matricula);
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public Matricula getEntityById(Long id) {
        return matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + id));
    }
}
