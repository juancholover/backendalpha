package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.TipoUnidad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoUnidadRepository implements PanacheRepositoryBase<TipoUnidad, Long> {

    /**
     * Buscar tipo de unidad por nombre
     */
    public Optional<TipoUnidad> findByNombre(String nombre) {
        return find("nombre = ?1 and active = true", nombre).firstResultOptional();
    }

    /**
     * Listar tipos por nivel jerárquico
     */
    public List<TipoUnidad> findByNivel(Integer nivel) {
        return find("nivel = ?1 and active = true", nivel).list();
    }

    /**
     * Listar todos los tipos activos ordenados por nivel
     */
    public List<TipoUnidad> findAllActiveOrderedByNivel() {
        return find("active = true order by nivel asc").list();
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
