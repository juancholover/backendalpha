package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.PlanAcademicoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.PlanAcademicoMapper;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.curriculum.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de planes académicos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class PlanAcademicoService {

    @Inject
    PlanAcademicoRepository planRepository;

    @Inject
    PlanAcademicoMapper planMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<PlanAcademicoResponseDTO> findAll() {
        List<PlanAcademico> planes = planRepository.findAllActive();
        return planMapper.toResponseDTOList(planes);
    }

    public PlanAcademicoResponseDTO findById(Long id) {
        PlanAcademico plan = planRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + id));
        return planMapper.toResponseDTO(plan);
    }

    public PlanAcademicoResponseDTO findByCodigo(String codigo) {
        PlanAcademico plan = planRepository.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con código: " + codigo));
        return planMapper.toResponseDTO(plan);
    }

    public List<PlanAcademicoResponseDTO> findByProgramaAcademico(Long programaAcademicoId) {
        List<PlanAcademico> planes = planRepository.findByProgramaAcademico(programaAcademicoId);
        return planMapper.toResponseDTOList(planes);
    }

    public List<PlanAcademicoResponseDTO> findPlanesVigentes(Long programaAcademicoId) {
        List<PlanAcademico> planes = planRepository.findPlanesVigentes(programaAcademicoId);
        return planMapper.toResponseDTOList(planes);
    }

    public PlanAcademicoResponseDTO findPlanVigenteActual(Long programaAcademicoId) {
        PlanAcademico plan = planRepository.findPlanVigenteActual(programaAcademicoId)
                .orElseThrow(() -> new NotFoundException(
                        "No hay plan vigente actual para el programa con ID: " + programaAcademicoId));
        return planMapper.toResponseDTO(plan);
    }

    public PlanAcademico getEntityById(Long id) {
        return planRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + id));
    }
}
