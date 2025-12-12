package upeu.edu.pe.curriculum.infrastructure;

import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar métodos que requieren validación de permisos
 * en operaciones de plantillas multi-campus.
 * 
 * Valores permitidos:
 * - "plantilla:create" - Crear plantillas
 * - "plantilla:read" - Leer plantillas
 * - "plantilla:update" - Actualizar plantillas
 * - "plantilla:delete" - Eliminar plantillas
 * - "plantilla:publicar_campus" - Publicar plantillas en campus
 * - "publicacion:read" - Leer publicaciones
 * - "publicacion:create" - Crear publicaciones
 * - "publicacion:update" - Actualizar publicaciones
 * - "publicacion:delete" - Eliminar publicaciones
 * - "publicacion:adaptar" - Adaptar publicaciones (campus)
 * 
 * Ejemplo:
 * <pre>
 * {@code
 * @RequirePlantillaPermission("plantilla:create")
 * public SilaboPlantilla crearPlantilla(...) {
 *     // ...
 * }
 * }
 * </pre>
 * 
 * @author Sistema de Currículum UPEU
 */
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequirePlantillaPermission {
    /**
     * Permiso requerido para ejecutar el método
     */
    String value();
}
