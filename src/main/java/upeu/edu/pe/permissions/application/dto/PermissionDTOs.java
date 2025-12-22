package upeu.edu.pe.permissions.application.dto;

import java.util.List;
import java.util.Map;

/**
 * DTOs para la administración de roles y permisos. listoooo
 */
public class PermissionDTOs {

        /**
         * Resumen de un rol con conteo de permisos.
         */
        public record RolSummaryDTO(
                        String nombre,
                        int totalPermisos,
                        int modulosActivos) {
        }

        /**
         * Módulo (isla) con sus targets.
         */
        public record ModuloDTO(
                        Long id,
                        String codigo,
                        String nombre,
                        String descripcion,
                        String icono,
                        String color,
                        Integer orden,
                        List<TargetDTO> targets) {
        }

        /**
         * Target (hijo de módulo) con estado y APIs.
         */
        public record TargetDTO(
                        Long id,
                        String codigo,
                        String nombre,
                        String descripcion,
                        boolean activo,
                        List<ApiPermisoDTO> apis) {
        }

        /**
         * Permiso de API.
         */
        public record ApiPermisoDTO(
                        Long id,
                        String apiBase,
                        String descripcion,
                        boolean puedeGet,
                        boolean puedePost,
                        boolean puedePut,
                        boolean puedeDelete) {
        }

        /**
         * Request para actualizar permisos de un rol.
         */
        public record UpdateRolPermisosRequest(
                        List<Long> menuItemIdsActivos) {
        }

        /**
         * Permisos completos de un rol.
         */
        public record RolPermisosDetalleDTO(
                        String rolNombre,
                        int totalPermisos,
                        List<ModuloDTO> modulos) {
        }

        /**
         * Usuario con sus permisos individuales.
         */
        public record UsuarioPermisosDTO(
                        String email,
                        String nombreCompleto,
                        List<String> roles,
                        List<PermisoIndividualDTO> permisosIndividuales) {
        }

        /**
         * Permiso individual asignado a un usuario.
         */
        public record PermisoIndividualDTO(
                        Long id,
                        Long menuItemId,
                        String menuItemCodigo,
                        String menuItemNombre,
                        String moduloPadre,
                        String otorgadoPor,
                        String razon) {
        }

        /**
         * Request para buscar usuarios.
         */
        public record BuscarUsuariosRequest(
                        String query) {
        }

        /**
         * Request para asignar permiso individual.
         */
        public record AsignarPermisoIndividualRequest(
                        String usuarioEmail,
                        Long menuItemId,
                        String razon) {
        }
}
