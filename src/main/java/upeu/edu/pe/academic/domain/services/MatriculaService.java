package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.academic.application.dto.MatriculaResponseDTO;
import upeu.edu.pe.academic.application.mapper.MatriculaMapper;
import upeu.edu.pe.academic.domain.entities.*;
import upeu.edu.pe.academic.domain.repositories.*;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class MatriculaService {

    @Inject
    MatriculaRepository matriculaRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    MatriculaMapper matriculaMapper;

    public List<MatriculaResponseDTO> findByEstudiante(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudiante(estudianteId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findBySeccion(Long seccionId) {
        List<Matricula> matriculas = matriculaRepository.findBySeccion(seccionId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByPeriodoAcademico(Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findByPeriodoAcademico(periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudianteAndPeriodo(estudianteId, periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByEstadoMatricula(String estadoMatricula) {
        List<Matricula> matriculas = matriculaRepository.findByEstadoMatricula(estadoMatricula);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findMatriculasActivas(Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findMatriculasActivas(periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findMatriculasAprobadas(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findMatriculasAprobadas(estudianteId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public List<MatriculaResponseDTO> findByTipoMatricula(String tipoMatricula, Long periodoId) {
        List<Matricula> matriculas = matriculaRepository.findByTipoMatricula(tipoMatricula, periodoId);
        return matriculaMapper.toResponseDTOList(matriculas);
    }

    public MatriculaResponseDTO findById(Long id) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + id));
        return matriculaMapper.toResponseDTO(matricula);
    }

    public MatriculaResponseDTO findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        Matricula matricula = matriculaRepository.findByEstudianteAndSeccion(estudianteId, seccionId)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada para el estudiante y sección especificados"));
        return matriculaMapper.toResponseDTO(matricula);
    }

    @Transactional
    public MatriculaResponseDTO create(MatriculaRequestDTO requestDTO) {
        // Validar que no exista la matrícula
        if (matriculaRepository.existsByEstudianteAndSeccion(requestDTO.getEstudianteId(), requestDTO.getSeccionId())) {
            throw new BusinessException("El estudiante ya está matriculado en esta sección");
        }

        // Validar estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(requestDTO.getEstudianteId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + requestDTO.getEstudianteId()));

        // Validar curso ofertado
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(requestDTO.getSeccionId())
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + requestDTO.getSeccionId()));

        // Verificar cupo disponible
        if (!cursoOfertado.hayCupoDisponible()) {
            throw new BusinessException("El curso ofertado no tiene cupo disponible");
        }

        // Crear matrícula
        Matricula matricula = matriculaMapper.toEntity(requestDTO);
        matricula.setEstudiante(estudiante);
        matricula.setCursoOfertado(cursoOfertado);

        // Reducir vacantes del curso ofertado
        cursoOfertado.reducirVacantes();
        cursoOfertadoRepository.persist(cursoOfertado);

        matriculaRepository.persist(matricula);
        return matriculaMapper.toResponseDTO(matricula);
    }

    @Transactional
    public MatriculaResponseDTO update(Long id, MatriculaRequestDTO requestDTO) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + id));

        // Validar cambio de sección
        if (!matricula.getCursoOfertado().getId().equals(requestDTO.getSeccionId())) {
            // Verificar que no exista matrícula en la nueva sección
            if (matriculaRepository.existsByEstudianteAndSeccion(requestDTO.getEstudianteId(), requestDTO.getSeccionId())) {
                throw new BusinessException("El estudiante ya está matriculado en la nueva sección");
            }

            CursoOfertado nuevoCursoOfertado = cursoOfertadoRepository.findByIdOptional(requestDTO.getSeccionId())
                    .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + requestDTO.getSeccionId()));

            // Verificar cupo en nuevo curso ofertado
            if (!nuevoCursoOfertado.hayCupoDisponible()) {
                throw new BusinessException("El nuevo curso ofertado no tiene cupo disponible");
            }

            // Liberar cupo en curso ofertado anterior
            CursoOfertado cursoOfertadoAnterior = matricula.getCursoOfertado();
            cursoOfertadoAnterior.aumentarVacantes();
            cursoOfertadoRepository.persist(cursoOfertadoAnterior);

            // Reducir cupo en nuevo curso ofertado
            nuevoCursoOfertado.reducirVacantes();
            cursoOfertadoRepository.persist(nuevoCursoOfertado);

            matricula.setCursoOfertado(nuevoCursoOfertado);
        }

        // Validar cambio de estudiante
        if (!matricula.getEstudiante().getId().equals(requestDTO.getEstudianteId())) {
            Estudiante nuevoEstudiante = estudianteRepository.findByIdOptional(requestDTO.getEstudianteId())
                    .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + requestDTO.getEstudianteId()));
            matricula.setEstudiante(nuevoEstudiante);
        }

        matriculaMapper.updateEntityFromDTO(requestDTO, matricula);
        matriculaRepository.persist(matricula);
        return matriculaMapper.toResponseDTO(matricula);
    }

    @Transactional
    public MatriculaResponseDTO retirar(Long id) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + id));

        // Retirar estudiante
        matricula.retirar();

        // Liberar cupo en el curso ofertado
        CursoOfertado cursoOfertado = matricula.getCursoOfertado();
        cursoOfertado.aumentarVacantes();
        cursoOfertadoRepository.persist(cursoOfertado);

        matriculaRepository.persist(matricula);
        return matriculaMapper.toResponseDTO(matricula);
    }

    @Transactional
    public void delete(Long id) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Matrícula no encontrada con ID: " + id));

        // Liberar cupo en el curso ofertado si está activa
        if ("MATRICULADO".equals(matricula.getEstadoMatricula())) {
            CursoOfertado cursoOfertado = matricula.getCursoOfertado();
            cursoOfertado.aumentarVacantes();
            cursoOfertadoRepository.persist(cursoOfertado);
        }

        // Soft delete
        matricula.setActive(false);
        matriculaRepository.persist(matricula);
    }
}
