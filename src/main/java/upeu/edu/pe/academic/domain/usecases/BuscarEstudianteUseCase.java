package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarEstudianteUseCase {

    private final EstudianteRepository repository;

    @Inject
    public BuscarEstudianteUseCase(EstudianteRepository repository) {
        this.repository = repository;
    }

    public Estudiante findById(Long id) {
        return repository.findByIdOptional(id)
                .filter(Estudiante::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));
    }

    public Estudiante findByCodigoEstudiante(String codigoEstudiante) {
        return repository.findByCodigoEstudiante(codigoEstudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "codigoEstudiante", codigoEstudiante));
    }

    public List<Estudiante> findAllActive() {
        return repository.findAllActive();
    }

    public List<Estudiante> findByProgramaAcademico(Long programaAcademicoId) {
        return repository.findByProgramaAcademico(programaAcademicoId);
    }

    public List<Estudiante> findByEstadoAcademico(String estadoAcademico) {
        return repository.findByEstadoAcademico(estadoAcademico);
    }

    public List<Estudiante> findEstudiantesActivos(Long programaAcademicoId) {
        return repository.findEstudiantesActivos(programaAcademicoId);
    }
}
