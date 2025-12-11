package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.PlanCursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.PlanCursoMapper;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;
import upeu.edu.pe.curriculum.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de plan-curso.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class PlanCursoService {

    @Inject
    PlanCursoRepository planCursoRepository;

    @Inject
    PlanCursoMapper planCursoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<PlanCursoResponseDTO> findByPlanAcademico(Long planAcademicoId) {
        List<PlanCurso> planCursos = planCursoRepository.findByPlanAcademico(planAcademicoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    public List<PlanCursoResponseDTO> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo) {
        List<PlanCurso> planCursos = planCursoRepository.findByPlanAcademicoAndCiclo(planAcademicoId, ciclo);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    public List<PlanCursoResponseDTO> findByCurso(Long cursoId) {
        List<PlanCurso> planCursos = planCursoRepository.findByCurso(cursoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    public List<PlanCursoResponseDTO> findObligatoriosByPlanAcademico(Long planAcademicoId) {
        List<PlanCurso> planCursos = planCursoRepository.findObligatoriosByPlanAcademico(planAcademicoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    public List<PlanCursoResponseDTO> findElectivosByPlanAcademico(Long planAcademicoId) {
        List<PlanCurso> planCursos = planCursoRepository.findElectivosByPlanAcademico(planAcademicoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    public PlanCursoResponseDTO findById(Long id) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new NotFoundException("PlanCurso no encontrado con ID: " + id));
        return planCursoMapper.toResponseDTO(planCurso);
    }

    public PlanCurso getEntityById(Long id) {
        return planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new NotFoundException("PlanCurso no encontrado con ID: " + id));
    }

    public Integer calcularCreditosTotales(Long planAcademicoId) {
        Integer obligatorios = planCursoRepository.sumCreditosObligatorios(planAcademicoId);
        Integer electivos = planCursoRepository.sumCreditosElectivos(planAcademicoId);
        return (obligatorios != null ? obligatorios : 0) + (electivos != null ? electivos : 0);
    }
}
