package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.PlanAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.PlanAcademicoResponseDTO;
import upeu.edu.pe.academic.application.mapper.PlanAcademicoMapper;
import upeu.edu.pe.academic.domain.entities.PlanAcademico;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class PlanAcademicoService {

    @Inject
    PlanAcademicoRepository planRepository;

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Inject
    PlanAcademicoMapper planMapper;

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
                .orElseThrow(() -> new NotFoundException("No hay plan vigente actual para el programa con ID: " + programaAcademicoId));
        return planMapper.toResponseDTO(plan);
    }

    @Transactional
    public PlanAcademicoResponseDTO create(PlanAcademicoRequestDTO requestDTO) {
        // Validar que no exista el código
        if (planRepository.existsByCodigo(requestDTO.getCodigo())) {
            throw new BusinessException("Ya existe un plan académico con el código: " + requestDTO.getCodigo());
        }

        // Validar que exista el programa académico
        ProgramaAcademico programaAcademico = programaRepository.findByIdOptional(requestDTO.getProgramaAcademicoId())
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + requestDTO.getProgramaAcademicoId()));

        // Validar fechas
        if (requestDTO.getFechaVigenciaFin() != null && 
            requestDTO.getFechaVigenciaFin().isBefore(requestDTO.getFechaVigenciaInicio())) {
            throw new BusinessException("La fecha de fin de vigencia debe ser posterior a la fecha de inicio");
        }

        // Crear el plan
        PlanAcademico plan = planMapper.toEntity(requestDTO);
        plan.setProgramaAcademico(programaAcademico);

        planRepository.persist(plan);
        return planMapper.toResponseDTO(plan);
    }

    @Transactional
    public PlanAcademicoResponseDTO update(Long id, PlanAcademicoRequestDTO requestDTO) {
        PlanAcademico plan = planRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + id));

        // Validar código único si cambió
        if (!plan.getCodigo().equals(requestDTO.getCodigo()) && 
            planRepository.existsByCodigoAndIdNot(requestDTO.getCodigo(), id)) {
            throw new BusinessException("Ya existe un plan académico con el código: " + requestDTO.getCodigo());
        }

        // Validar programa académico si cambió
        if (!plan.getProgramaAcademico().getId().equals(requestDTO.getProgramaAcademicoId())) {
            ProgramaAcademico programaAcademico = programaRepository.findByIdOptional(requestDTO.getProgramaAcademicoId())
                    .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + requestDTO.getProgramaAcademicoId()));
            plan.setProgramaAcademico(programaAcademico);
        }

        // Validar fechas
        if (requestDTO.getFechaVigenciaFin() != null && 
            requestDTO.getFechaVigenciaFin().isBefore(requestDTO.getFechaVigenciaInicio())) {
            throw new BusinessException("La fecha de fin de vigencia debe ser posterior a la fecha de inicio");
        }

        planMapper.updateEntityFromDTO(requestDTO, plan);
        planRepository.persist(plan);
        return planMapper.toResponseDTO(plan);
    }

    @Transactional
    public void delete(Long id) {
        PlanAcademico plan = planRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Plan académico no encontrado con ID: " + id));

        // Soft delete
        plan.setActive(false);
        planRepository.persist(plan);
    }
}
