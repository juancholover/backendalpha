package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.RequisitoCurso;
import upeu.edu.pe.curriculum.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarRequisitoCursoUseCase {

    @Inject
    RequisitoCursoRepository requisitoRepository;

    @Transactional
    public void execute(Long id) {
        RequisitoCurso requisito = requisitoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Requisito no encontrado con ID: " + id));

        requisitoRepository.delete(requisito);
    }
}
