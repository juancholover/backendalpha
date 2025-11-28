package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class CambiarEstadoEstudianteUseCase {

    private final EstudianteRepository repository;

    @Inject
    public CambiarEstadoEstudianteUseCase(EstudianteRepository repository) {
        this.repository = repository;
    }

    public Estudiante execute(Long id, String nuevoEstado) {
        Estudiante estudiante = repository.findByIdOptional(id)
                .filter(Estudiante::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante", "id", id));

        if (!List.of("ACTIVO", "RETIRADO", "EGRESADO", "GRADUADO", "LICENCIA").contains(nuevoEstado)) {
            throw new BusinessRuleException("Estado académico inválido: " + nuevoEstado);
        }

        estudiante.setEstadoAcademico(nuevoEstado);
        repository.persist(estudiante);

        return estudiante;
    }
}
