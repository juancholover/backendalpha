package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.PlanCursoRequestDTO;
import upeu.edu.pe.academic.application.dto.PlanCursoResponseDTO;
import upeu.edu.pe.academic.application.mapper.PlanCursoMapper;
import upeu.edu.pe.academic.domain.entities.PlanCurso;
import upeu.edu.pe.academic.domain.usecases.ActualizarPlanCursoUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarPlanCursoUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearPlanCursoUseCase;
import upeu.edu.pe.academic.domain.usecases.EliminarPlanCursoUseCase;

import java.util.List;

@ApplicationScoped
public class PlanCursoApplicationService {

    @Inject
    CrearPlanCursoUseCase crearUseCase;

    @Inject
    ActualizarPlanCursoUseCase actualizarUseCase;

    @Inject
    BuscarPlanCursoUseCase buscarUseCase;

    @Inject
    EliminarPlanCursoUseCase eliminarUseCase;

    @Inject
    PlanCursoMapper mapper;

    @Transactional
    public PlanCursoResponseDTO create(PlanCursoRequestDTO dto) {
        PlanCurso planCurso = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getPlanAcademicoId(),
                dto.getCursoId(),
                dto.getCreditos(),
                dto.getCiclo(),
                dto.getTipoCurso(),
                dto.getEsObligatorio());
        return mapper.toResponseDTO(planCurso);
    }

    @Transactional
    public PlanCursoResponseDTO update(Long id, PlanCursoRequestDTO dto) {
        PlanCurso planCurso = actualizarUseCase.execute(
                id,
                dto.getCreditos(),
                dto.getCiclo(),
                dto.getTipoCurso(),
                dto.getEsObligatorio());
        return mapper.toResponseDTO(planCurso);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public PlanCursoResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public List<PlanCursoResponseDTO> findByPlanAcademico(Long planAcademicoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPlanAcademico(planAcademicoId));
    }

    public List<PlanCursoResponseDTO> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo) {
        return mapper.toResponseDTOList(buscarUseCase.findByPlanAcademicoAndCiclo(planAcademicoId, ciclo));
    }

    public List<PlanCursoResponseDTO> findByCurso(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByCurso(cursoId));
    }

    public List<PlanCursoResponseDTO> findObligatoriosByPlanAcademico(Long planAcademicoId) {
        return mapper.toResponseDTOList(buscarUseCase.findObligatoriosByPlanAcademico(planAcademicoId));
    }

    public List<PlanCursoResponseDTO> findElectivosByPlanAcademico(Long planAcademicoId) {
        return mapper.toResponseDTOList(buscarUseCase.findElectivosByPlanAcademico(planAcademicoId));
    }

    public Integer calcularCreditosTotales(Long planAcademicoId) {
        return buscarUseCase.calcularCreditosTotales(planAcademicoId);
    }
}
