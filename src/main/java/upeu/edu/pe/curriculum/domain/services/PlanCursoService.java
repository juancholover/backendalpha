package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.application.dto.PlanCursoRequestDTO;
import upeu.edu.pe.curriculum.application.dto.PlanCursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.PlanCursoMapper;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.entities.PlanAcademico;
import upeu.edu.pe.curriculum.domain.entities.PlanCurso;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.curriculum.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.curriculum.domain.repositories.PlanCursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;
import upeu.edu.pe.shared.exceptions.BusinessException;

import java.util.List;

@ApplicationScoped
public class PlanCursoService {

    @Inject
    PlanCursoRepository planCursoRepository;

    @Inject
    PlanCursoMapper planCursoMapper;

    @Inject
    PlanAcademicoRepository planAcademicoRepository;

    @Inject
    CursoRepository cursoRepository;

    /**
     * Busca todos los cursos de un plan académico
     */
    public List<PlanCursoResponseDTO> findByPlanAcademico(Long planAcademicoId) {
        List<PlanCurso> planCursos = planCursoRepository.findByPlanAcademico(planAcademicoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    /**
     * Busca cursos por plan y ciclo
     */
    public List<PlanCursoResponseDTO> findByPlanAcademicoAndCiclo(Long planAcademicoId, Integer ciclo) {
        List<PlanCurso> planCursos = planCursoRepository.findByPlanAcademicoAndCiclo(planAcademicoId, ciclo);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    /**
     * Busca todos los planes que contienen un curso
     */
    public List<PlanCursoResponseDTO> findByCurso(Long cursoId) {
        List<PlanCurso> planCursos = planCursoRepository.findByCurso(cursoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    /**
     * Busca cursos obligatorios de un plan
     */
    public List<PlanCursoResponseDTO> findObligatoriosByPlanAcademico(Long planAcademicoId) {
        List<PlanCurso> planCursos = planCursoRepository.findObligatoriosByPlanAcademico(planAcademicoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    /**
     * Busca cursos electivos de un plan
     */
    public List<PlanCursoResponseDTO> findElectivosByPlanAcademico(Long planAcademicoId) {
        List<PlanCurso> planCursos = planCursoRepository.findElectivosByPlanAcademico(planAcademicoId);
        return planCursoMapper.toResponseDTOList(planCursos);
    }

    /**
     * Busca por ID
     */
    public PlanCursoResponseDTO findById(Long id) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));
        return planCursoMapper.toResponseDTO(planCurso);
    }

    /**
     * Crea un nuevo PlanCurso (asigna curso a plan académico)
     */
    @Transactional
    public PlanCursoResponseDTO create(PlanCursoRequestDTO requestDTO) {
        // Validar que el curso no exista ya en el plan
        if (planCursoRepository.existsByPlanAcademicoAndCurso(
                requestDTO.getPlanAcademicoId(), requestDTO.getCursoId())) {
            throw new BusinessException("El curso ya existe en este plan académico");
        }

        // Cargar entidades relacionadas
        PlanAcademico planAcademico = planAcademicoRepository.findByIdOptional(requestDTO.getPlanAcademicoId())
                .filter(PlanAcademico::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanAcademico", "id", requestDTO.getPlanAcademicoId()));

        Curso curso = cursoRepository.findByIdOptional(requestDTO.getCursoId())
                .filter(Curso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", requestDTO.getCursoId()));

        // Crear entidad
        PlanCurso planCurso = planCursoMapper.toEntity(requestDTO);
        planCurso.setPlanAcademico(planAcademico);
        planCurso.setCurso(curso);

        planCursoRepository.persist(planCurso);
        return planCursoMapper.toResponseDTO(planCurso);
    }

    /**
     * Actualiza un PlanCurso existente (créditos, ciclo, tipo)
     */
    @Transactional
    public PlanCursoResponseDTO update(Long id, PlanCursoRequestDTO requestDTO) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));

        // Actualizar solo los campos modificables (NO cambiar plan ni curso)
        planCurso.setCreditos(requestDTO.getCreditos());
        planCurso.setCiclo(requestDTO.getCiclo());
        planCurso.setTipoCurso(requestDTO.getTipoCurso());
        planCurso.setEsObligatorio(requestDTO.getEsObligatorio());

        planCursoRepository.persist(planCurso);
        return planCursoMapper.toResponseDTO(planCurso);
    }

    /**
     * Elimina un PlanCurso (borrado lógico)
     */
    @Transactional
    public void delete(Long id) {
        PlanCurso planCurso = planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));

        planCurso.setActive(false);
        planCursoRepository.persist(planCurso);
    }

    /**
     * Obtiene la entidad PlanCurso por ID (para uso interno)
     */
    public PlanCurso getEntityById(Long id) {
        return planCursoRepository.findByIdOptional(id)
                .filter(PlanCurso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PlanCurso", "id", id));
    }

    /**
     * Calcula el total de créditos de un plan académico
     */
    public Integer calcularCreditosTotales(Long planAcademicoId) {
        Integer obligatorios = planCursoRepository.sumCreditosObligatorios(planAcademicoId);
        Integer electivos = planCursoRepository.sumCreditosElectivos(planAcademicoId);
        return (obligatorios != null ? obligatorios : 0) + (electivos != null ? electivos : 0);
    }
}

