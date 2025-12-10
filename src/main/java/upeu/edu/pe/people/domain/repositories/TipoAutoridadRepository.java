package upeu.edu.pe.people.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.people.domain.entities.TipoAutoridad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoAutoridadRepository implements PanacheRepository<TipoAutoridad> {

    /**
     * Busca todos los tipos de autoridad ordenados por nivel jerárquico
     */
    public List<TipoAutoridad> findAllOrderByNivel() {
        return list("order by nivelJerarquia asc");
    }

    /**
     * Busca un tipo de autoridad por nombre
     */
    public Optional<TipoAutoridad> findByNombre(String nombre) {
        return find("UPPER(nombre) = UPPER(?1)", nombre)
                .firstResultOptional();
    }

    /**
     * Verifica si existe un tipo de autoridad con ese nombre
     */
    public boolean existsByNombre(String nombre) {
        return count("UPPER(nombre) = UPPER(?1)", nombre) > 0;
    }

    /**
     * Verifica si existe un tipo de autoridad con ese nombre, excluyendo un ID específico
     */
    public boolean existsByNombreAndIdNot(String nombre, Long id) {
        return count("UPPER(nombre) = UPPER(?1) and id != ?2", 
                     nombre, id) > 0;
    }

    /**
     * Obtiene el tipo de autoridad con mayor jerarquía (nivel 1)
     */
    public Optional<TipoAutoridad> findMaximaAutoridad() {
        return find("order by nivelJerarquia asc")
                .firstResultOptional();
    }
}

