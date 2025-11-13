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
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.PlanAcademicoRepository;
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

        // Validar que el ciclo no exceda la duración del programa
        Integer duracionPrograma = planAcademico.getProgramaAcademico().getDuracionSemestres();
        if (dto.getCiclo() > duracionPrograma) {
            throw new BusinessRuleException(
                "El ciclo (" + dto.getCiclo() + ") no puede ser mayor a la duración del programa (" + 
                duracionPrograma + " semestres)"
            );
        }

        // Validar prerequisito si existe
        Curso prerequisito = null;
        if (dto.getPrerequisitoId() != null) {
            prerequisito = cursoRepository.findByIdOptional(dto.getPrerequisitoId())
                    .filter(c -> c.getActive())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso prerequisito", "id", dto.getPrerequisitoId()));

            // Validar que el prerequisito sea del mismo plan académico
            if (!prerequisito.getPlanAcademico().getId().equals(dto.getPlanAcademicoId())) {
                throw new BusinessRuleException("El prerequisito debe pertenecer al mismo plan académico");
            }

            // Validar que el prerequisito sea de un ciclo anterior
            if (prerequisito.getCiclo() >= dto.getCiclo()) {
                throw new BusinessRuleException(
                    "El prerequisito debe ser de un ciclo anterior al curso actual"
                );
            }

            // Validar que no haya dependencia circular
            if (tienePrerequisito(prerequisito, dto.getCodigoCurso())) {
                throw new BusinessRuleException("Se detectó una dependencia circular con el prerequisito");
            }
        }

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
        curso.setPlanAcademico(planAcademico);
        curso.setPrerequisito(prerequisito);

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

        // Validar ciclo
        Integer duracionPrograma = curso.getPlanAcademico().getProgramaAcademico().getDuracionSemestres();
        if (dto.getCiclo() > duracionPrograma) {
            throw new BusinessRuleException(
                "El ciclo (" + dto.getCiclo() + ") no puede ser mayor a la duración del programa (" + 
                duracionPrograma + " semestres)"
            );
        }

        // Validar prerequisito si cambió
        if (dto.getPrerequisitoId() != null) {
            if (!dto.getPrerequisitoId().equals(id)) { // No puede ser prerequisito de sí mismo
                Curso prerequisito = cursoRepository.findByIdOptional(dto.getPrerequisitoId())
                        .filter(c -> c.getActive())
                        .orElseThrow(() -> new ResourceNotFoundException("Curso prerequisito", "id", dto.getPrerequisitoId()));

                if (!prerequisito.getPlanAcademico().getId().equals(dto.getPlanAcademicoId())) {
                    throw new BusinessRuleException("El prerequisito debe pertenecer al mismo plan académico");
                }

                if (prerequisito.getCiclo() >= dto.getCiclo()) {
                    throw new BusinessRuleException("El prerequisito debe ser de un ciclo anterior");
                }

                curso.setPrerequisito(prerequisito);
            } else {
                throw new BusinessRuleException("Un curso no puede ser prerequisito de sí mismo");
            }
        } else {
            curso.setPrerequisito(null);
        }

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

        // Validar que no haya otros cursos que dependan de este como prerequisito
        List<Curso> cursosConPrerequisito = cursoRepository.findAllActive()
                .stream()
                .filter(c -> c.getPrerequisito() != null && c.getPrerequisito().getId().equals(id))
                .collect(Collectors.toList());

        if (!cursosConPrerequisito.isEmpty()) {
            String cursosDependientes = cursosConPrerequisito.stream()
                    .map(Curso::getNombre)
                    .collect(Collectors.joining(", "));
            throw new BusinessRuleException(
                "No se puede eliminar el curso porque es prerequisito de: " + cursosDependientes
            );
        }

        curso.setActive(false);
        cursoRepository.persist(curso);
    }

    /**
     * Verifica si un curso tiene como prerequisito (directo o indirecto) a otro curso
     * Usado para detectar dependencias circulares
     */
    private boolean tienePrerequisito(Curso curso, String codigoBuscado) {
        if (curso == null || curso.getPrerequisito() == null) {
            return false;
        }
        
        if (curso.getPrerequisito().getCodigoCurso().equals(codigoBuscado)) {
            return true;
        }
        
        return tienePrerequisito(curso.getPrerequisito(), codigoBuscado);
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
