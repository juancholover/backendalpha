package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarTipoUnidadUseCase {

    private final TipoUnidadRepository tipoUnidadRepository;

    @Inject
    public EliminarTipoUnidadUseCase(TipoUnidadRepository tipoUnidadRepository) {
        this.tipoUnidadRepository = tipoUnidadRepository;
    }

    @Transactional
    public void execute(Long id) {
        if (tipoUnidadRepository.findByIdOptional(id).isEmpty()) {
            throw new ResourceNotFoundException("TipoUnidad not found with id " + id);
        }
        tipoUnidadRepository.delete(id);
    }
}
