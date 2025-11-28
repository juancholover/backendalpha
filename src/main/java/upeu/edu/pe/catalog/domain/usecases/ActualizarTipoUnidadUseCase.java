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
public class ActualizarTipoUnidadUseCase {

    private final TipoUnidadRepository tipoUnidadRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public ActualizarTipoUnidadUseCase(TipoUnidadRepository tipoUnidadRepository,
            UniversidadRepository universidadRepository) {
        this.tipoUnidadRepository = tipoUnidadRepository;
        this.universidadRepository = universidadRepository;
    }

    @Transactional
    public TipoUnidad execute(Long id, Long universidadId, String nombre, String descripcion, Integer nivel) {
        TipoUnidad tipoUnidad = tipoUnidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("TipoUnidad not found with id " + id));

        if (!tipoUnidad.getNombre().equalsIgnoreCase(nombre) && tipoUnidadRepository.existsByNombre(nombre)) {
            throw new IllegalArgumentException("TipoUnidad with nombre " + nombre + " already exists");
        }

        if (universidadId != null) {
            Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                    .orElseThrow(() -> new ResourceNotFoundException("Universidad not found with id " + universidadId));
            tipoUnidad.setUniversidad(universidad);
        }

        tipoUnidad.setNombre(nombre);
        tipoUnidad.setDescripcion(descripcion);
        tipoUnidad.setNivel(nivel);
        return tipoUnidadRepository.save(tipoUnidad);
    }
}
