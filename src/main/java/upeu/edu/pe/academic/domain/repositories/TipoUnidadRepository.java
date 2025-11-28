package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.TipoUnidad;
import java.util.List;
import java.util.Optional;

public interface TipoUnidadRepository {
    TipoUnidad save(TipoUnidad tipoUnidad);

    Optional<TipoUnidad> findByIdOptional(Long id);

    List<TipoUnidad> listAll();

    void delete(Long id);

    List<TipoUnidad> search(String query);

    Optional<TipoUnidad> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
