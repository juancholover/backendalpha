package upeu.edu.pe.security.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.security.domain.entities.Permiso;
import upeu.edu.pe.security.domain.repositories.PermisoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;

@ApplicationScoped
public class CrearPermisoUseCase {

    private final PermisoRepository permisoRepository;

    @Inject
    public CrearPermisoUseCase(PermisoRepository permisoRepository) {
        this.permisoRepository = permisoRepository;
    }

    public Permiso execute(String nombreClave, String descripcion, String modulo, String recurso, String accion) {
        // Generar nombre clave si no existe para verificar duplicados
        String finalNombreClave = nombreClave;
        if (finalNombreClave == null || finalNombreClave.trim().isEmpty()) {
            if (modulo != null && accion != null && recurso != null) {
                finalNombreClave = String.format("%s_%s_%s", modulo, accion, recurso)
                        .toUpperCase()
                        .replaceAll("\\s+", "_");
            }
        }

        if (finalNombreClave != null && permisoRepository.existsByNombreClave(finalNombreClave)) {
            throw new BusinessException(
                    "Ya existe un permiso con esa combinación de módulo, acción y recurso o nombre clave");
        }

        Permiso permiso = Permiso.crear(nombreClave, descripcion, modulo, recurso, accion);
        permisoRepository.persist(permiso);
        return permiso;
    }
}
