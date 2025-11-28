package upeu.edu.pe.security.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.RolPermiso;
import upeu.edu.pe.security.domain.repositories.RolPermisoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RolPermisoPanacheRepository implements RolPermisoRepository, PanacheRepository<RolPermiso> {

    @Override
    public void persist(RolPermiso rolPermiso) {
        PanacheRepository.super.persist(rolPermiso);
    }

    @Override
    public Optional<RolPermiso> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public List<RolPermiso> findByRol(Long rolId) {
        return find("rol.id = ?1 and active = true", rolId).list();
    }

    @Override
    public List<RolPermiso> findByPermiso(Long permisoId) {
        return find("permiso.id = ?1 and active = true", permisoId).list();
    }

    @Override
    public boolean existsByRolAndPermiso(Long rolId, Long permisoId) {
        return count("rol.id = ?1 and permiso.id = ?2", rolId, permisoId) > 0;
    }

    @Override
    public Optional<RolPermiso> findByRolAndPermiso(Long rolId, Long permisoId) {
        return find("rol.id = ?1 and permiso.id = ?2", rolId, permisoId).firstResultOptional();
    }

    @Override
    public List<RolPermiso> findDelegablesByRol(Long rolId) {
        return find("rol.id = ?1 and puedeDeleagar = true and active = true", rolId).list();
    }

    @Override
    public long deleteByRol(Long rolId) {
        return delete("rol.id = ?1", rolId);
    }

    @Override
    public long countByRol(Long rolId) {
        return count("rol.id = ?1", rolId);
    }

    @Override
    public List<RolPermiso> findByRolAndModulo(Long rolId, String modulo) {
        return find("rol.id = ?1 and UPPER(permiso.modulo) = UPPER(?2) and active = true",
                rolId, modulo).list();
    }

    @Override
    public void delete(RolPermiso rolPermiso) {
        PanacheRepository.super.delete(rolPermiso);
    }
}
