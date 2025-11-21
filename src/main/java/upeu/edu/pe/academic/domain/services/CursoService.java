package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import upeu.edu.pe.academic.application.dto.CursoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoResponseDTO;
import upeu.edu.pe.academic.application.mapper.CursoMapper;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.entities.PlanAcademico;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.PlanAcademicoRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CursoService {

    @Inject
    CursoRepository cursoRepository;

    @Inject
    PlanAcademicoRepository planAcademicoRepository;

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    CursoMapper cursoMapper;

    /**
     * Listar todos los cursos activos
     */
    public List<CursoResponseDTO> findAll() {
        return cursoRepository.findAllActive()
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar curso por ID
     */
    public CursoResponseDTO findById(Long id) {
        Curso curso = cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
        
        return cursoMapper.toResponseDTO(curso);
    }

    /**
     * Buscar curso por código
     */
    public CursoResponseDTO findByCodigoCurso(String codigoCurso) {
        Curso curso = cursoRepository.findByCodigoCurso(codigoCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "codigoCurso", codigoCurso));
        
        return cursoMapper.toResponseDTO(curso);
    }

    /**
     * Listar cursos por plan académico
     */
    public List<CursoResponseDTO> findByPlanAcademico(Long planAcademicoId) {
        return cursoRepository.findByPlanAcademico(planAcademicoId)
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar cursos por ciclo
     */
    public List<CursoResponseDTO> findByCiclo(Long planAcademicoId, Integer ciclo) {
        return cursoRepository.findByCiclo(planAcademicoId, ciclo)
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar cursos por tipo (OBLIGATORIO, ELECTIVO, LIBRE)
     */
    public List<CursoResponseDTO> findByTipoCurso(String tipoCurso) {
        return cursoRepository.findByTipoCurso(tipoCurso)
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar cursos sin prerequisitos
     */
    public List<CursoResponseDTO> findCursosSinPrerequisitos(Long planAcademicoId) {
        return cursoRepository.findCursosSinPrerequisitos(planAcademicoId)
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear nuevo curso
     */
    @Transactional
    public CursoResponseDTO create(@Valid CursoRequestDTO dto) {
        // Validar que exista la universidad
        Universidad universidad = universidadRepository.findByIdOptional(dto.getUniversidadId())
                .filter(u -> u.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", dto.getUniversidadId()));

        // Validar que exista el plan académico
        PlanAcademico planAcademico = planAcademicoRepository.findByIdOptional(dto.getPlanAcademicoId())
                .filter(p -> p.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("PlanAcademico", "id", dto.getPlanAcademicoId()));

        // Validar que el plan académico esté vigente
        if (!"VIGENTE".equals(planAcademico.getEstado())) {
            throw new BusinessRuleException("El plan académico debe estar en estado VIGENTE para crear cursos");
        }

        // Validar que no exista el código del curso
        if (cursoRepository.existsByCodigoCurso(dto.getCodigoCurso())) {
            throw new DuplicateResourceException("Curso", "codigoCurso", dto.getCodigoCurso());
        }

        // NOTA: creditos y ciclo están en PlanAcademico (varían por programa)
        // NOTA: prerequisitos están en RequisitoCurso (con universidad_id)

        // Validar coherencia de horas
        if (dto.getHorasTeoricas() != null && dto.getHorasPracticas() != null && dto.getHorasSemanales() != null) {
            int totalHoras = dto.getHorasTeoricas() + dto.getHorasPracticas();
            if (totalHoras != dto.getHorasSemanales()) {
                throw new BusinessRuleException(
                    "Las horas semanales (" + dto.getHorasSemanales() + ") deben ser igual a la suma de " +
                    "horas teóricas (" + dto.getHorasTeoricas() + ") y prácticas (" + dto.getHorasPracticas() + ")"
                );
            }
        }

        // Crear la entidad curso
        Curso curso = cursoMapper.toEntity(dto);
        curso.setUniversidad(universidad);
        curso.setPlanAcademico(planAcademico);

        // Establecer valores por defecto
        if (curso.getTipoCurso() == null) {
            curso.setTipoCurso("OBLIGATORIO");
        }
        if (curso.getHorasTeoricas() == null) {
            curso.setHorasTeoricas(0);
        }
        if (curso.getHorasPracticas() == null) {
            curso.setHorasPracticas(0);
        }
        if (curso.getHorasSemanales() == null) {
            curso.setHorasSemanales(curso.getHorasTeoricas() + curso.getHorasPracticas());
        }

        cursoRepository.persist(curso);

        return cursoMapper.toResponseDTO(curso);
    }

    /**
     * Actualizar curso existente
     */
    @Transactional
    public CursoResponseDTO update(Long id, @Valid CursoRequestDTO dto) {
        // Buscar el curso
        Curso curso = cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));

        // Validar universidad si cambió
        if (!curso.getUniversidad().getId().equals(dto.getUniversidadId())) {
            Universidad nuevaUniversidad = universidadRepository.findByIdOptional(dto.getUniversidadId())
                    .filter(u -> u.getActive())
                    .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", dto.getUniversidadId()));
            curso.setUniversidad(nuevaUniversidad);
        }

        // Validar plan académico si cambió
        if (!curso.getPlanAcademico().getId().equals(dto.getPlanAcademicoId())) {
            PlanAcademico nuevoPlan = planAcademicoRepository.findByIdOptional(dto.getPlanAcademicoId())
                    .filter(p -> p.getActive())
                    .orElseThrow(() -> new ResourceNotFoundException("PlanAcademico", "id", dto.getPlanAcademicoId()));

            if (!"VIGENTE".equals(nuevoPlan.getEstado())) {
                throw new BusinessRuleException("El plan académico debe estar en estado VIGENTE");
            }

            curso.setPlanAcademico(nuevoPlan);
        }

        // Validar código de curso si cambió
        if (!curso.getCodigoCurso().equals(dto.getCodigoCurso()) &&
            cursoRepository.existsByCodigoCursoAndIdNot(dto.getCodigoCurso(), id)) {
            throw new DuplicateResourceException("Curso", "codigoCurso", dto.getCodigoCurso());
        }

        // NOTA: creditos y ciclo están en PlanAcademico (varían por programa)
        // NOTA: prerequisitos están en RequisitoCurso (con universidad_id)

        // Validar coherencia de horas
        if (dto.getHorasTeoricas() != null && dto.getHorasPracticas() != null && dto.getHorasSemanales() != null) {
            int totalHoras = dto.getHorasTeoricas() + dto.getHorasPracticas();
            if (totalHoras != dto.getHorasSemanales()) {
                throw new BusinessRuleException(
                    "Las horas semanales deben ser igual a la suma de horas teóricas y prácticas"
                );
            }
        }

        // Actualizar la entidad
        cursoMapper.updateEntityFromDto(dto, curso);
        cursoRepository.persist(curso);

        return cursoMapper.toResponseDTO(curso);
    }

    /**
     * Eliminar curso (borrado lógico)
     */
    @Transactional
    public void delete(Long id) {
        Curso curso = cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));

        // NOTA: prerequisitos están en RequisitoCurso (consultar ahí antes de eliminar)

        curso.setActive(false);
        cursoRepository.persist(curso);
    }

    /**
     * Obtener entidad Curso por ID (para uso interno de otros servicios)
     */
    public Curso getEntityById(Long id) {
        return cursoRepository.findByIdOptional(id)
                .filter(Curso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
    }
}
