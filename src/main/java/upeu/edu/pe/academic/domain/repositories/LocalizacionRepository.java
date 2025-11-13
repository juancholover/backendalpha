package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Localizacion;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LocalizacionRepository implements PanacheRepositoryBase<Localizacion, Long> {

    /**
     * Buscar localización por código
     */
    public Optional<Localizacion> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo).firstResultOptional();
    }

    /**
     * Listar localizaciones por universidad
     */
    public List<Localizacion> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    /**
     * Listar localizaciones por tipo
     */
    public List<Localizacion> findByTipo(Long tipoLocalizacionId) {
        return find("tipoLocalizacion.id = ?1 and active = true", tipoLocalizacionId).list();
    }

    /**
     * Listar localizaciones por departamento
     */
    public List<Localizacion> findByDepartamento(String departamento) {
        return find("departamento = ?1 and active = true", departamento).list();
    }

    /**
     * Listar todas las localizaciones activas
     */
    public List<Localizacion> findAllActive() {
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
}
