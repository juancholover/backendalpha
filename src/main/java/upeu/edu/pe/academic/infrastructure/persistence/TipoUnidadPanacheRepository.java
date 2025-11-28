package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;
import upeu.edu.pe.academic.domain.repositories.TipoUnidadRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoUnidadPanacheRepository implements TipoUnidadRepository, PanacheRepository<TipoUnidad> {

    @Override
    public TipoUnidad save(TipoUnidad tipoUnidad) {
        persist(tipoUnidad);
        return tipoUnidad;
    }

    @Override
    public Optional<TipoUnidad> findByIdOptional(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    @Override
    public List<TipoUnidad> listAll() {
        return list("active = true ORDER BY nombre");
    }

    @Override
    public void delete(Long id) {
        findByIdOptional(id).ifPresent(tipoUnidad -> {
            tipoUnidad.setActive(false);
            persist(tipoUnidad);
        });
    }

    @Override
    public List<TipoUnidad> search(String query) {
        return list("active = true and (UPPER(nombre) like UPPER(?1) or UPPER(descripcion) like UPPER(?1))",
                "%" + query + "%");
    }

    @Override
    public Optional<TipoUnidad> findByNombre(String nombre) {
        return find("UPPER(nombre) = UPPER(?1) and active = true", nombre).firstResultOptional();
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return count("UPPER(nombre) = UPPER(?1) and active = true", nombre) > 0;
    }
}
