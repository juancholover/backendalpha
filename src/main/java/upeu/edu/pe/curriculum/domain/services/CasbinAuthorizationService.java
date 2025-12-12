package upeu.edu.pe.curriculum.domain.services;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.file_adapter.FileAdapter;
import upeu.edu.pe.security.domain.entities.AuthUsuario;

/**
 * Servicio de autorización basado en Casbin para control de permisos de Sílabos.
 * 
 * Roles soportados:
 * - sede_central: Control total sobre sílabos y plantillas
 * - campus_admin: Solo puede editar evaluaciones y fechas, adaptar publicaciones
 * - docente: Solo lectura y creación de propuestas
 * - coordinador: Revisión y aprobación, adaptar publicaciones
 * - director: Aprobación final
 */
@ApplicationScoped
@Slf4j
public class CasbinAuthorizationService {

    private Enforcer enforcer;

    @PostConstruct
    public void init() {
        try {
            // Cargar modelo y políticas desde resources
            String modelPath = "src/main/resources/casbin/model.conf";
            String policyPath = "src/main/resources/casbin/policy.csv";
            
            Model model = new Model();
            model.loadModel(modelPath);
            
            FileAdapter adapter = new FileAdapter(policyPath);
            
            enforcer = new Enforcer(model, adapter);
            
            log.info("✅ Casbin Enforcer inicializado correctamente");
            log.info("📋 Modelo cargado desde: {}", modelPath);
            log.info("📋 Políticas cargadas desde: {}", policyPath);
            
        } catch (Exception e) {
            log.error("❌ Error inicializando Casbin Enforcer", e);
            throw new RuntimeException("No se pudo inicializar el servicio de autorización", e);
        }
    }

    /**
     * Verifica si un usuario tiene permiso para realizar una acción sobre un objeto
     */
    public boolean enforce(String usuario, String objeto, String accion) {
        try {
            boolean permitido = enforcer.enforce(usuario, objeto, accion);
            log.debug("🔍 Enforce: usuario={}, objeto={}, accion={} → {}", 
                usuario, objeto, accion, permitido ? "✅ PERMITIDO" : "❌ DENEGADO");
            return permitido;
        } catch (Exception e) {
            log.error("❌ Error en validación de permiso", e);
            return false;
        }
    }

    /**
     * Asigna un rol a un usuario
     */
    public boolean assignRole(String usuario, String rol) {
        try {
            boolean added = enforcer.addGroupingPolicy(usuario, rol);
            if (added) {
                log.info("👤 Rol asignado: usuario={}, rol={}", usuario, rol);
            }
            return added;
        } catch (Exception e) {
            log.error("❌ Error asignando rol", e);
            return false;
        }
    }

    /**
     * Remueve un rol de un usuario
     */
    public boolean removeRole(String usuario, String rol) {
        try {
            boolean removed = enforcer.removeGroupingPolicy(usuario, rol);
            if (removed) {
                log.info("🗑️ Rol removido: usuario={}, rol={}", usuario, rol);
            }
            return removed;
        } catch (Exception e) {
            log.error("❌ Error removiendo rol", e);
            return false;
        }
    }

    // ==================== MÉTODOS DE VALIDACIÓN DE SÍLABOS ====================

    /**
     * Puede crear sílabos (Solo Sede Central)
     */
    public boolean canCreateSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "create");
    }

    /**
     * Puede leer sílabos (Todos los roles)
     */
    public boolean canReadSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "read");
    }

    /**
     * Puede actualizar contenido del sílabo (Solo Sede Central)
     */
    public boolean canUpdateContenido(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "update_contenido");
    }

    /**
     * Puede actualizar evaluaciones (Sede Central, Campus Admin, Coordinador)
     */
    public boolean canUpdateEvaluaciones(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "update_evaluaciones");
    }

    /**
     * Puede actualizar fechas (Sede Central, Campus Admin)
     */
    public boolean canUpdateFechas(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "update_fechas");
    }

    /**
     * Puede congelar sílabos (Solo Sede Central)
     */
    public boolean canCongelarSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "congelar");
    }

    /**
     * Puede aprobar sílabos (Sede Central, Director)
     */
    public boolean canAprobarSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "aprobar");
    }

    /**
     * Puede eliminar sílabos (Solo Sede Central)
     */
    public boolean canDeleteSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "delete");
    }

    /**
     * Puede publicar sílabos (Solo Sede Central)
     */
    public boolean canPublicarSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "publicar");
    }

    /**
     * Puede crear propuestas (Docente, Coordinador, Director, Sede Central)
     */
    public boolean canCreatePropuesta(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "create_propuesta");
    }

    /**
     * Puede revisar sílabos (Coordinador, Director, Sede Central)
     */
    public boolean canRevisarSilabo(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "silabo", "revisar");
    }

    // ==================== MÉTODOS DE PLANTILLAS (Nuevos) ====================

    /**
     * Puede crear plantillas (Solo Sede Central)
     */
    public boolean canCrearPlantilla(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "plantilla", "create");
    }

    /**
     * Puede leer plantillas (Todos los roles)
     */
    public boolean canReadPlantilla(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "plantilla", "read");
    }

    /**
     * Puede actualizar plantillas (Solo Sede Central)
     */
    public boolean canUpdatePlantilla(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "plantilla", "update");
    }

    /**
     * Puede eliminar plantillas (Solo Sede Central)
     */
    public boolean canDeletePlantilla(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "plantilla", "delete");
    }

    /**
     * Puede publicar plantillas en campus (Solo Sede Central)
     */
    public boolean canPublicarPlantillaEnCampus(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "plantilla", "publicar_campus");
    }

    // ==================== MÉTODOS DE PUBLICACIONES (Nuevos) ====================

    /**
     * Puede leer publicaciones (Todos los roles)
     */
    public boolean canReadPublicacion(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "publicacion", "read");
    }

    /**
     * Puede crear publicaciones (Sede Central al publicar)
     */
    public boolean canCreatePublicacion(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "publicacion", "create");
    }

    /**
     * Puede actualizar publicaciones (Sede Central)
     */
    public boolean canUpdatePublicacion(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "publicacion", "update");
    }

    /**
     * Puede eliminar publicaciones (Sede Central)
     */
    public boolean canDeletePublicacion(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "publicacion", "delete");
    }

    /**
     * Puede adaptar publicaciones (Campus Admin, Coordinador)
     */
    public boolean canAdaptarPublicacion(AuthUsuario usuario) {
        return enforce(usuario.getUsername(), "publicacion", "adaptar");
    }

    // ==================== MÉTODOS DE UTILIDAD ====================

    /**
     * Obtiene todos los roles de un usuario
     */
    public java.util.List<String> getUserRoles(String usuario) {
        return enforcer.getRolesForUser(usuario);
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean hasRole(String usuario, String rol) {
        return enforcer.hasRoleForUser(usuario, rol);
    }

    /**
     * Recarga las políticas desde el archivo
     */
    public void reloadPolicies() {
        try {
            enforcer.loadPolicy();
            log.info("🔄 Políticas de Casbin recargadas");
        } catch (Exception e) {
            log.error("❌ Error recargando políticas", e);
        }
    }

    /**
     * Obtiene el enforcer de Casbin para verificaciones avanzadas
     */
    public Enforcer getEnforcer() {
        return enforcer;
    }
}
