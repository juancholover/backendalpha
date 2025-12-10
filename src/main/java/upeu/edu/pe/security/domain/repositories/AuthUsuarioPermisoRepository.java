package upeu.edu.pe.security.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.security.domain.entities.AuthUsuarioPermiso;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AuthUsuarioPermisoRepository implements PanacheRepository<AuthUsuarioPermiso> {

    /**
     * Obtiene todos los permisos individuales de un usuario (vigentes y expirados)
     */
    public List<AuthUsuarioPermiso> findByUsuarioId(Long authUsuarioId) {
        return list("authUsuario.id", authUsuarioId);
    }

    /**
     * Obtiene solo los permisos individuales vigentes de un usuario
     */
    public List<AuthUsuarioPermiso> findPermisosVigentes(Long authUsuarioId) {
        LocalDateTime ahora = LocalDateTime.now();
        return list("authUsuario.id = ?1 AND (fechaExpiracion IS NULL OR fechaExpiracion > ?2)", 
                    authUsuarioId, ahora);
    }

    /**
     * Obtiene permisos individuales expirados de un usuario
     */
    public List<AuthUsuarioPermiso> findPermisosExpirados(Long authUsuarioId) {
        LocalDateTime ahora = LocalDateTime.now();
        return list("authUsuario.id = ?1 AND fechaExpiracion IS NOT NULL AND fechaExpiracion <= ?2", 
                    authUsuarioId, ahora);
    }

    /**
     * Verifica si un usuario tiene un permiso individual específico vigente
     */
    public boolean usuarioTienePermisoVigente(Long authUsuarioId, Long permisoId) {
        LocalDateTime ahora = LocalDateTime.now();
        long count = count("authUsuario.id = ?1 AND permiso.id = ?2 AND esAdicion = true " +
                          "AND (fechaExpiracion IS NULL OR fechaExpiracion > ?3)", 
                          authUsuarioId, permisoId, ahora);
        return count > 0;
    }

    /**
     * Verifica si un usuario tiene un permiso individual específico vigente por código
     */
    public boolean usuarioTienePermisoVigenteByCodigo(Long authUsuarioId, String codigoPermiso) {
        LocalDateTime ahora = LocalDateTime.now();
        long count = count("authUsuario.id = ?1 AND permiso.codigo = ?2 AND esAdicion = true " +
                          "AND (fechaExpiracion IS NULL OR fechaExpiracion > ?3)", 
                          authUsuarioId, codigoPermiso, ahora);
        return count > 0;
    }

    /**
     * Obtiene todos los permisos individuales asignados por un usuario específico
     */
    public List<AuthUsuarioPermiso> findByAsignadoPor(Long asignadoPorId) {
        return list("asignadoPor.id", asignadoPorId);
    }

    /**
     * Obtiene permisos que expiran en los próximos N días
     */
    public List<AuthUsuarioPermiso> findPorExpirar(int diasAnticipacion) {
        LocalDateTime inicio = LocalDateTime.now();
        LocalDateTime fin = inicio.plusDays(diasAnticipacion);
        return list("fechaExpiracion BETWEEN ?1 AND ?2", inicio, fin);
    }

    /**
     * Obtiene todos los permisos individuales de un permiso específico
     */
    public List<AuthUsuarioPermiso> findByPermisoId(Long permisoId) {
        return list("permiso.id", permisoId);
    }

    /**
     * Verifica si existe ya una asignación del permiso al usuario (para evitar duplicados)
     */
    public boolean existeAsignacion(Long authUsuarioId, Long permisoId) {
        long count = count("authUsuario.id = ?1 AND permiso.id = ?2", authUsuarioId, permisoId);
        return count > 0;
    }

    /**
     * Obtiene permisos temporales (con fecha de expiración)
     */
    public List<AuthUsuarioPermiso> findPermisosTemporales(Long authUsuarioId) {
        return list("authUsuario.id = ?1 AND fechaExpiracion IS NOT NULL", authUsuarioId);
    }

    /**
     * Obtiene permisos permanentes (sin fecha de expiración)
     */
    public List<AuthUsuarioPermiso> findPermisosPermanentes(Long authUsuarioId) {
        return list("authUsuario.id = ?1 AND fechaExpiracion IS NULL", authUsuarioId);
    }

    /**
     * Cuenta permisos individuales vigentes por usuario
     */
    public long countPermisosVigentes(Long authUsuarioId) {
        LocalDateTime ahora = LocalDateTime.now();
        return count("authUsuario.id = ?1 AND (fechaExpiracion IS NULL OR fechaExpiracion > ?2)", 
                     authUsuarioId, ahora);
    }

    /**
     * Elimina permisos expirados (limpieza periódica)
     */
    public long eliminarPermisosExpirados() {
        LocalDateTime ahora = LocalDateTime.now();
        return delete("fechaExpiracion IS NOT NULL AND fechaExpiracion <= ?1", ahora);
    }

    /**
     * Revoca todos los permisos individuales de un usuario
     */
    public long revocarTodosLosPermisos(Long authUsuarioId) {
        return delete("authUsuario.id", authUsuarioId);
    }

    /**
     * Revoca un permiso específico de un usuario
     */
    public long revocarPermiso(Long authUsuarioId, Long permisoId) {
        return delete("authUsuario.id = ?1 AND permiso.id = ?2", authUsuarioId, permisoId);
    }
}
