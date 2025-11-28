package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;
import upeu.edu.pe.academic.domain.repositories.TipoLocalizacionRepository;

@ApplicationScoped
public class CrearTipoLocalizacionUseCase {

    private final TipoLocalizacionRepository tipoLocalizacionRepository;

    @Inject
    public CrearTipoLocalizacionUseCase(TipoLocalizacionRepository tipoLocalizacionRepository) {
        this.tipoLocalizacionRepository = tipoLocalizacionRepository;
    }

    @Transactional
    public TipoLocalizacion execute(String nombre) {
        if (tipoLocalizacionRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("TipoLocalizacion with nombre " + nombre + " already exists");
        }
        TipoLocalizacion tipoLocalizacion = TipoLocalizacion.crear(nombre);
        return tipoLocalizacionRepository.save(tipoLocalizacion);
    }
}
