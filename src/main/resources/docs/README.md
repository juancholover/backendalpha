# 🏗️ Guía de Arquitectura: Patrón Use Cases

## Introducción

Este proyecto implementa **Clean Architecture** con el patrón de **Use Cases** para separar claramente las responsabilidades:

- **Services** → Solo operaciones de **LECTURA** (queries)
- **Use Cases** → Operaciones de **ESCRITURA** con lógica de negocio
- **Commands** → Objetos inmutables que encapsulan datos de entrada
- **Controllers** → Orquestación: delegan a Use Cases (escritura) y Services (lectura)

---

## 📁 Estructura de Carpetas por Módulo

```
src/main/java/upeu/edu/pe/{modulo}/
├── application/
│   ├── dto/           # DTOs de Request y Response
│   └── mapper/        # Mappers Entity ↔ DTO
├── domain/
│   ├── commands/      # 🆕 Comandos (datos de entrada para Use Cases)
│   ├── entities/      # Entidades JPA
│   ├── exceptions/    # Excepciones específicas del módulo
│   ├── repositories/  # Interfaces de repositorios
│   ├── services/      # 🔄 Solo consultas (refactorizado)
│   └── usecases/      # 🆕 Casos de uso (lógica de negocio)
└── infrastructure/
    ├── persistence/   # Implementaciones de repositorios
    └── rest/          # Controllers REST
```

---

## 🎯 Patrón de Implementación

### 1. Command (Datos de Entrada)

```java
package upeu.edu.pe.{modulo}.domain.commands;

/**
 * Comando para [descripción de la operación].
 */
public record CrearEntidadCommand(
    Long campoRequerido,
    String otroCampo,
    // ... más campos
) {
    // Validaciones en el constructor compacto
    public CrearEntidadCommand {
        if (campoRequerido == null) {
            throw new IllegalArgumentException("El campo es obligatorio");
        }
    }
}
```

**Reglas:**
- Usar `record` de Java (inmutable)
- Validar campos obligatorios en el constructor
- Nombrar con verbo en infinitivo: `Crear...`, `Actualizar...`, `Eliminar...`

---

### 2. Use Case (Lógica de Negocio)

```java
package upeu.edu.pe.{modulo}.domain.usecases;

@ApplicationScoped
public class CrearEntidadUseCase {

    @Inject
    EntidadRepository entidadRepository;

    @Inject
    OtraEntidadRepository otraRepository; // Si se necesita

    @Transactional
    public Entidad execute(CrearEntidadCommand command) {
        
        // 1. Validaciones de negocio
        if (entidadRepository.existsByCodigo(command.codigo())) {
            throw new DuplicateResourceException("Entidad", "codigo", command.codigo());
        }

        // 2. Obtener entidades relacionadas
        OtraEntidad otra = otraRepository.findByIdOptional(command.otraId())
            .orElseThrow(() -> new NotFoundException("OtraEntidad no encontrada"));

        // 3. Crear entidad
        Entidad entidad = new Entidad();
        entidad.setCodigo(command.codigo());
        entidad.setOtra(otra);
        // ... más campos

        // 4. Persistir
        entidadRepository.persist(entidad);

        return entidad;
    }
}
```

**Reglas:**
- Un método `execute()` que recibe el Command y retorna la entidad
- Usar `@Transactional` para operaciones de escritura
- Lanzar excepciones específicas del dominio
- No devolver DTOs (el Controller hace la conversión)

---

### 3. Service (Solo Consultas)

```java
package upeu.edu.pe.{modulo}.domain.services;

@ApplicationScoped
public class EntidadService {

    @Inject
    EntidadRepository entidadRepository;

    @Inject
    EntidadMapper entidadMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<EntidadResponseDTO> findAll() {
        return entidadMapper.toResponseDTOList(
            entidadRepository.findAllActive()
        );
    }

    public EntidadResponseDTO findById(Long id) {
        Entidad entidad = entidadRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Entidad no encontrada"));
        return entidadMapper.toResponseDTO(entidad);
    }

    // Método para uso interno (otros services/usecases)
    public Entidad getEntityById(Long id) {
        return entidadRepository.findByIdOptional(id)
            .orElseThrow(() -> new NotFoundException("Entidad no encontrada"));
    }
}
```

**Reglas:**
- **NO** incluir métodos `create()`, `update()`, `delete()`
- Retornar DTOs para endpoints públicos
- Método `getEntityById()` para uso interno

---

### 4. Controller (Orquestación)

```java
package upeu.edu.pe.{modulo}.infrastructure.rest;

@Path("/api/v1/entidades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Entidades", description = "Gestión de entidades")
public class EntidadController {

    // ====== Use Cases para ESCRITURA ======
    @Inject
    CrearEntidadUseCase crearEntidadUseCase;

    @Inject
    ActualizarEntidadUseCase actualizarEntidadUseCase;

    @Inject
    EliminarEntidadUseCase eliminarEntidadUseCase;

    // ====== Service para LECTURA ======
    @Inject
    EntidadService entidadService;

    @Inject
    EntidadMapper entidadMapper;

    // =====================================================
    // OPERACIONES DE LECTURA (Service)
    // =====================================================

    @GET
    public Response findAll() {
        List<EntidadResponseDTO> entidades = entidadService.findAll();
        return Response.ok(ApiResponse.success("Entidades obtenidas", entidades)).build();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        EntidadResponseDTO entidad = entidadService.findById(id);
        return Response.ok(ApiResponse.success("Entidad encontrada", entidad)).build();
    }

    // =====================================================
    // OPERACIONES DE ESCRITURA (Use Cases)
    // =====================================================

    @POST
    public Response create(@Valid EntidadRequestDTO dto) {
        
        // 1. Convertir DTO a Command
        CrearEntidadCommand command = new CrearEntidadCommand(
            dto.getCampo1(),
            dto.getCampo2()
        );

        // 2. Ejecutar Use Case
        Entidad entidad = crearEntidadUseCase.execute(command);

        // 3. Convertir a DTO
        EntidadResponseDTO response = entidadMapper.toResponseDTO(entidad);

        return Response.status(Response.Status.CREATED)
            .entity(ApiResponse.success("Entidad creada", response))
            .build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        eliminarEntidadUseCase.execute(id);
        return Response.ok(ApiResponse.success("Entidad eliminada", null)).build();
    }
}
```

---

## 🚨 Sistema de Excepciones

### Jerarquía

```
RuntimeException
└── shared.exceptions
    ├── NotFoundException           # Recurso no encontrado (404)
    ├── DuplicateResourceException  # Recurso duplicado (409)
    ├── BusinessException           # Regla de negocio violada (400)
    └── BusinessRuleException       # Regla de negocio violada (400)

{modulo}.domain.exceptions
├── EntidadNoEncontradaException  extends NotFoundException
├── EntidadDuplicadaException     extends DuplicateResourceException
└── ReglaEspecificaException      extends BusinessException
```

### Ejemplo de Excepción de Módulo

```java
package upeu.edu.pe.{modulo}.domain.exceptions;

import upeu.edu.pe.shared.exceptions.NotFoundException;

public class EntidadNoEncontradaException extends NotFoundException {
    
    private final Long id;
    
    public EntidadNoEncontradaException(Long id) {
        super(String.format("Entidad con ID %d no encontrada", id));
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
}
```

---

## ✅ Checklist para Nuevas Funcionalidades

Al implementar una nueva operación de escritura:

- [ ] Crear `Command` en `domain/commands/`
- [ ] Crear `UseCase` en `domain/usecases/`
- [ ] Verificar que `Service` solo tiene consultas
- [ ] Actualizar `Controller`:
  - [ ] Inyectar el nuevo UseCase
  - [ ] Convertir DTO → Command
  - [ ] Ejecutar UseCase
  - [ ] Convertir Entidad → DTO
- [ ] Crear excepciones específicas si es necesario en `domain/exceptions/`
- [ ] Compilar: `./gradlew compileJava`

---

## 📋 Módulos Implementados

| Módulo | Commands | Use Cases | Services | Controllers |
|:---|:---:|:---:|:---:|:---:|
| Assessment | 4 | 5 | 3 (solo lectura) | 3 |
| Core | 3 | 4 | 3 (solo lectura) | 3 |
| People | 4 | 5 | 4 (solo lectura) | 4 |
| Enrollment | 4 | 4 | 2 (solo lectura) | 2 |
| Curriculum | 7 | 14 | 2 (solo lectura) | 3 |
| Finance | 4 | 6 | 3 (solo lectura) | 3 |

---

## 🔍 Ejemplos de Referencia

Para ver implementaciones completas, revisar:

- **Command simple**: `enrollment/domain/commands/MatricularEstudianteCommand.java`
- **UseCase con validaciones**: `finance/domain/usecases/AplicarPagoADeudaUseCase.java`
- **Service refactorizado**: `curriculum/domain/services/ProgramaAcademicoService.java`
- **Controller completo**: `finance/infrastructure/web/PagoController.java`

---

## ❓ FAQ

**¿Cuándo crear un UseCase?**
- Operaciones que modifican datos (POST, PUT, DELETE)
- Lógica de negocio compleja con validaciones
- Operaciones que afectan múltiples entidades

**¿Puedo usar el Service en un UseCase?**
- Sí, para obtener entidades via `getEntityById()`
- No para operaciones de escritura

**¿Qué pasa con operaciones simples de CRUD?**
- Aún así crear UseCase para mantener consistencia
- Facilita agregar validaciones en el futuro
