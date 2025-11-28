package upeu.edu.pe.catalog.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.TipoUnidadRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearTipoUnidadUseCase {

    private final TipoUnidadRepository tipoUnidadRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public CrearTipoUnidadUseCase(TipoUnidadRepository tipoUnidadRepository,
            UniversidadRepository universidadRepository) {
        this.tipoUnidadRepository = tipoUnidadRepository;
        this.universidadRepository = universidadRepository;
    }

    @Transactional
    public TipoUnidad execute(Long universidadId, String nombre, String descripcion, Integer nivel) {
        if (tipoUnidadRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("TipoUnidad with nombre " + nombre + " already exists");
        }

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad not found with id " + universidadId));

        TipoUnidad tipoUnidad = TipoUnidad.crear(universidad, nombre, descripcion, nivel);
        return tipoUnidadRepository.save(tipoUnidad);
    }
}
