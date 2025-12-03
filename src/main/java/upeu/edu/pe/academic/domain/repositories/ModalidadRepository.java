package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Modalidad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ModalidadRepository implements PanacheRepositoryBase<Modalidad, Long> {

    /**
     * Busca una modalidad por su código en una universidad
     */
    public Optional<Modalidad> findByCodigo(String codigo, Long universidadId) {
        return find("codigo = ?1 and universidad.id = ?2 and active = true", 
                    codigo, universidadId)
                .firstResultOptional();
    }

    /**
     * Obtiene todas las modalidades activas de una universidad
     */
    public List<Modalidad> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId)
                .list();
    }

    /**
     * Verifica si existe una modalidad con el código dado en la universidad
     */
    public boolean existsByCodigo(String codigo, Long universidadId) {
        return count("codigo = ?1 and universidad.id = ?2 and active = true", 
                     codigo, universidadId) > 0;
    }

    /**
     * Busca modalidades por nombre (búsqueda parcial)
     */
    public List<Modalidad> findByNombreLike(String nombre, Long universidadId) {
        return find("lower(nombre) like ?1 and universidad.id = ?2 and active = true", 
                    "%" + nombre.toLowerCase() + "%", universidadId)
                .list();
    }

    /**
     * Obtiene modalidades que requieren aula física
     */
    public List<Modalidad> findRequiereAula(Long universidadId) {
        return find("requiereAula = true and universidad.id = ?1 and active = true", 
                    universidadId)
                .list();
    }

    /**
     * Obtiene modalidades que requieren plataforma digital
     */
    public List<Modalidad> findRequierePlataforma(Long universidadId) {
        return find("requierePlataforma = true and universidad.id = ?1 and active = true", 
                    universidadId)
                .list();
    }
}
