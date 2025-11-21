package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.TipoAutoridad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TipoAutoridadRepository implements PanacheRepository<TipoAutoridad> {

    /**
     * Busca todos los tipos de autoridad de una universidad ordenados por nivel jerárquico
     */
    public List<TipoAutoridad> findByUniversidadIdOrderByNivel(Long universidadId) {
        return list("universidadId = ?1 order by nivelJerarquia asc", universidadId);
    }

    /**
     * Busca un tipo de autoridad por nombre y universidad
     */
    public Optional<TipoAutoridad> findByNombreAndUniversidadId(String nombre, Long universidadId) {
        return find("UPPER(nombre) = UPPER(?1) and universidadId = ?2", nombre, universidadId)
                .firstResultOptional();
    }

    /**
     * Verifica si existe un tipo de autoridad con ese nombre en la universidad
     */
    public boolean existsByNombreAndUniversidadId(String nombre, Long universidadId) {
        return count("UPPER(nombre) = UPPER(?1) and universidadId = ?2", nombre, universidadId) > 0;
    }

    /**
     * Verifica si existe un tipo de autoridad con ese nombre, excluyendo un ID específico
     */
    public boolean existsByNombreAndUniversidadIdAndIdNot(String nombre, Long universidadId, Long id) {
        return count("UPPER(nombre) = UPPER(?1) and universidadId = ?2 and id != ?3", 
                     nombre, universidadId, id) > 0;
    }

    /**
     * Obtiene el tipo de autoridad con mayor jerarquía (nivel 1) de una universidad
     */
    public Optional<TipoAutoridad> findMaximaAutoridadByUniversidadId(Long universidadId) {
        return find("universidadId = ?1 order by nivelJerarquia asc", universidadId)
                .firstResultOptional();
    }
}
