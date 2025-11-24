package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UnidadOrganizativaRepository implements PanacheRepositoryBase<UnidadOrganizativa, Long> {

    /**
     * Buscar unidad organizativa por código en una universidad específica
     */
    public Optional<UnidadOrganizativa> findByCodigoAndUniversidad(String codigo, Long universidadId) {
        return find("codigo = ?1 and universidad.id = ?2 and active = true", codigo, universidadId)
                .firstResultOptional();
    }

    /**
     * Buscar unidad organizativa por sigla en una universidad específica
     */
    public Optional<UnidadOrganizativa> findBySiglaAndUniversidad(String sigla, Long universidadId) {
        return find("sigla = ?1 and universidad.id = ?2 and active = true", sigla, universidadId)
                .firstResultOptional();
    }

    /**
     * Listar unidades organizativas por universidad
     */
    public List<UnidadOrganizativa> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    /**
     * Listar unidades raíz (sin padre) por universidad
     */
    public List<UnidadOrganizativa> findRootUnidades(Long universidadId) {
        return find("universidad.id = ?1 and unidadPadre is null and active = true", universidadId).list();
    }

    /**
     * Listar unidades hijas de una unidad padre
     */
    public List<UnidadOrganizativa> findByUnidadPadre(Long unidadPadreId) {
        return find("unidadPadre.id = ?1 and active = true", unidadPadreId).list();
    }

    /**
     * Listar unidades por localización
     */
    public List<UnidadOrganizativa> findByLocalizacion(Long localizacionId) {
        return find("localizacion.id = ?1 and active = true", localizacionId).list();
    }

    /**
     * Listar unidades por tipo
     */
    public List<UnidadOrganizativa> findByTipoUnidad(Long tipoUnidadId) {
        return find("tipoUnidad.id = ?1 and active = true", tipoUnidadId).list();
    }

    /**
     * Listar unidades por tipo y universidad
     */
    public List<UnidadOrganizativa> findByTipoUnidadAndUniversidad(Long tipoUnidadId, Long universidadId) {
        return find("tipoUnidad.id = ?1 and universidad.id = ?2 and active = true", tipoUnidadId, universidadId)
                .list();
    }

    /**
     * Buscar con todas las relaciones cargadas
     */
    public Optional<UnidadOrganizativa> findByIdWithRelations(Long id) {
        return find("SELECT u FROM UnidadOrganizativa u " +
                "LEFT JOIN FETCH u.universidad " +
                "LEFT JOIN FETCH u.tipoUnidad " +
                "LEFT JOIN FETCH u.localizacion " +
                "LEFT JOIN FETCH u.unidadPadre " +
                "WHERE u.id = ?1 and u.active = true", id)
                .firstResultOptional();
    }

    /**
     * Listar todas las unidades activas
     */
    public List<UnidadOrganizativa> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código en una universidad
     */
    public boolean existsByCodigoAndUniversidad(String codigo, Long universidadId) {
        return count("codigo = ?1 and universidad.id = ?2 and active = true", codigo, universidadId) > 0;
    }

    /**
     * Verificar si existe código en una universidad excluyendo un ID
     */
    public boolean existsByCodigoAndUniversidadAndIdNot(String codigo, Long universidadId, Long id) {
        return count("codigo = ?1 and universidad.id = ?2 and id != ?3 and active = true", 
                codigo, universidadId, id) > 0;
    }

    /**
     * Verificar si existe nombre en una universidad
     */
    public boolean existsByNombreAndUniversidad(String nombre, Long universidadId) {
        return count("nombre = ?1 and universidad.id = ?2 and active = true", nombre, universidadId) > 0;
    }

    /**
     * Verificar si existe nombre en una universidad excluyendo un ID
     */
    public boolean existsByNombreAndUniversidadAndIdNot(String nombre, Long universidadId, Long id) {
        return count("nombre = ?1 and universidad.id = ?2 and id != ?3 and active = true", 
                nombre, universidadId, id) > 0;
    }

    /**
     * Verificar si una unidad tiene unidades hijas
     */
    public boolean hasUnidadesHijas(Long unidadId) {
        return count("unidadPadre.id = ?1 and active = true", unidadId) > 0;
    }

    /**
     * Contar unidades por universidad
     */
    public long countByUniversidad(Long universidadId) {
        return count("universidad.id = ?1 and active = true", universidadId);
    }
}
