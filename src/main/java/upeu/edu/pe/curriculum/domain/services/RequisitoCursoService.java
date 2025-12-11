package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.RequisitoCursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.RequisitoCursoMapper;
import upeu.edu.pe.curriculum.domain.entities.RequisitoCurso;
import upeu.edu.pe.curriculum.domain.repositories.RequisitoCursoRepository;

import java.util.List;

/**
 * Servicio de dominio para consultas de requisitos de curso.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class RequisitoCursoService {

    @Inject
    RequisitoCursoRepository requisitoRepository;

    @Inject
    RequisitoCursoMapper requisitoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<RequisitoCursoResponseDTO> findByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findPrerequisitosByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findPrerequisitosByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findCorrequisitosByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findCorrequisitosByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findCursosQueTienenComoRequisito(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findCursosQueTienenComoRequisito(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findObligatoriosByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findObligatoriosByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findAllRequisitosCascada(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findAllRequisitosCascada(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public long countByCurso(Long cursoId) {
        return requisitoRepository.countByCurso(cursoId);
    }
}
