# 🏗️ Migración Clean Architecture Pragmática

## 📋 Índice
1. [Por Qué](#-por-qué)
2. [Solución](#-solución-pragmática)
3. [Arquitectura Explicada](#-arquitectura-explicada)
4. [Estructura Objetivo](#-estructura-objetivo)
5. [Plan de Implementación](#-plan-5-sprints)
6. [Ejemplos](#-ejemplo)

---

## 🎯 Por Qué

### Problemas Actuales
1. **Domain → Application** (dependencia invertida)
2. **@PrePersist** en entidades (infraestructura en dominio)
3. **Repositorios concretos** en domain

---

## ✅ Solución Pragmática

**NO duplicamos entidades** (mantener JPA está OK para SaaS)

**SÍ separamos**:
- Use Cases (lógica sin DTOs)
- Repositorios como interfaces
- Application Services (orquestación)

---

## 🏛️ Arquitectura Explicada

### Principios Fundamentales

#### 1. **Regla de Dependencias** (The Dependency Rule)
```
┌─────────────────────────────────────────┐
│          Infrastructure                  │  ← Capa externa
│  ┌─────────────────────────────────┐   │
│  │      Application                │   │  ← Capa media
│  │  ┌─────────────────────────┐   │   │
│  │  │       Domain            │   │   │  ← Capa interna (núcleo)
│  │  │  • Entities             │   │   │
│  │  │  • Use Cases            │   │   │
│  │  │  • Repositories         │   │   │
│  │  │    (interfaces)         │   │   │
│  │  └─────────────────────────┘   │   │
│  │  • DTOs                         │   │
│  │  • Mappers                      │   │
│  │  • Application Services         │   │
│  └─────────────────────────────────┘   │
│  • Controllers (REST)                   │
│  • Repositories (Impl Panache)          │
│  • Database                             │
└─────────────────────────────────────────┘

Las flechas de dependencia solo apuntan hacia adentro →
```

**Regla de Oro**: Las capas internas **NO** conocen las capas externas.
- ✅ Domain define interfaces, Infrastructure las implementa
- ❌ Domain NO puede importar nada de Application o Infrastructure

---

### 2. **Capas y Responsabilidades**

#### 🔵 **Domain Layer** (Lógica de Negocio Pura)

**Ubicación**: `domain/`

**Responsabilidades**:
- Contiene las **reglas de negocio** del SaaS
- Define **QUÉ** hace el sistema (no cómo)
- Independiente de frameworks, DB, UI

**Componentes**:

##### `domain/entities/`
Entidades con **lógica de negocio**, no solo contenedores de datos.

```java
@Entity  // ✅ Mantenemos JPA (pragmático)
public class Universidad extends AuditableEntity {
    
    // 1. Constructor privado (no se puede crear directamente)
    private Universidad() {
        this.estado = "ACTIVA";
        this.totalEstudiantes = 0;
    }
    
    // 2. Factory method (única forma de crear)
    public static Universidad crear(String codigo, String nombre, String ruc) {
        validarCodigo(codigo);
        Universidad uni = new Universidad();
        uni.setCodigo(codigo.toUpperCase());
        uni.setNombre(nombre);
        uni.setRuc(ruc);
        return uni;
    }
    
    // 3. Comportamiento de negocio
    public void activar() {
        if (fechaVencida()) {
            throw new SubscripcionVencidaException();
        }
        this.estado = "ACTIVA";
    }
    
    public boolean puedeAgregarEstudiante() {
        return estaActiva() && !haExcedidoLimiteEstudiantes();
    }
    
    // 4. Validaciones internas
    private static void validarCodigo(String codigo) {
        if (codigo == null || codigo.length() > 20) {
            throw new CodigoInvalidoException();
        }
    }
}
```

**Características**:
- ✅ Lógica de negocio **dentro** de la entidad
- ✅ Factory methods para creación controlada
- ✅ Validaciones propias del dominio
- ✅ Métodos con nombres del lenguaje de negocio

##### `domain/repositories/` (INTERFACES)
Contratos que define el dominio, implementados por infraestructura.

```java
// ✅ Solo interfaz (puerto)
public interface UniversidadRepository {
    void persist(Universidad universidad);
    Optional<Universidad> findById(Long id);
    Optional<Universidad> findByCodigo(String codigo);
    List<Universidad> findAllActive();
    boolean existsByCodigo(String codigo);
}
```

**Por qué interfaces**:
- Inversión de Dependencias: Domain define el contrato
- Testeable: se puede mockear fácilmente
- Flexible: cambiar implementación sin tocar domain

##### `domain/usecases/`
Casos de uso = **acciones que un usuario puede realizar**.

```java
public class CrearUniversidadUseCase {
    
    private final UniversidadRepository repository;
    
    @Inject
    public CrearUniversidadUseCase(UniversidadRepository repository) {
        this.repository = repository;  // ✅ Interfaz del domain
    }
    
    // ✅ SIN DTOs, solo tipos primitivos o del dominio
    public Universidad execute(String codigo, String nombre, String ruc) {
        // 1. Validaciones de negocio
        validarCodigoUnico(codigo);
        validarRucUnico(ruc);
        
        // 2. Crear entidad usando factory
        Universidad universidad = Universidad.crear(codigo, nombre, ruc);
        
        // 3. Persistir
        repository.persist(universidad);
        
        // 4. Retornar entidad de dominio
        return universidad;
    }
    
    private void validarCodigoUnico(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new CodigoDuplicadoException(codigo);
        }
    }
}
```

**Características**:
- ✅ Un caso de uso = una acción de usuario
- ✅ Lógica de orquestación de dominio
- ✅ **Sin DTOs** (solo entidades/primitivos)
- ✅ Testeable sin base de datos

##### `domain/valueobjects/` (opcional)
Conceptos inmutables del dominio.

```java
public record CodigoUniversidad(String valor) {
    public CodigoUniversidad {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Código vacío");
        }
        if (valor.length() > 20) {
            throw new IllegalArgumentException("Código muy largo");
        }
        valor = valor.toUpperCase().trim();
    }
}
```

---

#### 🟢 **Application Layer** (Orquestación)

**Ubicación**: `application/`

**Responsabilidades**:
- Orquestar **casos de uso**
- Transformar **DTOs ↔ Entidades**
- Manejo de **transacciones**

**Componentes**:

##### `application/dto/`
Objetos de transferencia (entrada/salida de la API).

```java
public class UniversidadRequestDTO {
    private String codigo;
    private String nombre;
    private String ruc;
    // getters/setters
}

public class UniversidadResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String estado;
    // getters/setters
}
```

##### `application/mapper/`
Transformadores DTO ↔ Entity.

```java
@Mapper(componentModel = "cdi")
public interface UniversidadMapper {
    UniversidadResponseDTO toResponseDTO(Universidad entity);
    // No toEntity - usamos factory del domain
}
```

##### `application/services/`
**Orquestadores** entre controllers y use cases.

```java
@ApplicationScoped
public class UniversidadApplicationService {
    
    @Inject CrearUniversidadUseCase crearUseCase;
    @Inject BuscarUniversidadUseCase buscarUseCase;
    @Inject UniversidadMapper mapper;
    
    @Transactional
    public UniversidadResponseDTO crear(UniversidadRequestDTO dto) {
        // 1. Extraer datos del DTO
        String codigo = dto.getCodigo();
        String nombre = dto.getNombre();
        String ruc = dto.getRuc();
        
        // 2. Ejecutar caso de uso (lógica de negocio)
        Universidad universidad = crearUseCase.execute(codigo, nombre, ruc);
        
        // 3. Transformar a DTO de respuesta
        return mapper.toResponseDTO(universidad);
    }
    
    public UniversidadResponseDTO buscarPorId(Long id) {
        Universidad universidad = buscarUseCase.execute(id);
        return mapper.toResponseDTO(universidad);
    }
}
```

**Flujo**:
```
DTO → Application Service → Use Case → Entity
Entity → Application Service → DTO
```

---

#### 🟣 **Infrastructure Layer** (Detalles Técnicos)

**Ubicación**: `infrastructure/`

**Responsabilidades**:
- Implementaciones **concretas** de interfaces
- Conexión con **frameworks** (Quarkus, JPA, REST)
- Detalles de **persistencia**

**Componentes**:

##### `infrastructure/persistence/`
Implementaciones de repositorios usando Panache.

```java
@ApplicationScoped
public class UniversidadPanacheRepository 
    implements PanacheRepositoryBase<Universidad, Long>, 
               UniversidadRepository {  // ✅ Implementa interfaz del domain
    
    @Override
    public Optional<Universidad> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo)
                .firstResultOptional();
    }
    
    @Override
    public List<Universidad> findAllActive() {
        return find("active = true").list();
    }
    
    @Override
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", codigo) > 0;
    }
}
```

##### `infrastructure/web/`
Controllers REST.

```java
@Path("/api/v1/universidades")
public class UniversidadController {
    
    @Inject
    UniversidadApplicationService service;  // ✅ Usa Application Service
    
    @POST
    public Response create(@Valid UniversidadRequestDTO dto) {
        UniversidadResponseDTO result = service.crear(dto);
        return Response.status(201).entity(result).build();
    }
    
    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        UniversidadResponseDTO result = service.buscarPorId(id);
        return Response.ok(result).build();
    }
}
```

---

### 3. **Flujo Completo de Datos**

#### Crear Universidad (POST /api/v1/universidades)

```
1. REQUEST (JSON)
   ↓
2. Controller recibe UniversidadRequestDTO
   │
   ↓
3. Controller → UniversidadApplicationService.crear(dto)
   │
   ├─→ Extrae datos del DTO
   │
   ├─→ Llama CrearUniversidadUseCase.execute(codigo, nombre, ruc)
   │    │
   │    ├─→ Valida código único (repository.existsByCodigo)
   │    ├─→ Valida RUC único
   │    ├─→ Universidad.crear(codigo, nombre, ruc)  ← Factory method
   │    │    └─→ Validaciones internas de la entidad
   │    ├─→ repository.persist(universidad)
   │    │    └─→ UniversidadPanacheRepository guarda en DB
   │    └─→ Retorna Universidad (entidad)
   │
   ├─→ Mapper.toResponseDTO(universidad)
   │
   └─→ Retorna UniversidadResponseDTO
   │
   ↓
4. Controller → Response 201 Created
   ↓
5. RESPONSE (JSON)
```

#### Diagrama de Capas

```
┌─────────────────────────────────────────────────────┐
│  INFRASTRUCTURE LAYER (Implementaciones)             │
│                                                      │
│  UniversidadController                              │
│    ↓ usa                                            │
│  UniversidadApplicationService                      │
│    ↓ usa                                            │
│  ┌────────────────────────────────────────────┐    │
│  │  APPLICATION LAYER (Orquestación)          │    │
│  │                                             │    │
│  │  • Recibe DTOs                             │    │
│  │  • Llama Use Cases                         │    │
│  │  • Convierte Entity → DTO                  │    │
│  │  • Maneja @Transactional                   │    │
│  └────────────────────────────────────────────┘    │
│    ↓ usa                                            │
│  ┌────────────────────────────────────────────┐    │
│  │  DOMAIN LAYER (Lógica de Negocio)         │    │
│  │                                             │    │
│  │  CrearUniversidadUseCase                   │    │
│  │    ↓ usa                                   │    │
│  │  Universidad.crear() (entidad)             │    │
│  │    ↓ usa                                   │    │
│  │  UniversidadRepository (interfaz)          │    │
│  │    ↑ implementada por                      │    │
│  └────────────────────────────────────────────┘    │
│    ↓                                                │
│  UniversidadPanacheRepository                       │
│    ↓                                                │
│  Database (PostgreSQL)                              │
└─────────────────────────────────────────────────────┘
```

---

## 📁 Estructura Objetivo

```
academic/
├── application/
│   ├── dto/
│   │   ├── UniversidadRequestDTO.java
│   │   └── UniversidadResponseDTO.java
│   ├── mapper/
│   │   └── UniversidadMapper.java
│   └── services/                          # ✅ NUEVO
│       └── UniversidadApplicationService.java
│
├── domain/
│   ├── entities/
│   │   └── Universidad.java               # Con lógica de negocio
│   ├── repositories/                      # ✅ SOLO INTERFACES
│   │   └── UniversidadRepository.java
│   ├── usecases/                          # ✅ NUEVO
│   │   ├── CrearUniversidadUseCase.java
│   │   ├── ActualizarUniversidadUseCase.java
│   │   └── BuscarUniversidadUseCase.java
│   └── valueobjects/                      # ✅ NUEVO (opcional)
│       ├── CodigoUniversidad.java
│       └── RUC.java
│
└── infrastructure/
    ├── persistence/                       # ✅ NUEVO
    │   └── UniversidadPanacheRepository.java
    └── web/
        └── UniversidadController.java
```

---

## 📅 Plan (5 Sprints)

**Sprint 1**: Universidad (piloto)  
**Sprint 2-3**: Estudiante, Persona, Matricula  
**Sprint 4**: Resto de módulos  
**Sprint 5**: Value Objects + limpieza  

---

## 🔄 Ejemplo Completo

### Antes ❌

```java
// domain/services/UniversidadService.java
@ApplicationScoped
public class UniversidadService {
    @Inject UniversidadRepository repository;
    @Inject UniversidadMapper mapper;  // ❌ Domain → Application
    
    @Transactional
    public UniversidadResponseDTO create(UniversidadRequestDTO dto) {
        // ❌ Validación en service
        if (repository.existsByCodigo(dto.getCodigo())) {
            throw new DuplicateResourceException(...);
        }
        Universidad uni = mapper.toEntity(dto);
        repository.persist(uni);
        return mapper.toResponseDTO(uni);
    }
}
```

### Después ✅

```java
// domain/usecases/CrearUniversidadUseCase.java
public class CrearUniversidadUseCase {
    private final UniversidadRepository repository;
    
    public Universidad execute(String codigo, String nombre, String ruc) {
        validarCodigoUnico(codigo);
        Universidad uni = Universidad.crear(codigo, nombre, ruc);
        repository.persist(uni);
        return uni;
    }
}

// application/services/UniversidadApplicationService.java
@ApplicationScoped
public class UniversidadApplicationService {
    @Inject CrearUniversidadUseCase crearUseCase;
    @Inject UniversidadMapper mapper;
    
    @Transactional
    public UniversidadResponseDTO crear(UniversidadRequestDTO dto) {
        Universidad uni = crearUseCase.execute(
            dto.getCodigo(), dto.getNombre(), dto.getRuc()
        );
        return mapper.toResponseDTO(uni);
    }
}
```

---

## 📚 Contribuir

### Crear nueva entidad:
1. Entidad con factory en `domain/entities/`
2. Interfaz repo en `domain/repositories/`
3. Impl Panache en `infrastructure/persistence/`
4. Use Cases en `domain/usecases/`
5. App Service en `application/services/`

---

Ver análisis completo en artifact `implementation_plan.md`
