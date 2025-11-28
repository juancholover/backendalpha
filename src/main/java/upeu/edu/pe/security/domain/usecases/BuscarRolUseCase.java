package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Rol;
import upeu.edu.pe.security.domain.repositories.AuthUsuarioRepository;
import upeu.edu.pe.security.domain.repositories.RolRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarRolUseCase {

    private final RolRepository rolRepository;

    private final AuthUsuarioRepository authUsuarioRepository;

    @Inject
    public BuscarRolUseCase(RolRepository rolRepository, AuthUsuarioRepository authUsuarioRepository) {
        this.rolRepository = rolRepository;
        this.authUsuarioRepository = authUsuarioRepository;
    }

    public Rol findById(Long id) {
        return rolRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", "id", id));
    }

    public List<Rol> findAll() {
        return rolRepository.findAllRoles();
    }

    public List<Rol> findByUniversidad(Long universidadId) {
        return rolRepository.findByUniversidad(universidadId);
    }

    public List<Rol> findRolesSistema() {
        return rolRepository.findAllRoles().stream()
                .filter(Rol::getEsSistema)
                .toList();
    }

    public boolean existsByNombre(String nombre, Long universidadId) {
        return rolRepository.existsByNombre(nombre, universidadId);
    }

    public List<Rol> findByPermisoNombre(String permisoNombre, Long universidadId) {
        return rolRepository.findByPermisoNombre(permisoNombre, universidadId);
    }

    public long countUsuariosConRol(Long rolId) {
        return authUsuarioRepository.countByRol(rolId);
    }
}
