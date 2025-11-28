package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;
import upeu.edu.pe.academic.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarTipoUnidadUseCase {

    private final TipoUnidadRepository tipoUnidadRepository;

    @Inject
    public BuscarTipoUnidadUseCase(TipoUnidadRepository tipoUnidadRepository) {
        this.tipoUnidadRepository = tipoUnidadRepository;
    }

    public TipoUnidad findById(Long id) {
        return tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoUnidad not found with id " + id));
    }

    public List<TipoUnidad> findAll() {
        return tipoUnidadRepository.listAll();
    }

    public List<TipoUnidad> search(String query) {
        return tipoUnidadRepository.search(query);
    }
}
