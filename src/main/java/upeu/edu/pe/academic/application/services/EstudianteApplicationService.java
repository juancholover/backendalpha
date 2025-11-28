package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.EstudianteRequestDTO;
import upeu.edu.pe.academic.application.dto.EstudianteResponseDTO;
import upeu.edu.pe.academic.application.mapper.EstudianteMapper;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.usecases.ActualizarEstudianteUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarEstudianteUseCase;
import upeu.edu.pe.academic.domain.usecases.CambiarEstadoEstudianteUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearEstudianteUseCase;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EstudianteApplicationService {

    @Inject
    CrearEstudianteUseCase crearUseCase;

    @Inject
    BuscarEstudianteUseCase buscarUseCase;

    @Inject
    ActualizarEstudianteUseCase actualizarUseCase;

    @Inject
    CambiarEstadoEstudianteUseCase cambiarEstadoUseCase;

    @Inject
    EstudianteMapper mapper;

    @Transactional
    public EstudianteResponseDTO create(EstudianteRequestDTO dto) {
        Estudiante estudiante = crearUseCase.execute(
                dto.getPersonaId(),
                dto.getProgramaAcademicoId(),
                dto.getCodigoEstudiante(),
                dto.getFechaIngreso(),
                dto.getCicloActual(),
                dto.getModalidadIngreso(),
                dto.getTipoEstudiante());
        return mapper.toResponseDTO(estudiante);
    }

    @Transactional
    public EstudianteResponseDTO update(Long id, EstudianteRequestDTO dto) {
        Estudiante estudiante = actualizarUseCase.execute(
                id,
                dto.getPersonaId(),
                dto.getProgramaAcademicoId(),
                dto.getCodigoEstudiante(),
                dto.getFechaIngreso(),
                dto.getCicloActual(),
                dto.getModalidadIngreso(),
                dto.getTipoEstudiante());
        return mapper.toResponseDTO(estudiante);
    }

    public EstudianteResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public EstudianteResponseDTO findByCodigoEstudiante(String codigoEstudiante) {
        return mapper.toResponseDTO(buscarUseCase.findByCodigoEstudiante(codigoEstudiante));
    }

    public List<EstudianteResponseDTO> findAll() {
        return buscarUseCase.findAllActive().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> findByProgramaAcademico(Long programaAcademicoId) {
        return buscarUseCase.findByProgramaAcademico(programaAcademicoId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> findByEstadoAcademico(String estadoAcademico) {
        return buscarUseCase.findByEstadoAcademico(estadoAcademico).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EstudianteResponseDTO> findEstudiantesActivos(Long programaAcademicoId) {
        return buscarUseCase.findEstudiantesActivos(programaAcademicoId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EstudianteResponseDTO cambiarEstadoAcademico(Long id, String nuevoEstado) {
        Estudiante estudiante = cambiarEstadoUseCase.execute(id, nuevoEstado);
        return mapper.toResponseDTO(estudiante);
    }

    @Transactional
    public void delete(Long id) {
        Estudiante estudiante = buscarUseCase.findById(id);
        estudiante.setActive(false);
    }
}
