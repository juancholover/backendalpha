package upeu.edu.pe.people.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.people.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.people.application.mapper.EstudianteMapper;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.usecases.BuscarEstudianteUseCase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de estudiantes.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class EstudianteService {

    @Inject
    EstudianteMapper estudianteMapper;

    @Inject
    BuscarEstudianteUseCase buscarUseCase;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<EstudianteResponseDTO> findAll() {
        return buscarUseCase.ejecutarListarActivos()
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public EstudianteResponseDTO findById(Long id) {
        Estudiante estudiante = buscarUseCase.ejecutarPorId(id);
        return estudianteMapper.toResponseDTO(estudiante);
    }

    public EstudianteResponseDTO findByCodigoEstudiante(String codigoEstudiante) {
        Estudiante estudiante = buscarUseCase.ejecutarPorCodigo(codigoEstudiante);
        return estudianteMapper.toResponseDTO(estudiante);
    }

    public List<EstudianteResponseDTO> findByProgramaAcademico(Long programaAcademicoId) {
        return buscarUseCase.ejecutarPorPrograma(programaAcademicoId)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> findByEstadoAcademico(String estadoAcademico) {
        return buscarUseCase.ejecutarPorEstadoAcademico(estadoAcademico)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> findEstudiantesActivos(Long programaAcademicoId) {
        return buscarUseCase.ejecutarActivosPorPrograma(programaAcademicoId)
                .stream()
                .map(estudianteMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Estudiante getEntityById(Long id) {
        return buscarUseCase.ejecutarPorId(id);
    }
}
