package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;
import upeu.edu.pe.academic.domain.repositories.TipoLocalizacionRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoLocalizacionPanacheRepository
        implements TipoLocalizacionRepository, PanacheRepository<TipoLocalizacion> {

    @Override
    public TipoLocalizacion save(TipoLocalizacion tipoLocalizacion) {
        persist(tipoLocalizacion);
        return tipoLocalizacion;
    }

    @Override
    public Optional<TipoLocalizacion> findByIdOptional(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    @Override
    public List<TipoLocalizacion> listAll() {
        return list("active = true ORDER BY nombre");
    }

    @Override
    public void delete(Long id) {
        findByIdOptional(id).ifPresent(tipoLocalizacion -> {
            tipoLocalizacion.setActive(false);
            persist(tipoLocalizacion);
        });
    }

    @Override
    public List<TipoLocalizacion> search(String query) {
        return list("active = true and UPPER(nombre) like UPPER(?1)", "%" + query + "%");
    }

    @Override
    public Optional<TipoLocalizacion> findByNombre(String nombre) {
        return find("UPPER(nombre) = UPPER(?1) and active = true", nombre).firstResultOptional();
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return count("UPPER(nombre) = UPPER(?1) and active = true", nombre) > 0;
    }
}
