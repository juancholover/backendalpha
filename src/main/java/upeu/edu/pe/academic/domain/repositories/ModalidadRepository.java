package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Modalidad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ModalidadRepository implements PanacheRepositoryBase<Modalidad, Long> {

    /**
     * Busca una modalidad por su código
     */
    public Optional<Modalidad> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", 
                    codigo)
                .firstResultOptional();
    }

    /**
     * Obtiene todas las modalidades activas
     */
    public List<Modalidad> findAllActive() {
        return find("active = true")
                .list();
    }

    /**
     * Verifica si existe una modalidad con el código dado
     */
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", 
                     codigo) > 0;
    }

    /**
     * Busca modalidades por nombre (búsqueda parcial)
     */
    public List<Modalidad> findByNombreLike(String nombre) {
        return find("lower(nombre) like ?1 and active = true", 
                    "%" + nombre.toLowerCase() + "%")
                .list();
    }

    /**
     * Obtiene modalidades que requieren aula física
     */
    public List<Modalidad> findRequiereAula() {
        return find("requiereAula = true and active = true")
                .list();
    }

    /**
     * Obtiene modalidades que requieren plataforma digital
     */
    public List<Modalidad> findRequierePlataforma() {
        return find("requierePlataforma = true and active = true")
                .list();
    }
}
