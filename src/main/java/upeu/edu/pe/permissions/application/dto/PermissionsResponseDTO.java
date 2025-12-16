package upeu.edu.pe.permissions.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO completo de permisos que se retorna en el login.
 * Contiene islas, módulos, recursos, acciones y permisos individuales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionsResponseDTO {

    private List<IslaDTO> islas = new ArrayList<>();
    private Map<String, ContextoPermisos> permisos = new HashMap<>();
    private List<PermisoIndividualDTO> permisosIndividuales = new ArrayList<>();
    private MetadataPermisosDTO metadata;

    // ================================================
    // CLASES INTERNAS
    // ================================================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IslaDTO {
        private String id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String icono;
        private String color;
        private String rutaDefault;
        private Boolean esIslaPrincipal;
        private Integer orden;
        private List<SidebarTargetDTO> sidebarTargets = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SidebarTargetDTO {
        private String id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String rutaFrontend;
        private String icono;
        private Integer orden;
        private List<ApiPermisoDTO> apis = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiPermisoDTO {
        private String apiBase;
        private String descripcion;
        private Boolean get;
        private Boolean post;
        private Boolean put;
        private Boolean delete;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContextoPermisos {
        private String contexto;
        private List<ModuloPermisoDTO> modulos = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuloPermisoDTO {
        private String id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String icono;
        private Integer orden;
        private List<RecursoPermisoDTO> recursos = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecursoPermisoDTO {
        private String id;
        private String codigo;
        private String nombre;
        private String descripcion;
        private String rutaFrontend;
        private String icono;
        private Integer orden;
        private Map<String, AccionPermisoDTO> acciones = new HashMap<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccionPermisoDTO {
        private Boolean permitido;
        private String endpoint;
        private String metodo;
        private String descripcion;
        private String razonDenegado;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisoIndividualDTO {
        private String id;
        private String tipo; // "accion_especifica", "modulo_completo", "isla_completa"
        private String isla;
        private String modulo;
        private String recurso;
        private String accion;
        private String descripcion;
        private OtorgadoPorDTO otorgadoPor;
        private LocalDateTime fechaOtorgamiento;
        private LocalDateTime expiraEn;
        private String razon;
        private Boolean esTemporal;
        private Integer diasRestantes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtorgadoPorDTO {
        private Long idPersona;
        private String nombre;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetadataPermisosDTO {
        private Integer totalIslas;
        private Integer totalModulos;
        private Integer totalRecursos;
        private Integer totalPermisosActivos;
        private Integer permisosIndividualesCount;
        private String islaPrincipal;
    }
}
