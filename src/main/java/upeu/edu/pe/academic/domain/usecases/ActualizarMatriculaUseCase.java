package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.application.dto.MatriculaRequestDTO;
import upeu.edu.pe.academic.application.mapper.MatriculaMapper;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.MatriculaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarMatriculaUseCase {

    private final MatriculaRepository matriculaRepository;
    private final CursoOfertadoRepository cursoOfertadoRepository;
    private final EstudianteRepository estudianteRepository;
    private final MatriculaMapper matriculaMapper;

    @Inject
    public ActualizarMatriculaUseCase(MatriculaRepository matriculaRepository,
            CursoOfertadoRepository cursoOfertadoRepository,
            EstudianteRepository estudianteRepository,
            MatriculaMapper matriculaMapper) {
        this.matriculaRepository = matriculaRepository;
        this.cursoOfertadoRepository = cursoOfertadoRepository;
        this.estudianteRepository = estudianteRepository;
        this.matriculaMapper = matriculaMapper;
    }

    public Matricula execute(Long id, MatriculaRequestDTO requestDTO) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", "id", id));

        // Validar cambio de sección
        if (!matricula.getCursoOfertado().getId().equals(requestDTO.getSeccionId())) {
            // Verificar que no exista matrícula en la nueva sección
            if (matriculaRepository.existsByEstudianteAndCurso(requestDTO.getEstudianteId(),
                    requestDTO.getSeccionId())) {
                throw new BusinessException("El estudiante ya está matriculado en la nueva sección");
            }

            CursoOfertado nuevoCursoOfertado = cursoOfertadoRepository.findByIdOptional(requestDTO.getSeccionId())
                    .orElseThrow(() -> new ResourceNotFoundException("CursoOfertado", "id", requestDTO.getSeccionId()));

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
                    .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", requestDTO.getEstudianteId()));
            matricula.setEstudiante(nuevoEstudiante);
        }

        matriculaMapper.updateEntityFromDTO(requestDTO, matricula);
        matriculaRepository.persist(matricula);

        return matricula;
    }
}
