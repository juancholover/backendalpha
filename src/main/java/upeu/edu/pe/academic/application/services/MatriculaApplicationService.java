package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.academic.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.academic.application.mapper.MatriculaMapper;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.usecases.*;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class MatriculaApplicationService {

    @Inject
    MatricularEstudianteUseCase matricularUseCase;

    @Inject
    RetirarMatriculaUseCase retirarUseCase;

    @Inject
    BuscarMatriculaUseCase buscarUseCase;

    @Inject
    ActualizarMatriculaUseCase actualizarUseCase;

    @Inject
    EliminarMatriculaUseCase eliminarUseCase;

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    MatriculaMapper mapper;

    @Transactional
    public MatriculaResponseDTO create(MatriculaRequestDTO dto) {
        Estudiante estudiante = estudianteRepository.findByIdOptional(dto.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", dto.getEstudianteId()));

        Long universidadId = estudiante.getUniversidad().getId();

        Matricula matricula = matricularUseCase.execute(
                universidadId,
                dto.getEstudianteId(),
                dto.getSeccionId(),
                dto.getTipoMatricula());
        return mapper.toResponseDTO(matricula);
    }

    @Transactional
    public MatriculaResponseDTO update(Long id, MatriculaRequestDTO dto) {
        Matricula matricula = actualizarUseCase.execute(id, dto);
        return mapper.toResponseDTO(matricula);
    }

    @Transactional
    public MatriculaResponseDTO retirar(Long id) {
        Matricula matricula = retirarUseCase.execute(id);
        return mapper.toResponseDTO(matricula);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public MatriculaResponseDTO findById(Long id) {
        Matricula matricula = buscarUseCase.findById(id);
        return mapper.toResponseDTO(matricula);
    }

    public List<MatriculaResponseDTO> findByEstudiante(Long estudianteId) {
        return mapper.toResponseDTOList(buscarUseCase.findByEstudiante(estudianteId));
    }

    public List<MatriculaResponseDTO> findBySeccion(Long seccionId) {
        return mapper.toResponseDTOList(buscarUseCase.findByCursoOfertado(seccionId));
    }

    public List<MatriculaResponseDTO> findByPeriodoAcademico(Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPeriodo(periodoId));
    }

    public List<MatriculaResponseDTO> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByEstudianteAndPeriodo(estudianteId, periodoId));
    }

    public List<MatriculaResponseDTO> findByEstadoMatricula(String estadoMatricula) {
        return mapper.toResponseDTOList(buscarUseCase.findByEstadoMatricula(estadoMatricula));
    }

    public List<MatriculaResponseDTO> findMatriculasActivas(Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPeriodoAndActive(periodoId));
    }

    public List<MatriculaResponseDTO> findMatriculasAprobadas(Long estudianteId) {
        return mapper.toResponseDTOList(buscarUseCase.findByEstudianteAndAprobado(estudianteId));
    }

    public List<MatriculaResponseDTO> findByTipoMatricula(String tipoMatricula, Long periodoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByTipoAndPeriodo(tipoMatricula, periodoId));
    }

    public MatriculaResponseDTO findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        Matricula matricula = buscarUseCase.findByEstudianteAndCurso(estudianteId, seccionId);
        return mapper.toResponseDTO(matricula);
    }
}
