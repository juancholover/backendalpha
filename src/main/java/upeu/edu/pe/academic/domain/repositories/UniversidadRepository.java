package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Universidad;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UniversidadRepository implements PanacheRepositoryBase<Universidad, Long> {

    /**
     * Buscar universidad por código
     */
    public Optional<Universidad> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo).firstResultOptional();
    }

    /**
     * Buscar universidad por dominio
     */
    public Optional<Universidad> findByDominio(String dominio) {
        return find("dominio = ?1 and active = true", dominio).firstResultOptional();
    }

    /**
     * Listar todas las universidades activas
     */
    public List<Universidad> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código (para validaciones)
     */
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", codigo) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID específico
     */
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return count("codigo = ?1 and id != ?2 and active = true", codigo, id) > 0;
    }

    /**
     * Verificar si existe dominio (para validaciones)
     */
    public boolean existsByDominio(String dominio) {
        return count("dominio = ?1 and active = true", dominio) > 0;
    }

    /**
     * Verificar si existe dominio excluyendo un ID específico
     */
    public boolean existsByDominioAndIdNot(String dominio, Long id) {
        return count("dominio = ?1 and id != ?2 and active = true", dominio, id) > 0;
    }

    /**
     * Buscar universidad por RUC
     */
    public Optional<Universidad> findByRuc(String ruc) {
        return find("ruc = ?1 and active = true", ruc).firstResultOptional();
    }

    /**
     * Verificar si existe RUC (para validaciones)
     */
    public boolean existsByRuc(String ruc) {
        return count("ruc = ?1 and active = true", ruc) > 0;
    }

    /**
     * Verificar si existe RUC excluyendo un ID específico
     */
    public boolean existsByRucAndIdNot(String ruc, Long id) {
        return count("ruc = ?1 and id != ?2 and active = true", ruc, id) > 0;
    }
}
