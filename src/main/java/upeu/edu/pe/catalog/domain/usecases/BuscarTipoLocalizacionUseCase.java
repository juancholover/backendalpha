package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;
import upeu.edu.pe.academic.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarTipoLocalizacionUseCase {

    private final TipoLocalizacionRepository tipoLocalizacionRepository;

    @Inject
    public BuscarTipoLocalizacionUseCase(TipoLocalizacionRepository tipoLocalizacionRepository) {
        this.tipoLocalizacionRepository = tipoLocalizacionRepository;
    }

    public TipoLocalizacion findById(Long id) {
        return tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoLocalizacion not found with id " + id));
    }

    public List<TipoLocalizacion> findAll() {
        return tipoLocalizacionRepository.listAll();
    }

    public List<TipoLocalizacion> search(String query) {
        return tipoLocalizacionRepository.search(query);
    }
}
