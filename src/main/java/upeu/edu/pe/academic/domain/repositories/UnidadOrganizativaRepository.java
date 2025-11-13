package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UnidadOrganizativaRepository implements PanacheRepositoryBase<UnidadOrganizativa, Long> {

    /**
     * Buscar unidad organizativa por código
     */
    public Optional<UnidadOrganizativa> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo).firstResultOptional();
    }

    /**
     * Buscar unidad organizativa por sigla
     */
    public Optional<UnidadOrganizativa> findBySigla(String sigla) {
        return find("sigla = ?1 and active = true", sigla).firstResultOptional();
    }

    /**
     * Listar unidades raíz (sin padre) - Primer nivel
     */
    public List<UnidadOrganizativa> findUnidadesRaiz() {
        return find("unidadPadre is null and active = true").list();
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
    public List<UnidadOrganizativa> findByTipo(Long tipoUnidadId) {
        return find("tipoUnidad.id = ?1 and active = true", tipoUnidadId).list();
    }

    /**
     * Listar todas las unidades activas
     */
    public List<UnidadOrganizativa> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código
     */
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", codigo) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return count("codigo = ?1 and id != ?2 and active = true", codigo, id) > 0;
    }

    /**
     * Verificar si una unidad tiene unidades hijas
     */
    public boolean hasUnidadesHijas(Long unidadId) {
        return count("unidadPadre.id = ?1 and active = true", unidadId) > 0;
    }
}
