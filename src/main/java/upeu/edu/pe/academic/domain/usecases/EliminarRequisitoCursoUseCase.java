package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarRequisitoCursoUseCase {

    private final RequisitoCursoRepository requisitoRepository;

    @Inject
    public EliminarRequisitoCursoUseCase(RequisitoCursoRepository requisitoRepository) {
        this.requisitoRepository = requisitoRepository;
    }

    public void execute(Long id) {
        RequisitoCurso requisito = requisitoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("RequisitoCurso", "id", id));

        requisito.setActive(false);
        requisitoRepository.persist(requisito);
    }
}
