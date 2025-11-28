package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;
import upeu.edu.pe.academic.domain.repositories.TipoLocalizacionRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarTipoLocalizacionUseCase {

    private final TipoLocalizacionRepository tipoLocalizacionRepository;

    @Inject
    public ActualizarTipoLocalizacionUseCase(TipoLocalizacionRepository tipoLocalizacionRepository) {
        this.tipoLocalizacionRepository = tipoLocalizacionRepository;
    }

    @Transactional
    public TipoLocalizacion execute(Long id, String nombre) {
        TipoLocalizacion tipoLocalizacion = tipoLocalizacionRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoLocalizacion not found with id " + id));

        if (!tipoLocalizacion.getNombre().equalsIgnoreCase(nombre)
                && tipoLocalizacionRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("TipoLocalizacion with nombre " + nombre + " already exists");
        }

        tipoLocalizacion.setNombre(nombre);
        return tipoLocalizacionRepository.save(tipoLocalizacion);
    }
}
