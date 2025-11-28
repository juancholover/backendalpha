package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;
import java.util.List;
import java.util.Optional;

public interface TipoLocalizacionRepository {
    TipoLocalizacion save(TipoLocalizacion tipoLocalizacion);

    Optional<TipoLocalizacion> findByIdOptional(Long id);

    List<TipoLocalizacion> listAll();

    void delete(Long id);

    List<TipoLocalizacion> search(String query);

    Optional<TipoLocalizacion> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
