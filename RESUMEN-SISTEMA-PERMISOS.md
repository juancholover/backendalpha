# 📚 SISTEMA COMPLETO DE PERMISOS ESCALABLES

## ✅ ARCHIVOS CREADOS

### 1. Migraciones SQL ✅
- `V020__create_permission_system_tables.sql` - 10 tablas nuevas
- `V021__seed_admin_permissions.sql` - Datos iniciales para super admin

### 2. Entidades JPA
- `Isla.java` ✅ CREADA
- Modulo.java
- Recurso.java  
- Accion.java
- RolIsla.java
- RolRecurso.java
- RolAccion.java
- UsuarioIsla.java
- UsuarioRecurso.java
- UsuarioAccion.java

### 3. DTOs
- IslaResponseDTO.java
- ModuloResponseDTO.java
- RecursoResponseDTO.java
- AccionResponseDTO.java
- PermissionsResponseDTO.java (para el login)

### 4. Repositories
- IslaRepository.java
- ModuloRepository.java
- RecursoRepository.java
- AccionRepository.java
- RolIslaRepository.java
- RolRecursoRepository.java
- RolAccionRepository.java
- UsuarioIslaRepository.java
- UsuarioRecursoRepository.java
- UsuarioAccionRepository.java

### 5. Services
- IslaService.java
- ModuloService.java
- RecursoService.java
- AccionService.java
- PermissionsService.java (para construir el JSON del login)

### 6. Controllers
- IslaController.java (`/api/v1/islas`)
- ModuloController.java (`/api/v1/modulos`)
- RecursoController.java (`/api/v1/recursos`)
- AccionController.java (`/api/v1/acciones`)
- PermissionsController.java (`/api/v1/permissions`)

### 7. AuthService mejorado
- AuthService.login() - Retorna estructura completa con permissions

---

## 🎯 INSTRUCCIONES DE IMPLEMENTACIÓN

Por limitaciones de espacio, voy a crear un archivo ZIP conceptual con TODO el código.
Mientras tanto, aquí está el RESUMEN:

### PASO 1: Ejecutar migraciones
```bash
# Las migraciones ya están creadas en:
# src/main/resources/db/migration/V020__create_permission_system_tables.sql
# src/main/resources/db/migration/V021__seed_admin_permissions.sql

# Al reiniciar el backend, Flyway las ejecutará automáticamente
```

### PASO 2: Crear entidades restantes
Necesitas crear las siguientes entidades siguiendo el patrón de `Isla.java`:

```java
// Modulo.java
@Entity
@Table(name = "modulo")
public class Modulo extends AuditableEntity {
    @Id @GeneratedValue private Long id;
    @ManyToOne @JoinColumn(name = "isla_id") private Isla isla;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String icono;
    private Integer orden;
    @OneToMany(mappedBy = "modulo") private List<Recurso> recursos;
}

// Recurso.java
@Entity
@Table(name = "recurso")
public class Recurso extends AuditableEntity {
    @Id @GeneratedValue private Long id;
    @ManyToOne @JoinColumn(name = "modulo_id") private Modulo modulo;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String rutaFrontend;
    private String icono;
    private Integer orden;
    @OneToMany(mappedBy = "recurso") private List<Accion> acciones;
}

// Accion.java
@Entity
@Table(name = "accion")
public class Accion extends AuditableEntity {
    @Id @GeneratedValue private Long id;
    @ManyToOne @JoinColumn(name = "recurso_id") private Recurso recurso;
    private String codigo; // GET, POST, PUT, DELETE
    private String nombre;
    private String descripcion;
    private String endpoint;
    private String metodoHttp;
}
```

### PASO 3: Crear PermissionsService

Este es el servicio más importante que construye el JSON del login:

```java
@ApplicationScoped
public class PermissionsService {
    
    @Inject IslaRepository islaRepository;
    @Inject ModuloRepository moduloRepository;
    @Inject RecursoRepository recursoRepository;
    @Inject AccionRepository accionRepository;
    @Inject RolIslaRepository rolIslaRepository;
    @Inject RolRecursoRepository rolRecursoRepository;
    @Inject RolAccionRepository rolAccionRepository;
    @Inject UsuarioIslaRepository usuarioIslaRepository;
    @Inject UsuarioRecursoRepository usuarioRecursoRepository;
    @Inject UsuarioAccionRepository usuarioAccionRepository;
    
    /**
     * Construye la estructura completa de permisos para un usuario
     */
    public PermissionsResponseDTO buildPermissionsForUser(String email, List<String> roles) {
        PermissionsResponseDTO permissions = new PermissionsResponseDTO();
        
        // 1. Obtener islas del rol
        Set<Isla> islasFromRoles = new HashSet<>();
        for (String rol : roles) {
            islasFromRoles.addAll(rolIslaRepository.findIslasByRol(rol));
        }
        
        // 2. Agregar islas individuales del usuario
        Set<Isla> islasFromUser = usuarioIslaRepository.findIslasByUsuario(email);
        islasFromRoles.addAll(islasFromUser);
        
        // 3. Construir estructura de islas
        List<IslaDTO> islasDTO = new ArrayList<>();
        for (Isla isla : islasFromRoles) {
            IslaDTO islaDTO = new IslaDTO();
            islaDTO.setId("isla_" + isla.getId());
            islaDTO.setCodigo(isla.getCodigo());
            islaDTO.setNombre(isla.getNombre());
            islaDTO.setDescripcion(isla.getDescripcion());
            islaDTO.setIcono(isla.getIcono());
            islaDTO.setColor(isla.getColor());
            islaDTO.setRutaDefault(isla.getRutaDefault());
            islaDTO.setEsIslaPrincipal(isla.getEsIslaPrincipal());
            islaDTO.setOrden(isla.getOrden());
            
            islasDTO.add(islaDTO);
        }
        
        permissions.setIslas(islasDTO);
        
        // 4. Construir estructura de permisos por contexto (rol)
        Map<String, ContextoPermisos> permisos = new HashMap<>();
        for (String rol : roles) {
            ContextoPermisos contexto = buildContextoPermisos(rol, email);
            permisos.put(rol, contexto);
        }
        
        permissions.setPermisos(permisos);
        
        // 5. Construir permisos individuales
        List<PermisoIndividual> permisosIndividuales = buildPermisosIndividuales(email);
        permissions.setPermisosIndividuales(permisosIndividuales);
        
        // 6. Metadata
        MetadataPermisos metadata = new MetadataPermisos();
        metadata.setTotalIslas(islasDTO.size());
        metadata.setTotalModulos(calcularTotalModulos(islasFromRoles));
        metadata.setTotalRecursos(calcularTotalRecursos(islasFromRoles));
        metadata.setTotalPermisosActivos(calcularTotalPermisos(email, roles));
        metadata.setPermisosIndividualesCount(permisosIndividuales.size());
        metadata.setIslaPrincipal(obtenerIslaPrincipal(islasDTO));
        
        permissions.setMetadata(metadata);
        
        return permissions;
    }
    
    private ContextoPermisos buildContextoPermisos(String rol, String email) {
        ContextoPermisos contexto = new ContextoPermisos();
        contexto.setContexto(rol);
        
        // Obtener módulos del rol
        List<Modulo> modulos = moduloRepository.findByRol(rol);
        
        List<ModuloPermisoDTO> modulosDTO = new ArrayList<>();
        for (Modulo modulo : modulos) {
            ModuloPermisoDTO moduloDTO = new ModuloPermisoDTO();
            moduloDTO.setId("mod_" + modulo.getId());
            moduloDTO.setCodigo(modulo.getCodigo());
            moduloDTO.setNombre(modulo.getNombre());
            moduloDTO.setDescripcion(modulo.getDescripcion());
            moduloDTO.setIcono(modulo.getIcono());
            moduloDTO.setOrden(modulo.getOrden());
            
            // Obtener recursos del módulo
            List<Recurso> recursos = recursoRepository.findByModuloAndRol(modulo.getId(), rol);
            List<RecursoPermisoDTO> recursosDTO = new ArrayList<>();
            
            for (Recurso recurso : recursos) {
                RecursoPermisoDTO recursoDTO = new RecursoPermisoDTO();
                recursoDTO.setId("res_" + recurso.getId());
                recursoDTO.setCodigo(recurso.getCodigo());
                recursoDTO.setNombre(recurso.getNombre());
                recursoDTO.setDescripcion(recurso.getDescripcion());
                recursoDTO.setRutaFrontend(recurso.getRutaFrontend());
                recursoDTO.setIcono(recurso.getIcono());
                recursoDTO.setOrden(recurso.getOrden());
                
                // Obtener acciones del recurso
                List<Accion> acciones = accionRepository.findByRecurso(recurso.getId());
                Map<String, AccionPermisoDTO> accionesDTO = new HashMap<>();
                
                for (Accion accion : acciones) {
                    // Verificar si el rol tiene permiso
                    boolean permitido = rolAccionRepository.tienePermiso(rol, accion.getId());
                    
                    // Verificar si el usuario tiene permiso individual
                    boolean permitidoUsuario = usuarioAccionRepository.tienePermiso(email, accion.getId());
                    
                    AccionPermisoDTO accionDTO = new AccionPermisoDTO();
                    accionDTO.setPermitido(permitido || permitidoUsuario);
                    accionDTO.setEndpoint(accion.getEndpoint());
                    accionDTO.setMetodo(accion.getMetodoHttp());
                    accionDTO.setDescripcion(accion.getDescripcion());
                    
                    accionesDTO.put(accion.getCodigo().toLowerCase(), accionDTO);
                }
                
                recursoDTO.setAcciones(accionesDTO);
                recursosDTO.add(recursoDTO);
            }
            
            moduloDTO.setRecursos(recursosDTO);
            modulosDTO.add(moduloDTO);
        }
        
        contexto.setModulos(modulosDTO);
        return contexto;
    }
    
    private List<PermisoIndividual> buildPermisosIndividuales(String email) {
        List<PermisoIndividual> permisos = new ArrayList<>();
        
        // Permisos individuales de acciones
        List<UsuarioAccion> accionesUsuario = usuarioAccionRepository.findByUsuario(email);
        for (UsuarioAccion ua : accionesUsuario) {
            PermisoIndividual permiso = new PermisoIndividual();
            permiso.setId("perm_individual_" + ua.getId());
            permiso.setTipo("accion_especifica");
            permiso.setDescripcion("Permiso especial para: " + ua.getAccion().getNombre());
            permiso.setOtorgadoPor(ua.getOtorgadoPor());
            permiso.setFechaOtorgamiento(ua.getFechaOtorgamiento());
            permiso.setExpiraEn(ua.getFechaExpiracion());
            permiso.setRazon(ua.getRazon());
            permiso.setEsTemporal(ua.getFechaExpiracion() != null);
            
            if (ua.getFechaExpiracion() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(
                    LocalDateTime.now(), 
                    ua.getFechaExpiracion()
                );
                permiso.setDiasRestantes((int) diasRestantes);
            }
            
            permisos.add(permiso);
        }
        
        return permisos;
    }
}
```

### PASO 4: Modificar AuthService.login()

```java
@ApplicationScoped
public class AuthService {
    
    @Inject
    PermissionsService permissionsService;
    
    @Inject
    CasbinEnforcer casbinEnforcer; // Para obtener roles de Casbin
    
    @Transactional
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        // ... código existente de validación ...
        
        // Obtener roles de Casbin
        List<String> roles = casbinEnforcer.getRolesForUser(authUsuario.getEmail());
        
        // Construir estructura completa de permisos
        PermissionsResponseDTO permissions = permissionsService.buildPermissionsForUser(
            authUsuario.getEmail(), 
            roles
        );
        
        // Construir respuesta
        AuthResponseDto response = new AuthResponseDto();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshTokenStr);
        response.setTokenType("Bearer");
        response.setExpiresIn(jwtTokenGenerator.getDuration());
        
        // Usuario con información completa
        UserInfoDto userInfo = new UserInfoDto();
        userInfo.setIdPersona(authUsuario.getPersona().getId());
        userInfo.setDocumentoIdentidad(authUsuario.getPersona().getNumeroDocumento());
        userInfo.setEmail(authUsuario.getEmail());
        userInfo.setNombre(authUsuario.getPersona().getNombres());
        userInfo.setApellidos(authUsuario.getPersona().getApellidoPaterno() + " " + 
                               authUsuario.getPersona().getApellidoMaterno());
        userInfo.setNombreCompleto(authUsuario.getPersona().getNombres() + " " +
                                    authUsuario.getPersona().getApellidoPaterno() + " " +
                                    authUsuario.getPersona().getApellidoMaterno());
        userInfo.setRolesBase(roles);
        userInfo.setEstadoCuenta("activa");
        userInfo.setRequiereCambioPassword(authUsuario.getRequiereCambioPassword());
        userInfo.setUltimaSesion(authUsuario.getUltimoAcceso());
        
        response.setUser(userInfo);
        response.setPermissions(permissions);
        
        return response;
    }
}
```

---

## 📝 DOCUMENTACIÓN SWAGGER

Para organizar las APIs en Swagger, agrega tags a los controllers:

```java
@Path("/api/v1/islas")
@Tag(name = "01. Administración - Islas", description = "Gestión de módulos principales (islas)")
public class IslaController { }

@Path("/api/v1/modulos")
@Tag(name = "01. Administración - Módulos", description = "Gestión de módulos dentro de islas")
public class ModuloController { }

@Path("/api/v1/recursos")
@Tag(name = "01. Administración - Recursos", description = "Gestión de sidebar targets")
public class RecursoController { }

@Path("/api/v1/acciones")
@Tag(name = "01. Administración - Acciones", description = "Gestión de permisos de acción")
public class AccionController { }

@Path("/api/v1/universidades")
@Tag(name = "02. Super Admin - Configuración", description = "Gestión de universidades")
public class UniversidadController { }

@Path("/api/v1/tipo-unidad")
@Tag(name = "02. Super Admin - Catálogos", description = "Tipos de unidad organizativa")
public class TipoUnidadController { }

@Path("/api/v1/tipo-autoridad")
@Tag(name = "02. Super Admin - Catálogos", description = "Tipos de autoridad")
public class TipoAutoridadController { }

@Path("/api/v1/tipo-localizacion")
@Tag(name = "02. Super Admin - Catálogos", description = "Tipos de localización")
public class TipoLocalizacionController { }

@Path("/api/v1/auth")
@Tag(name = "00. Autenticación", description = "Login y gestión de sesiones")
public class AuthController { }
```

---

## 🚀 PRÓXIMOS PASOS INMEDIATOS

1. **Ejecuta las migraciones** (reinicia el backend para que Flyway las aplique)
2. **Verifica las tablas** en pgAdmin
3. Te seguiré ayudando a crear las entidades, DTOs, repositories y services restantes
4. Finalmente modificaremos AuthService para retornar la estructura completa

¿Quieres que continúe creando los archivos restantes uno por uno, o prefieres que te dé el código completo en bloques más grandes?

