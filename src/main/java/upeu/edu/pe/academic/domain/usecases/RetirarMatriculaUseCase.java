package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Matricula;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.MatriculaRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class RetirarMatriculaUseCase {

    private final MatriculaRepository matriculaRepository;
    private final CursoOfertadoRepository cursoOfertadoRepository;
    private final EstudianteRepository estudianteRepository;

    @Inject
    public RetirarMatriculaUseCase(MatriculaRepository matriculaRepository,
            CursoOfertadoRepository cursoOfertadoRepository,
            EstudianteRepository estudianteRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoOfertadoRepository = cursoOfertadoRepository;
        this.estudianteRepository = estudianteRepository;
    }

    public Matricula execute(Long id) {
        Matricula matricula = matriculaRepository.findByIdOptional(id)
                .filter(Matricula::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Matricula", "id", id));

        matricula.retirar();

        // Actualizar créditos del estudiante (reducir créditos cursando)
        Estudiante estudiante = matricula.getEstudiante();
        Integer creditosActuales = estudiante.getCreditosCursando() != null ? estudiante.getCreditosCursando() : 0;
        Integer creditosCurso = matricula.getCreditosMatriculados() != null ? matricula.getCreditosMatriculados() : 0;
        estudiante.setCreditosCursando(Math.max(0, creditosActuales - creditosCurso));

        // Aumentar vacantes en el curso
        CursoOfertado curso = matricula.getCursoOfertado();
        curso.aumentarVacantes();

        matriculaRepository.persist(matricula);
        cursoOfertadoRepository.persist(curso);
        estudianteRepository.persist(estudiante);

        return matricula;
    }
}
