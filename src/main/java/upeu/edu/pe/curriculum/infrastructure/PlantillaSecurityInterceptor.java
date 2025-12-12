package upeu.edu.pe.curriculum.infrastructure;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.ForbiddenException;
import lombok.extern.slf4j.Slf4j;
import upeu.edu.pe.curriculum.domain.services.CasbinAuthorizationService;
import upeu.edu.pe.security.domain.entities.AuthUsuario;
import upeu.edu.pe.shared.context.AuditContext;

/**
 * Interceptor para validar permisos en operaciones de plantillas multi-campus.
 * 
 * Este interceptor se ejecuta antes de los métodos anotados con @RequirePlantillaPermission
 * y valida que el usuario autenticado tenga los permisos necesarios.
 * 
 * @author Sistema de Currículum UPEU
 */
@Interceptor
@RequirePlantillaPermission("")
@Priority(Interceptor.Priority.APPLICATION)
@Slf4j
public class PlantillaSecurityInterceptor {

    @Inject
    CasbinAuthorizationService casbinService;

    @Inject
    AuditContext auditContext;

    @AroundInvoke
    public Object validatePermission(InvocationContext context) throws Exception {
        // Obtener la anotación del método
        RequirePlantillaPermission annotation = context.getMethod()
            .getAnnotation(RequirePlantillaPermission.class);

        if (annotation == null) {
            // No hay anotación, continuar sin validación
            return context.proceed();
        }

        String permission = annotation.value();
        log.debug("🔒 Validando permiso: {}", permission);

        // Obtener usuario autenticado del contexto de auditoría
        AuthUsuario usuario = obtenerUsuarioAutenticado();

        if (usuario == null) {
            log.warn("⚠️ No hay usuario autenticado en el contexto");
            throw new ForbiddenException("Usuario no autenticado");
        }

        // Validar permiso según el tipo
        boolean hasPermission = validarPermiso(usuario, permission);

        if (!hasPermission) {
            log.warn("🚫 Usuario {} NO tiene permiso: {}", usuario.getUsername(), permission);
            throw new ForbiddenException(
                String.format("No tiene permisos para realizar esta operación: %s", permission)
            );
        }

        log.info("✅ Usuario {} tiene permiso: {}", usuario.getUsername(), permission);
        return context.proceed();
    }

    private AuthUsuario obtenerUsuarioAutenticado() {
        // Obtener usuario del contexto de auditoría
        String username = auditContext.getCurrentUser();
        if (username == null || "system".equals(username)) {
            return null;
        }

        // TODO: En producción, cargar el usuario completo desde la base de datos
        // Por ahora retornamos un mock para desarrollo
        AuthUsuario usuario = new AuthUsuario();
        upeu.edu.pe.core.domain.entities.Persona persona = new upeu.edu.pe.core.domain.entities.Persona();
        persona.setEmail(username);
        usuario.setPersona(persona);
        return usuario;
    }

    private boolean validarPermiso(AuthUsuario usuario, String permission) {
        return switch (permission) {
            // Permisos de plantillas
            case "plantilla:create" -> casbinService.canCrearPlantilla(usuario);
            case "plantilla:read" -> casbinService.canReadPlantilla(usuario);
            case "plantilla:update" -> casbinService.canUpdatePlantilla(usuario);
            case "plantilla:delete" -> casbinService.canDeletePlantilla(usuario);
            case "plantilla:publicar_campus" -> casbinService.canPublicarPlantillaEnCampus(usuario);
            
            // Permisos de publicaciones
            case "publicacion:read" -> casbinService.canReadPublicacion(usuario);
            case "publicacion:create" -> casbinService.canCreatePublicacion(usuario);
            case "publicacion:update" -> casbinService.canUpdatePublicacion(usuario);
            case "publicacion:delete" -> casbinService.canDeletePublicacion(usuario);
            case "publicacion:adaptar" -> casbinService.canAdaptarPublicacion(usuario);
            
            default -> {
                log.warn("⚠️ Permiso desconocido: {}", permission);
                yield false;
            }
        };
    }
}
