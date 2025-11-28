package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarTipoLocalizacionUseCase {

    private final TipoLocalizacionRepository tipoLocalizacionRepository;

    @Inject
    public EliminarTipoLocalizacionUseCase(TipoLocalizacionRepository tipoLocalizacionRepository) {
        this.tipoLocalizacionRepository = tipoLocalizacionRepository;
    }

    @Transactional
    public void execute(Long id) {
        if (tipoLocalizacionRepository.findByIdOptional(id).isEmpty()) {
            throw new ResourceNotFoundException("TipoLocalizacion not found with id " + id);
        }
        tipoLocalizacionRepository.delete(id);
    }
}
