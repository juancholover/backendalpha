package upeu.edu.pe.security.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PermisoPanacheRepository implements PermisoRepository, PanacheRepository<Permiso> {

    @Override
    public void persist(Permiso permiso) {
        PanacheRepository.super.persist(permiso);
    }

    @Override
    public Optional<Permiso> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public Optional<Permiso> findByNombreClave(String nombreClave) {
        return find("UPPER(nombreClave) = UPPER(?1) and active = true", nombreClave).firstResultOptional();
    }

    @Override
    public List<Permiso> findByModulo(String modulo) {
        return find("UPPER(modulo) = UPPER(?1) and active = true", modulo).list();
    }

    @Override
    public List<Permiso> findByRecurso(String recurso) {
        return find("UPPER(recurso) = UPPER(?1) and active = true", recurso).list();
    }

    @Override
    public List<Permiso> findByAccion(String accion) {
        return find("UPPER(accion) = UPPER(?1) and active = true", accion).list();
    }

    @Override
    public List<Permiso> findByModuloAndRecurso(String modulo, String recurso) {
        return find("UPPER(modulo) = UPPER(?1) and UPPER(recurso) = UPPER(?2) and active = true",
                modulo, recurso).list();
    }

    @Override
    public List<Permiso> findAllActive() {
        return find("active = true ORDER BY modulo, recurso, accion").list();
    }

    @Override
    public boolean existsByNombreClave(String nombreClave) {
        return count("UPPER(nombreClave) = UPPER(?1)", nombreClave) > 0;
    }

    @Override
    public List<Permiso> findByRol(Long rolId) {
        return find("SELECT p FROM Permiso p " +
                "JOIN RolPermiso rp ON rp.permiso.id = p.id " +
                "WHERE rp.rol.id = ?1 " +
                "AND p.active = true", rolId).list();
    }

    @Override
    public List<Permiso> search(String query) {
        String searchPattern = "%" + query.toUpperCase() + "%";
        return find("(UPPER(nombreClave) LIKE ?1 OR UPPER(descripcion) LIKE ?1) and active = true " +
                "ORDER BY modulo, recurso", searchPattern).list();
    }
}
