# 🚀 Guía de Migración - Backend Alpha

Esta guía detalla los pasos exactos para migrar cada módulo a la nueva **Clean Architecture Pragmática**.

---

## 📋 Checklist por Entidad

Para cada entidad que migres (ej. `Universidad`, `Estudiante`), sigue estos pasos:

### 1. Preparación del Dominio
- [ ] **Crear Factory Method**: En la entidad, crea un método estático `crear(...)` con validaciones.
- [ ] **Eliminar @PrePersist**: Mueve la lógica de inicialización al constructor privado o factory.
- [ ] **Definir Interfaz Repository**: En `domain/repositories/`, crea una interfaz pura (sin Panache).

### 2. Implementación de Casos de Uso
- [ ] **Crear Use Cases**: En `domain/usecases/`, crea clases para cada acción (Crear, Actualizar, Buscar).
- [ ] **Mover Lógica**: Mueve validaciones de negocio desde el Service antiguo a los Use Cases o Entidad.
- [ ] **Tests Unitarios**: Crea tests para los Use Cases (mockeando el repositorio).

### 3. Infraestructura
- [ ] **Implementar Repository**: En `infrastructure/persistence/`, crea la clase que implementa la interfaz del dominio y extiende `PanacheRepositoryBase`.
- [ ] **Actualizar Inyección**: Asegúrate de usar `@ApplicationScoped`.

### 4. Capa de Aplicación
- [ ] **Crear Application Service**: En `application/services/`, crea el orquestador.
- [ ] **Inyectar Dependencias**: Inyecta Use Cases y Mappers.
- [ ] **Manejar Transacciones**: Usa `@Transactional` aquí.

### 5. Capa Web
- [ ] **Actualizar Controller**: Cambia la inyección del Service antiguo al nuevo Application Service.
- [ ] **Verificar Endpoints**: Prueba que la API siga funcionando igual.

---

## 🗓️ Fases de Ejecución

### Fase 1: Piloto (Universidad) 🔴 PRIORIDAD ALTA
El objetivo es establecer el patrón.
- **Entidad**: `Universidad`
- **Archivos a crear**:
  - `domain/usecases/CrearUniversidadUseCase.java`
  - `domain/repositories/UniversidadRepository.java` (interfaz)
  - `infrastructure/persistence/UniversidadPanacheRepository.java`
  - `application/services/UniversidadApplicationService.java`

### Fase 2: Core Académico 🟡 PRIORIDAD MEDIA
Migrar las entidades centrales del sistema.
- **Entidades**: `Persona`, `Estudiante`
- **Reto**: Manejar las relaciones `@ManyToOne` correctamente en los Use Cases.

### Fase 3: Procesos Académicos 🟢 PRIORIDAD BAJA
- **Entidades**: `Matricula`, `PeriodoAcademico`, `Curso`
- **Reto**: Lógica compleja de validación de matrículas.

### Fase 4: Módulos Satélite
- **Módulos**: `Security`, `Catalog`, `Finance`
- **Estrategia**: Migrar bajo demanda o al final.

---

## 💡 Tips de Migración

1. **No rompas el código**: Crea las nuevas clases en paralelo y cambia el controller al final.
2. **Usa IDE Refactoring**: Mueve métodos con cuidado.
3. **Tests son tus amigos**: Si tienes tests, ejecútalos constantemente.
4. **Commits pequeños**: Un commit por capa o por entidad migrada.

---

## ❓ Preguntas Frecuentes

**¿Qué hago con los DTOs?**
Déjalos en `application/dto`. Los Use Cases NO deben usarlos, pero el Application Service sí.

**¿Puedo usar Panache en el dominio?**
NO. Solo en `infrastructure/persistence`.

**¿Dónde van las validaciones de formato?**
En los Value Objects (si usas) o en el Factory Method de la entidad.
