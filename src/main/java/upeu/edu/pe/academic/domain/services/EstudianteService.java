package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import upeu.edu.pe.academic.application.dto.EstudianteRequestDTO;
import upeu.edu.pe.academic.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.academic.application.mapper.EstudianteMapper;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EstudianteService {

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    ProgramaAcademicoRepository programaAcademicoRepository;

    @Inject
    PersonaService personaService;

    @Inject
    EstudianteMapper estudianteMapper;

    /**
     * Listar todos los estudiantes activos
     */
    public List<EstudianteResponseDTO> findAll() {
        return estudianteRepository.findAllActive()
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar estudiante por ID
     */
    public EstudianteResponseDTO findById(Long id) {
        Estudiante estudiante = estudianteRepository.findByIdOptional(id)
                .filter(e -> e.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));
        
        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Buscar estudiante por código
     */
    public EstudianteResponseDTO findByCodigoEstudiante(String codigoEstudiante) {
        Estudiante estudiante = estudianteRepository.findByCodigoEstudiante(codigoEstudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "codigoEstudiante", codigoEstudiante));
        
        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Listar estudiantes por programa académico
     */
    public List<EstudianteResponseDTO> findByProgramaAcademico(Long programaAcademicoId) {
        return estudianteRepository.findByProgramaAcademico(programaAcademicoId)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar estudiantes por estado académico
     */
    public List<EstudianteResponseDTO> findByEstadoAcademico(String estadoAcademico) {
        return estudianteRepository.findByEstadoAcademico(estadoAcademico)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar estudiantes activos de un programa
     */
    public List<EstudianteResponseDTO> findEstudiantesActivos(Long programaAcademicoId) {
        return estudianteRepository.findEstudiantesActivos(programaAcademicoId)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear nuevo estudiante
     */
    @Transactional
    public EstudianteResponseDTO create(@Valid EstudianteRequestDTO dto) {
        // Validar que exista la persona
        Persona persona = personaService.getEntityById(dto.getPersonaId());

        // Validar que la persona no sea ya un estudiante
        if (estudianteRepository.existsByPersona(dto.getPersonaId())) {
            throw new BusinessRuleException("La persona con ID " + dto.getPersonaId() + " ya es estudiante");
        }

        // Validar que exista el programa académico
        ProgramaAcademico programaAcademico = programaAcademicoRepository.findByIdOptional(dto.getProgramaAcademicoId())
                .filter(p -> p.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("ProgramaAcademico", "id", dto.getProgramaAcademicoId()));

        // Validar que el programa esté activo
        if (!"ACTIVO".equals(programaAcademico.getEstado())) {
            throw new BusinessRuleException("El programa académico no está activo");
        }

        // Validar que no exista el código de estudiante
        if (estudianteRepository.existsByCodigoEstudiante(dto.getCodigoEstudiante())) {
            throw new DuplicateResourceException("Estudiante", "codigoEstudiante", dto.getCodigoEstudiante());
        }

        // Validar que el ciclo actual sea válido
        if (dto.getCicloActual() != null && dto.getCicloActual() > programaAcademico.getDuracionSemestres()) {
            throw new BusinessRuleException(
                "El ciclo actual (" + dto.getCicloActual() + ") no puede ser mayor a la duración del programa (" + 
                programaAcademico.getDuracionSemestres() + " semestres)"
            );
        }

        // Crear la entidad estudiante
        Estudiante estudiante = estudianteMapper.toEntity(dto);
        estudiante.setPersona(persona);
        estudiante.setProgramaAcademico(programaAcademico);

        // Establecer valores por defecto si no se proporcionaron
        if (estudiante.getEstadoAcademico() == null) {
            estudiante.setEstadoAcademico("ACTIVO");
        }
        if (estudiante.getTipoEstudiante() == null) {
            estudiante.setTipoEstudiante("REGULAR");
        }
        if (estudiante.getCreditosAprobados() == null) {
            estudiante.setCreditosAprobados(0);
        }

        estudianteRepository.persist(estudiante);

        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Actualizar estudiante existente
     */
    @Transactional
    public EstudianteResponseDTO update(Long id, @Valid EstudianteRequestDTO dto) {
        // Buscar el estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(id)
                .filter(e -> e.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));

        // Validar que no se cambie la persona (regla de negocio)
        if (!estudiante.getPersona().getId().equals(dto.getPersonaId())) {
            throw new BusinessRuleException("No se puede cambiar la persona asociada a un estudiante");
        }

        // Validar programa académico si cambió
        if (!estudiante.getProgramaAcademico().getId().equals(dto.getProgramaAcademicoId())) {
            ProgramaAcademico nuevoPrograma = programaAcademicoRepository.findByIdOptional(dto.getProgramaAcademicoId())
                    .filter(p -> p.getActive())
                    .orElseThrow(() -> new ResourceNotFoundException("ProgramaAcademico", "id", dto.getProgramaAcademicoId()));

            if (!"ACTIVO".equals(nuevoPrograma.getEstado())) {
                throw new BusinessRuleException("El programa académico no está activo");
            }

            estudiante.setProgramaAcademico(nuevoPrograma);
        }

        // Validar código de estudiante si cambió
        if (!estudiante.getCodigoEstudiante().equals(dto.getCodigoEstudiante()) &&
            estudianteRepository.existsByCodigoEstudianteAndIdNot(dto.getCodigoEstudiante(), id)) {
            throw new DuplicateResourceException("Estudiante", "codigoEstudiante", dto.getCodigoEstudiante());
        }

        // Validar ciclo actual
        if (dto.getCicloActual() != null && dto.getCicloActual() > estudiante.getProgramaAcademico().getDuracionSemestres()) {
            throw new BusinessRuleException(
                "El ciclo actual (" + dto.getCicloActual() + ") no puede ser mayor a la duración del programa (" + 
                estudiante.getProgramaAcademico().getDuracionSemestres() + " semestres)"
            );
        }

        // Actualizar la entidad
        estudianteMapper.updateEntityFromDto(dto, estudiante);
        estudianteRepository.persist(estudiante);

        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Eliminar estudiante (borrado lógico)
     */
    @Transactional
    public void delete(Long id) {
        Estudiante estudiante = estudianteRepository.findByIdOptional(id)
                .filter(e -> e.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));

        estudiante.setActive(false);
        estudianteRepository.persist(estudiante);
    }

    /**
     * Cambiar estado académico del estudiante
     */
    @Transactional
    public EstudianteResponseDTO cambiarEstadoAcademico(Long id, String nuevoEstado) {
        Estudiante estudiante = estudianteRepository.findByIdOptional(id)
                .filter(e -> e.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));

        // Validar estados válidos
        if (!List.of("ACTIVO", "RETIRADO", "EGRESADO", "GRADUADO", "LICENCIA").contains(nuevoEstado)) {
            throw new BusinessRuleException("Estado académico inválido: " + nuevoEstado);
        }

        estudiante.setEstadoAcademico(nuevoEstado);
        estudianteRepository.persist(estudiante);

        return estudianteMapper.toResponseDTO(estudiante);
    }
}
