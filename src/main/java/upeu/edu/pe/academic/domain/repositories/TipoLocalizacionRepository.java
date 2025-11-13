package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.TipoLocalizacion;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoLocalizacionRepository implements PanacheRepositoryBase<TipoLocalizacion, Long> {

    /**
     * Buscar tipo de localización por nombre
     */
    public Optional<TipoLocalizacion> findByNombre(String nombre) {
        return find("nombre = ?1 and active = true", nombre).firstResultOptional();
    }

    /**
     * Listar todos los tipos activos
     */
    public List<TipoLocalizacion> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe un tipo con ese nombre
     */
    public boolean existsByNombre(String nombre) {
        return count("nombre = ?1 and active = true", nombre) > 0;
    }

    /**
     * Verificar si existe nombre excluyendo un ID
     */
    public boolean existsByNombreAndIdNot(String nombre, Long id) {
        return count("nombre = ?1 and id != ?2 and active = true", nombre, id) > 0;
    }
}
