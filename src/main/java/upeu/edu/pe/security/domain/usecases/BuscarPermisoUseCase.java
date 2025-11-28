package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarPermisoUseCase {

    private final PermisoRepository permisoRepository;

    @Inject
    public BuscarPermisoUseCase(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public Permiso findById(Long id) {
        return permisoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso", "id", id));
    }

    public List<Permiso> findAll() {
        return permisoRepository.findAllActive();
    }

    public Permiso findByNombreClave(String nombreClave) {
        return permisoRepository.findByNombreClave(nombreClave)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso", "nombreClave", nombreClave));
    }

    public List<Permiso> findByModulo(String modulo) {
        return permisoRepository.findByModulo(modulo);
    }

    public List<Permiso> findByRecurso(String recurso) {
        return permisoRepository.findByRecurso(recurso);
    }

    public List<Permiso> findByAccion(String accion) {
        return permisoRepository.findByAccion(accion);
    }

    public List<Permiso> findByRol(Long rolId) {
        return permisoRepository.findByRol(rolId);
    }

    public List<Permiso> search(String query) {
        return permisoRepository.search(query);
    }
}
