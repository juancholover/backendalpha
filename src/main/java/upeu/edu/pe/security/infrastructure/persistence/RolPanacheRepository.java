package upeu.edu.pe.security.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.RolRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RolPanacheRepository implements RolRepository, PanacheRepository<Rol> {

    @Override
    public void persist(Rol rol) {
        PanacheRepository.super.persist(rol);
    }

    @Override
    public Optional<Rol> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public Optional<Rol> findByNombre(String nombre) {
        return find("UPPER(nombre) = ?1", nombre.toUpperCase()).firstResultOptional();
    }

    @Override
    public List<Rol> findAllRoles() {
        return PanacheRepository.super.findAll().list();
    }

    @Override
    public List<Rol> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1", universidadId).list();
    }

    @Override
    public List<Rol> findByPermisoNombre(String permisoNombre, Long universidadId) {
        return find(
                "SELECT DISTINCT r FROM Rol r JOIN r.rolPermisos rp WHERE rp.permiso = ?1 AND r.universidad.id = ?2",
                permisoNombre, universidadId).list();
    }

    @Override
    public boolean existsByNombre(String nombre, Long universidadId) {
        return count("UPPER(nombre) = ?1 and universidad.id = ?2", nombre.toUpperCase(), universidadId) > 0;
    }

    @Override
    public void delete(Rol rol) {
        PanacheRepository.super.delete(rol);
    }
}
