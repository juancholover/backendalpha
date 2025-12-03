package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import upeu.edu.pe.academic.application.dto.EstudianteRequestDTO;
import upeu.edu.pe.academic.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.academic.application.mapper.EstudianteMapper;
import upeu.edu.pe.academic.domain.commands.ActualizarEstudianteCommand;
import upeu.edu.pe.academic.domain.commands.CambiarEstadoAcademicoCommand;
import upeu.edu.pe.academic.domain.commands.CrearEstudianteCommand;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.usecases.ActualizarEstudianteUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarEstudianteUseCase;
import upeu.edu.pe.academic.domain.usecases.CambiarEstadoAcademicoUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearEstudianteUseCase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application Service: Orquesta casos de uso y maneja conversiones DTO ↔ Entity.
 * 
 * Este servicio NO contiene lógica de negocio, solo:
 * - Conversión de DTOs a Commands
 * - Delegación a Use Cases
 * - Conversión de Entity a ResponseDTO
 * - Gestión de transacciones
 */
@ApplicationScoped
public class EstudianteService {

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    EstudianteMapper estudianteMapper;
    
    @Inject
    CrearEstudianteUseCase crearUseCase;
    
    @Inject
    ActualizarEstudianteUseCase actualizarUseCase;
    
    @Inject
    BuscarEstudianteUseCase buscarUseCase;
    
    @Inject
    CambiarEstadoAcademicoUseCase cambiarEstadoUseCase;

    /**
     * Listar todos los estudiantes activos.
     */
    public List<EstudianteResponseDTO> findAll() {
        return buscarUseCase.ejecutarListarActivos()
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar estudiante por ID.
     */
    public EstudianteResponseDTO findById(Long id) {
        Estudiante estudiante = buscarUseCase.ejecutarPorId(id);
        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Buscar estudiante por código.
     */
    public EstudianteResponseDTO findByCodigoEstudiante(String codigoEstudiante) {
        Estudiante estudiante = buscarUseCase.ejecutarPorCodigo(codigoEstudiante);
        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Listar estudiantes por programa académico.
     */
    public List<EstudianteResponseDTO> findByProgramaAcademico(Long programaAcademicoId) {
        return buscarUseCase.ejecutarPorPrograma(programaAcademicoId)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar estudiantes por estado académico.
     */
    public List<EstudianteResponseDTO> findByEstadoAcademico(String estadoAcademico) {
        return buscarUseCase.ejecutarPorEstadoAcademico(estadoAcademico)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar estudiantes activos de un programa.
     */
    public List<EstudianteResponseDTO> findEstudiantesActivos(Long programaAcademicoId) {
        return buscarUseCase.ejecutarActivosPorPrograma(programaAcademicoId)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear nuevo estudiante.
     * 
     * Flujo:
     * 1. DTO → Command (conversión)
     * 2. Use Case (lógica de negocio + validaciones)
     * 3. Entity → ResponseDTO (conversión)
     */
    @Transactional
    public EstudianteResponseDTO create(@Valid EstudianteRequestDTO dto) {
        // 1. Convertir DTO → Command
        CrearEstudianteCommand command = new CrearEstudianteCommand(
            dto.getPersonaId(),
            null, // universidadId se obtiene del contexto o del programa
            dto.getProgramaAcademicoId(),
            dto.getCodigoEstudiante(),
            dto.getFechaIngreso(),
            dto.getCicloActual(),
            dto.getModalidadIngreso(),
            dto.getTipoEstudiante()
        );
        
        // 2. Ejecutar caso de uso (validaciones + persistencia)
        Estudiante estudiante = crearUseCase.execute(command);
        
        // 3. Convertir Entity → ResponseDTO
        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Actualizar estudiante existente.
     * 
     * Flujo:
     * 1. DTO → Command
     * 2. Use Case (validaciones + actualización)
     * 3. Entity → ResponseDTO
     */
    @Transactional
    public EstudianteResponseDTO update(Long id, @Valid EstudianteRequestDTO dto) {
        // 1. Convertir DTO → Command
        ActualizarEstudianteCommand command = new ActualizarEstudianteCommand(
            id,
            dto.getProgramaAcademicoId(),
            dto.getCodigoEstudiante(),
            dto.getCicloActual(),
            dto.getCreditosAprobados(),
            dto.getCreditosCursando(),
            dto.getCreditosObligatoriosAprobados(),
            dto.getCreditosElectivosAprobados(),
            dto.getPromedioPonderado(),
            dto.getEstadoAcademico(),
            dto.getTipoEstudiante()
        );
        
        // 2. Ejecutar caso de uso
        Estudiante estudiante = actualizarUseCase.execute(command);
        
        // 3. Convertir Entity → ResponseDTO
        return estudianteMapper.toResponseDTO(estudiante);
    }

    /**
     * Eliminar estudiante (borrado lógico).
     */
    @Transactional
    public void delete(Long id) {
        Estudiante estudiante = buscarUseCase.ejecutarPorId(id);
        estudiante.setActive(false);
    }

    /**
     * Cambiar estado académico del estudiante.
     */
    @Transactional
    public EstudianteResponseDTO cambiarEstadoAcademico(Long id, String nuevoEstado) {
        // 1. Crear command
        CambiarEstadoAcademicoCommand command = new CambiarEstadoAcademicoCommand(id, nuevoEstado);
        
        // 2. Ejecutar caso de uso
        Estudiante estudiante = cambiarEstadoUseCase.execute(command);
        
        // 3. Convertir Entity → ResponseDTO
        return estudianteMapper.toResponseDTO(estudiante);
    }
}
