# 📘 Diccionario de Datos - Sistema Académico

> **Nota de Arquitectura:** Este sistema utiliza una arquitectura **Single-Tenant** (Instancia Única). La tabla `UNIVERSIDAD` sirve exclusivamente como configuración global de la institución y no como llave foránea para separación de datos.

---

## 📋 DOCUMENTACIÓN DE ENTIDADES - DEFINICIÓN Y REGLAS DE NEGOCIO

### 🏢 MÓDULO: CORE

#### UNIVERSIDAD
**DEFINICIÓN:** Representa la institución educativa propietaria de la instalación del sistema. En arquitectura Single-Tenant, siempre existe un solo registro.

**PROPÓSITO:** Almacenar la configuración global, identidad institucional (nombre, RUC, logo) y parámetros operativos del sistema para ser utilizados en reportes, certificados y documentos oficiales.

**REGLAS DE NEGOCIO:**
- Solo puede existir un registro activo en la base de datos.
- No puede ser eliminada mientras exista información académica en el sistema.
- Los campos `codigo`, `ruc` y `dominio` deben ser únicos.

---

#### PERSONA
**DEFINICIÓN:** Representa a cualquier individuo físico que interactúa con la institución, sin importar su rol específico (estudiante, empleado, docente).

**PROPÓSITO:** Centralizar información demográfica y de contacto para evitar duplicidad. Permite que una misma persona tenga múltiples roles (ej: estudiante que luego es empleado).

**REGLAS DE NEGOCIO:**
- El `numero_documento` debe ser único por `tipo_documento`.
- No puede ser eliminada si tiene relaciones activas (Estudiante, Empleado, Profesor, Autoridad).
- El `email` debe ser único en todo el sistema.
- La `fecha_nacimiento` debe ser anterior a la fecha actual.

---

#### TIPO_UNIDAD
**DEFINICIÓN:** Representa la clasificación jerárquica de las unidades organizativas de la institución.

**PROPÓSITO:** Establecer una taxonomía institucional para organizar facultades, escuelas, departamentos y oficinas administrativas.

**REGLAS DE NEGOCIO:**
- El `nombre` debe ser único.
- No puede ser eliminado si tiene unidades organizativas asociadas.

---

#### UNIDAD_ORGANIZATIVA
**DEFINICIÓN:** Representa una unidad funcional real dentro del organigrama institucional (facultad, escuela, departamento, oficina).

**PROPÓSITO:** Estructurar el organigrama académico y administrativo de la institución para asignación de programas, empleados y autoridades.

**REGLAS DE NEGOCIO:**
- El `codigo` debe ser único.
- No puede tener como `unidad_padre_id` a sí misma (evitar ciclos).
- No puede ser eliminada si tiene programas académicos, empleados o profesores asignados.
- La jerarquía debe ser consistente con el `tipo_unidad`.

---

#### TIPO_LOCALIZACION
**DEFINICIÓN:** Representa la clasificación de espacios físicos dentro de la institución.

**PROPÓSITO:** Categorizar los espacios según su uso (campus, edificio, aula, laboratorio, auditorio) para gestión de recursos físicos.

**REGLAS DE NEGOCIO:**
- El `nombre` debe ser único.
- No puede ser eliminado si tiene localizaciones asociadas.

---

#### LOCALIZACION
**DEFINICIÓN:** Representa un espacio físico específico dentro de la infraestructura institucional.

**PROPÓSITO:** Identificar y gestionar aulas, laboratorios, oficinas y espacios físicos para asignación de horarios y actividades académicas.

**REGLAS DE NEGOCIO:**
- El `codigo` debe ser único.
- La `capacidad` debe ser mayor a cero si es un espacio destinado a clases.
- No puede ser eliminada si tiene horarios o cursos ofertados asignados.
- La jerarquía (`localizacion_padre_id`) debe ser consistente (un aula no puede contener un edificio).

---

#### TIPO_AUTORIDAD
**DEFINICIÓN:** Representa un cargo directivo o de autoridad dentro de la institución.

**PROPÓSITO:** Definir los tipos de cargos directivos disponibles (Rector, Vicerrector, Decano, Director) con su nivel jerárquico correspondiente.

**REGLAS DE NEGOCIO:**
- El `nombre` debe ser único.
- El `nivel_jerarquia` debe ser único (no puede haber dos tipos con el mismo nivel).
- No puede ser eliminado si tiene autoridades asignadas.

---

### 👥 MÓDULO: PEOPLE

#### EMPLEADO
**DEFINICIÓN:** Representa a una persona que mantiene una relación laboral con la institución.

**PROPÓSITO:** Almacenar información laboral (cargo, salario, tipo de contrato, fechas) de todo el personal que trabaja en la institución.

**REGLAS DE NEGOCIO:**
- El `codigo_empleado` debe ser único.
- Una `persona_id` solo puede tener un registro de empleado activo.
- La `fecha_ingreso` no puede ser posterior a la fecha actual.
- Si tiene `fecha_cese`, el `estado_laboral` debe ser "CESADO".
- No puede ser eliminado si tiene profesores o pagos asociados.

---

#### ESTUDIANTE
**DEFINICIÓN:** Representa a una persona inscrita en un programa académico de la institución.

**PROPÓSITO:** Almacenar la información académica del alumno (código estudiantil, ciclo actual, créditos) y su vinculación con el programa en el que está matriculado.

**REGLAS DE NEGOCIO:**
- El `codigo_estudiante` debe ser único.
- Una `persona_id` solo puede tener un registro de estudiante activo por programa.
- La `fecha_ingreso` no puede ser posterior a la fecha actual.
- Los `creditos_aprobados` no pueden ser negativos.
- No puede ser eliminado si tiene matrículas, pagos o cuenta corriente asociados.

---

#### PROFESOR
**DEFINICIÓN:** Representa a un empleado que cumple funciones docentes en la institución.

**PROPÓSITO:** Almacenar información académica específica del docente (grado académico, especialidad, categoría) adicional a su información laboral como empleado.

**REGLAS DE NEGOCIO:**
- Un profesor DEBE ser un empleado activo (`empleado_id` obligatorio).
- Una `persona_id` solo puede tener un registro de profesor activo.
- No puede ser eliminado si tiene cursos ofertados asignados.
- El `grado_academico` debe ser igual o superior al nivel del programa donde enseña (SUNEDU).

---

#### AUTORIDAD
**DEFINICIÓN:** Representa la asignación de un cargo directivo a una persona en una unidad organizativa o programa específico durante un periodo determinado.

**PROPÓSITO:** Registrar quiénes ocupan los cargos de autoridad (Rector, Decano, Director) en cada nivel del organigrama institucional para firma de documentos oficiales y toma de decisiones.

**REGLAS DE NEGOCIO:**
- Una persona no puede tener dos autoridades vigentes del mismo tipo simultáneamente.
- Si `fecha_fin` es NULL, se considera autoridad vigente actual.
- Solo puede existir una autoridad vigente por tipo en cada unidad/programa.
- Si es Rector, tanto `unidad_organizativa_id` como `programa_academico_id` deben ser NULL.
- Si es Decano, debe tener `unidad_organizativa_id` y `programa_academico_id` debe ser NULL.
- Si es Director de Programa, debe tener `programa_academico_id`.

---

### 📚 MÓDULO: CURRICULUM

#### PROGRAMA_ACADEMICO
**DEFINICIÓN:** Representa una carrera profesional o programa de estudios ofrecido por la institución.

**PROPÓSITO:** Definir las características académicas de cada carrera (nivel, modalidad, duración, créditos totales, título otorgado) y su vinculación con la unidad organizativa que lo administra.

**REGLAS DE NEGOCIO:**
- El `codigo` debe ser único.
- Debe pertenecer a una `unidad_organizativa_id` activa.
- La `duracion_semestres` debe ser consistente con `duracion_anios`.
- No puede ser eliminado si tiene estudiantes matriculados o planes académicos asociados.
- El `cupo_maximo_anual` debe ser mayor a cero.

---

#### PERIODO_ACADEMICO
**DEFINICIÓN:** Representa un ciclo lectivo temporal en el cual se dictan cursos y se realizan actividades académicas.

**PROPÓSITO:** Definir los periodos de tiempo (semestre, trimestre, anual) en los que se organizan las actividades académicas de la institución.

**REGLAS DE NEGOCIO:**
- El `codigo` debe ser único (ej: "2025-I", "2025-II").
- Las fechas no pueden superponerse con otros periodos del mismo `tipo_periodo`.
- La `fecha_fin` debe ser posterior a `fecha_inicio`.
- No puede ser eliminado si tiene cursos ofertados o matrículas asociadas.

---

#### CURSO
**DEFINICIÓN:** Representa una asignatura o materia genérica independiente de programas específicos.

**PROPÓSITO:** Definir las características pedagógicas básicas de una asignatura (nombre, descripción, horas) que posteriormente será asociada a planes de estudio específicos.

**REGLAS DE NEGOCIO:**
- El `codigo_curso` debe ser único.
- Las `horas_semanales` deben ser la suma de `horas_teoricas` + `horas_practicas`.
- No puede ser eliminado si está asociado a planes académicos o tiene requisitos.

---

#### PLAN_ACADEMICO
**DEFINICIÓN:** Representa la estructura curricular oficial de un programa académico en una versión y año específico.

**PROPÓSITO:** Definir qué versión del plan de estudios está vigente para cada programa, con su distribución de créditos y fechas de vigencia.

**REGLAS DE NEGOCIO:**
- El `codigo` debe ser único.
- Solo puede existir un plan vigente por programa en un momento dado.
- Los `creditos_totales` deben coincidir con la suma de créditos de los cursos del plan.
- La `fecha_vigencia_fin` debe ser posterior a `fecha_vigencia_inicio`.
- No puede ser eliminado si tiene estudiantes activos que ingresaron bajo ese plan.

---

#### PLAN_CURSO
**DEFINICIÓN:** Representa la asociación de un curso específico dentro de un plan académico, definiendo ciclo, créditos y tipo.

**PROPÓSITO:** Establecer qué cursos forman parte de un plan de estudios, en qué ciclo se dictan, cuántos créditos valen y si son obligatorios o electivos.

**REGLAS DE NEGOCIO:**
- Un curso no puede aparecer dos veces en el mismo plan.
- El `ciclo` debe estar dentro del rango de la `duracion_semestres` del programa.
- Los `creditos` deben ser mayores a cero.
- No puede ser eliminado si tiene cursos ofertados asociados.

---

#### REQUISITO_CURSO
**DEFINICIÓN:** Representa la relación de dependencia académica entre dos cursos (prerequisito o correquisito).

**PROPÓSITO:** Establecer qué cursos deben ser aprobados previamente (prerequisito) o cursados simultáneamente (correquisito) para poder matricularse en otro curso.

**REGLAS DE NEGOCIO:**
- Un curso no puede ser requisito de sí mismo.
- No pueden existir ciclos en la cadena de requisitos.
- El `curso_requisito` debe estar en un ciclo anterior al `curso` (para prerequisitos).
- La `nota_minima_requerida` debe estar entre 0 y 20 (sistema vigesimal peruano).

---

#### MODALIDAD
**DEFINICIÓN:** Representa la forma de dictado de un curso ofertado (presencial, virtual, híbrido, semipresencial).

**PROPÓSITO:** Definir cómo se imparte una sección de curso para determinar recursos necesarios (aula física vs plataforma virtual) y cumplir con políticas educativas institucionales.

**REGLAS DE NEGOCIO:**
- El `nombre` debe ser único.
- Si `requiere_aula` es true, los cursos ofertados deben tener `localizacion_id`.
- Si `requiere_plataforma` es true, los cursos ofertados deben tener `url_plataforma`.
- No puede ser eliminada si tiene cursos ofertados asociados.

**JUSTIFICACIÓN TÉCNICA:**
- Un mismo curso puede dictarse en múltiples modalidades simultáneamente.
- Permite gestión flexible de recursos físicos y digitales.
- Facilita reportes institucionales por modalidad (SUNEDU, auditorías).

---

#### CURSO_OFERTADO
**DEFINICIÓN:** Representa una sección específica de un curso que se dicta en un periodo académico concreto, con profesor, aula y horarios asignados.

**PROPÓSITO:** Gestionar la oferta real de cursos por periodo, con capacidad, modalidad, profesor asignado y estado de apertura para permitir matrículas.

**REGLAS DE NEGOCIO:**
- El `codigo_seccion` debe ser único dentro del mismo curso y periodo.
- Las `vacantes_disponibles` no pueden ser mayores a `capacidad_maxima`.
- Si `estado` es "CERRADA", no se permiten nuevas matrículas.
- Debe tener `localizacion_id` si la modalidad requiere aula física.
- Debe tener `url_plataforma` si la modalidad requiere plataforma virtual.
- No puede ser eliminado si tiene matrículas registradas.

---

#### HORARIO
**DEFINICIÓN:** Representa un bloque de tiempo específico dentro de la semana en que se dicta una sesión de un curso ofertado.

**PROPÓSITO:** Definir cuándo, dónde y qué tipo de sesión (teoría, práctica, laboratorio) se realiza para un curso ofertado, permitiendo gestión de conflictos de horarios.

**REGLAS DE NEGOCIO:**
- No pueden existir dos horarios que se superpongan en la misma `localizacion_id`.
- La `hora_fin` debe ser posterior a `hora_inicio`.
- El `dia_semana` debe estar entre 1 (lunes) y 7 (domingo).
- El profesor del curso no puede tener dos horarios superpuestos.

---

#### SILABO
**DEFINICIÓN:** Representa el documento académico oficial que define competencias, unidades, actividades y criterios de evaluación de un curso para un año académico específico.

**PROPÓSITO:** Garantizar estandarización y calidad académica en todas las sedes. Documenta qué se enseña, cómo se evalúa y mantiene historial de versiones para auditorías institucionales y SUNEDU.

**REGLAS DE NEGOCIO:**
- Solo puede existir un sílabo vigente por curso por año académico.
- El `estado` "VIGENTE" implica que fue aprobado y está en uso.
- El `porcentaje_calidad` se calcula automáticamente según completitud de información.
- Si el `estado` es "APROBADO", debe tener `fecha_aprobacion` y `aprobado_por`.
- No puede ser eliminado; solo puede cambiar de estado a "OBSOLETO".

**JUSTIFICACIÓN ACADÉMICA:**
- La institución requiere un sílabo unificado por curso (sin variaciones por sede o docente).
- Responde a procesos de movilidad estudiantil entre sedes.
- Permite auditorías internas y externas (SUNEDU, QA).
- Conserva trazabilidad institucional año tras año.

---

#### SILABO_UNIDAD
**DEFINICIÓN:** Representa una unidad didáctica dentro del sílabo, que agrupa contenidos temáticos por semanas.

**PROPÓSITO:** Estructurar el contenido del curso en bloques temáticos con duración definida en semanas.

**REGLAS DE NEGOCIO:**
- El `numero_unidad` debe ser único dentro del mismo sílabo.
- La `semana_inicio` debe ser menor o igual a `semana_fin`.
- Las semanas no pueden superponerse entre unidades del mismo sílabo.

---

#### SILABO_ACTIVIDAD
**DEFINICIÓN:** Representa una actividad de aprendizaje o evaluación dentro de una unidad del sílabo.

**PROPÓSITO:** Definir las actividades formativas y sumativas del curso con su descripción, tipo y ponderación en la nota final.

**REGLAS DE NEGOCIO:**
- La suma de `ponderacion` de todas las actividades del sílabo debe ser 100%.
- El `tipo` puede ser "FORMATIVA" (práctica) o "SUMATIVA" (evaluación).
- No puede ser eliminada si tiene notas asociadas.

---

#### SILABO_HISTORIAL
**DEFINICIÓN:** Representa el registro de cambios, revisiones y aprobaciones realizadas sobre un sílabo.

**PROPÓSITO:** Mantener trazabilidad de modificaciones para auditorías institucionales y control de calidad académica.

**REGLAS DE NEGOCIO:**
- Cada cambio de estado del sílabo debe generar un registro en el historial.
- El campo `usuario` debe registrar quién realizó la acción.
- No puede ser eliminado (integridad histórica).

---

### 📝 MÓDULO: ENROLLMENT

#### MATRICULA
**DEFINICIÓN:** Representa la inscripción formal de un estudiante en un curso ofertado específico durante un periodo académico.

**PROPÓSITO:** Registrar la participación del estudiante en un curso, permitir control de asistencias, evaluaciones y gestión de retiros.

**REGLAS DE NEGOCIO:**
- Un estudiante no puede matricularse dos veces en el mismo curso ofertado.
- El estudiante debe cumplir los requisitos previos del curso.
- No puede matricularse en cursos con horarios superpuestos.
- Si el curso está "CERRADO", no se permite matrícula.
- Si `estado_matricula` es "RETIRADO", debe tener `fecha_retiro`.
- Los `creditos_matriculados` se suman a `creditos_cursando` del estudiante.

---

#### EVALUACION_CRITERIO
**DEFINICIÓN:** Representa un criterio de evaluación específico definido por el profesor para un curso ofertado.

**PROPÓSITO:** Establecer los componentes de evaluación (exámenes, prácticas, tareas) con su ponderación en la nota final del curso.

**REGLAS DE NEGOCIO:**
- La suma de `peso_porcentaje` de todos los criterios debe ser 100%.
- El `peso_porcentaje` debe estar entre 0 y 100.
- No puede ser eliminado si tiene notas asociadas.
- Debe existir al menos un criterio de evaluación por curso ofertado.

---

#### EVALUACION_NOTA
**DEFINICIÓN:** Representa la calificación obtenida por un estudiante en un criterio de evaluación específico.

**PROPÓSITO:** Registrar las notas de cada componente evaluativo del curso y calcular la nota final considerando recuperaciones.

**REGLAS DE NEGOCIO:**
- Las notas deben estar en el rango 0-20 (sistema vigesimal peruano).
- La `nota_final` es el máximo entre `nota` y `nota_recuperacion`.
- Una matrícula no puede tener dos notas para el mismo criterio.
- La nota mínima aprobatoria institucional es 10.5 (redondeado a 11).

---

#### ASISTENCIA_ALUMNO
**DEFINICIÓN:** Representa el registro de asistencia de un estudiante a una sesión específica de clase.

**PROPÓSITO:** Controlar la asistencia del estudiante, permitir cálculo de porcentaje de asistencia y aplicar políticas de asistencia mínima.

**REGLAS DE NEGOCIO:**
- Un estudiante no puede tener dos registros de asistencia para la misma fecha y curso.
- Si el porcentaje de asistencia es menor al 70%, el estudiante puede ser inhabilitado del curso (política institucional).
- El `estado` puede ser: PRESENTE, TARDE, AUSENTE, JUSTIFICADO.

---

### 💰 MÓDULO: FINANCE

#### CUENTA_CORRIENTE_ALUMNO
**DEFINICIÓN:** Representa una deuda o cargo financiero asignado a un estudiante por conceptos académicos.

**PROPÓSITO:** Gestionar las cuentas por cobrar de cada estudiante (matrícula, pensiones, derechos de examen, moras) con control de montos pagados y pendientes.

**REGLAS DE NEGOCIO:**
- El `monto_pendiente` debe ser igual a `monto` - `monto_pagado`.
- Si el `estado` es "PAGADO", el `monto_pendiente` debe ser cero.
- Si `fecha_vencimiento` < fecha actual y `monto_pendiente` > 0, el estado cambia a "VENCIDO".
- No puede ser eliminada si tiene pagos aplicados.
- El `monto` debe ser mayor a cero.

---

#### PAGO
**DEFINICIÓN:** Representa un pago realizado por un estudiante, que puede aplicarse a una o varias deudas de su cuenta corriente.

**PROPÓSITO:** Registrar los pagos recibidos con su método, fecha y referencia bancaria, permitiendo distribución del monto en múltiples deudas y generación de comprobantes.

**REGLAS DE NEGOCIO:**
- El `numero_recibo` debe ser único.
- El `monto_aplicado` no puede ser mayor a `monto_pagado`.
- El `monto_pendiente_aplicar` debe ser igual a `monto_pagado` - `monto_aplicado`.
- Si `monto_pendiente_aplicar` > 0, el estudiante tiene saldo a favor.
- No puede ser eliminado si tiene detalles de aplicación asociados.

---

#### PAGO_DETALLE_DEUDA
**DEFINICIÓN:** Representa la aplicación parcial o total de un pago a una deuda específica de la cuenta corriente del estudiante.

**PROPÓSITO:** Vincular cada pago con las deudas que cancela, permitiendo trazabilidad de qué deudas fueron pagadas con qué comprobantes.

**REGLAS DE NEGOCIO:**
- El `monto_aplicado` no puede ser mayor al `monto_pendiente` de la deuda.
- La suma de `monto_aplicado` de un pago no puede exceder el `monto_pagado`.
- No puede aplicarse un pago a una deuda "ANULADA".

---

### 🔐 MÓDULO: SECURITY

#### ROL
**DEFINICIÓN:** Representa un conjunto de permisos agrupados que define el nivel de acceso de un usuario en el sistema.

**PROPÓSITO:** Establecer perfiles de usuario (Administrador, Docente, Estudiante, Secretaria) con permisos específicos para control de acceso basado en roles (RBAC).

**REGLAS DE NEGOCIO:**
- El `nombre` debe ser único.
- No puede ser eliminado si tiene usuarios asignados.
- Debe tener al menos un permiso asociado.

---

#### PERMISO
**DEFINICIÓN:** Representa una acción específica que puede realizarse sobre un recurso del sistema.

**PROPÓSITO:** Definir de manera granular las operaciones permitidas (crear, leer, actualizar, eliminar) sobre cada módulo del sistema.

**REGLAS DE NEGOCIO:**
- El `codigo` debe ser único.
- El formato debe ser: MODULO_RECURSO_ACCION (ej: ACADEMIC_CURSO_CREATE).
- No puede ser eliminado si está asociado a roles.

---

#### ROL_PERMISO
**DEFINICIÓN:** Representa la asociación entre un rol y los permisos que tiene asignados.

**PROPÓSITO:** Implementar la relación muchos-a-muchos entre roles y permisos para control de acceso.

**REGLAS DE NEGOCIO:**
- Un rol no puede tener el mismo permiso duplicado.
- Al eliminar un rol, se eliminan sus asociaciones de permisos.

---

#### USER
**DEFINICIÓN:** Representa las credenciales de autenticación de un usuario del sistema.

**PROPÓSITO:** Gestionar el acceso al sistema mediante username único, sin duplicar información personal que ya existe en otras tablas.

**REGLAS DE NEGOCIO:**
- El `username` debe ser único.
- El `username` no puede modificarse una vez creado.
- Debe estar asociado a un `AuthUsuario` para tener acceso completo.

---

#### AUTH_USUARIO
**DEFINICIÓN:** Representa la vinculación entre una persona, sus credenciales de acceso y su rol en el sistema.

**PROPÓSITO:** Centralizar la autenticación y autorización, vinculando personas físicas con sus credenciales y permisos de acceso.

**REGLAS DE NEGOCIO:**
- Una persona solo puede tener un usuario activo.
- El `username` debe ser único.
- Después de 5 `intentos_fallidos`, la cuenta se bloquea automáticamente.
- Si `requiere_cambio_password` es true, debe cambiar contraseña en el siguiente acceso.
- El `token_recuperacion` expira según `fecha_expiracion_token`.

---

#### REFRESH_TOKEN
**DEFINICIÓN:** Representa un token de actualización JWT para mantener sesiones activas sin reautenticación constante.

**PROPÓSITO:** Implementar autenticación basada en tokens con renovación automática para mejorar experiencia de usuario y seguridad.

**REGLAS DE NEGOCIO:**
- El `token` debe ser único.
- Los tokens expirados deben eliminarse automáticamente.
- Un usuario no puede tener más de 5 refresh tokens activos simultáneamente.
- Si se detecta uso de un token revocado, bloquear la cuenta por seguridad.

---

## 🏢 MÓDULO 1: CORE (Configuración y Estructura)

### 1. UNIVERSIDAD
**Descripción:** Entidad raíz que almacena la configuración global, identidad institucional y parámetros del sistema para la instancia instalada.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único (siempre será 1 en Single-Tenant). | `1` |
| `codigo` | Código interno o abreviatura de la institución. | `"UPEU"` |
| `nombre` | Nombre legal completo de la universidad. | `"Universidad Peruana Unión"` |
| `dominio` | Dominio web principal para correos institucionales. | `"upeu.edu.pe"` |
| `ruc` | Registro Único de Contribuyentes (Identificación fiscal). | `"20138122256"` |
| `tipo` | Clasificación legal de la institución. | `"PRIVADA"` |
| `website` | URL del sitio web oficial. | `"https://www.upeu.edu.pe"` |
| `logo_url` | Ruta relativa o URL del logotipo oficial. | `"/assets/img/logo-main.png"` |
| `zona_horaria` | Zona horaria para fechas del sistema. | `"America/Lima"` |
| `locale` | Configuración regional (idioma y país). | `"es_PE"` |
| `configuracion` | JSON con parámetros globales (tema, límites, flags). | `{"theme":"blue", "mantenimiento":false}` |
| `plan` | Tipo de plan contratado (si aplica licenciamiento). | `"PREMIUM"` |
| `estado` | Estado operativo de la instancia. | `"ACTIVO"` |
| `fecha_vencimiento` | Fecha de vencimiento de la licencia. | `"2025-12-31"` |
| `max_estudiantes` | Límite máximo de estudiantes permitidos. | `15000` |
| `max_docentes` | Límite máximo de docentes permitidos. | `500` |
| `total_estudiantes` | Contador actual de estudiantes registrados. | `8500` |
| `total_docentes` | Contador actual de docentes registrados. | `320` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-20 14:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 2. PERSONA
**Descripción:** Repositorio maestro de datos demográficos. Centraliza la información personal para evitar duplicidad entre roles (un alumno puede ser también empleado).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Llave primaria única de la persona. | `1005` |
| `nombres` | Nombres de pila del individuo. | `"Juan Carlos"` |
| `apellido_paterno` | Primer apellido. | `"Pérez"` |
| `apellido_materno` | Segundo apellido. | `"Gómez"` |
| `tipo_documento` | Tipo de documento de identidad legal. | `"DNI"` |
| `numero_documento` | Número del documento de identidad. | `"45896321"` |
| `fecha_nacimiento` | Fecha de nacimiento registrada. | `"1995-05-15"` |
| `genero` | Género biológico o registral. | `"M"` |
| `estado_civil` | Estado civil legal. | `"SOLTERO"` |
| `direccion` | Dirección de residencia actual. | `"Av. Brasil 123, Lima"` |
| `telefono` | Número de teléfono fijo. | `"01-456-7890"` |
| `celular` | Número de teléfono móvil de contacto principal. | `"987654321"` |
| `email` | Correo electrónico personal o institucional. | `"juan.perez@upeu.edu.pe"` |
| `foto_url` | Ruta de la fotografía de perfil. | `"/storage/photos/45896321.jpg"` |
| `created_at` | Fecha de creación del registro. | `"2024-01-15 10:30:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-20 14:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 3. TIPO_UNIDAD
**Descripción:** Catálogo que define la jerarquía y clasificación de las áreas organizativas (ej. Facultad vs. Departamento).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del tipo de unidad. | `1` |
| `nombre` | Nombre descriptivo del tipo. | `"Facultad"` |
| `descripcion` | Explicación de la función de este tipo de unidad. | `"Unidad académica mayor que agrupa escuelas"` |
| `nivel` | Nivel jerárquico numérico (1=Alto, 5=Bajo). | `1` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 4. UNIDAD_ORGANIZATIVA
**Descripción:** Representa el organigrama real de la institución (Oficinas, Facultades, Departamentos, Escuelas).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la unidad. | `50` |
| `nombre` | Nombre oficial de la unidad. | `"Facultad de Ingeniería y Arquitectura"` |
| `codigo` | Código interno administrativo. | `"FIA"` |
| `sigla` | Abreviatura oficial. | `"FIA-J"` |
| `descripcion` | Descripción de las funciones de la unidad. | `"Facultad encargada de las carreras de ingeniería"` |
| `tipo_unidad_id` | FK al tipo de unidad que es. | `1` |
| `unidad_padre_id` | FK a la unidad superior (Jerarquía). | `10` |
| `localizacion_id` | FK a la ubicación física principal. | `5` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 5. TIPO_LOCALIZACION
**Descripción:** Catálogo de clasificación de espacios físicos (Campus, Edificio, Aula, Laboratorio).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del tipo. | `1` |
| `nombre` | Nombre del tipo de localización. | `"Campus"` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 6. LOCALIZACION
**Descripción:** Espacios físicos donde ocurren las actividades académicas o administrativas.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del espacio. | `101` |
| `nombre` | Nombre común del espacio. | `"Aula B-204"` |
| `codigo` | Código de infraestructura. | `"B204"` |
| `direccion` | Dirección física o referencia interna. | `"Pabellón B, 2do Piso"` |
| `telefono` | Teléfono o anexo del espacio. | `"Anexo 405"` |
| `email` | Correo de contacto del espacio. | `null` |
| `es_principal` | Indica si es una sede principal. | `false` |
| `tipo_localizacion_id` | FK al tipo (Aula, Laboratorio, Campus). | `3` |
| `localizacion_padre_id` | FK a la ubicación contenedora (Edificio/Sede). | `5` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

## 🔐 MÓDULO 2: SECURITY (Seguridad)

### 7. AUTH_USUARIO
**Descripción:** Credenciales de acceso y estado de la cuenta vinculadas a una Persona.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del usuario. | `500` |
| `persona_id` | FK a la persona dueña de la cuenta. | `1005` |
| `rol_id` | FK al rol principal asignado. | `2` |
| `password_hash` | Contraseña encriptada (BCrypt/Argon2). | `"$2a$10$XyZ..."` |
| `ultimo_acceso` | Timestamp del último login exitoso. | `"2024-03-20 14:30:00"` |
| `intentos_fallidos` | Contador para bloqueo por fuerza bruta. | `0` |
| `fecha_bloqueo` | Fecha/hora en que se bloqueó la cuenta. | `null` |
| `requiere_cambio_password` | Flag para forzar cambio de clave en próximo login. | `false` |
| `fecha_ultimo_cambio_password` | Fecha del último cambio de contraseña. | `"2024-01-15 10:00:00"` |
| `token_recuperacion` | Token temporal para recuperar contraseña. | `null` |
| `fecha_expiracion_token` | Fecha de expiración del token de recuperación. | `null` |
| `created_at` | Fecha de creación del registro. | `"2024-01-15 10:30:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-20 14:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 8. USERS
**Descripción:** Tabla legacy de usuarios para compatibilidad con sistemas anteriores.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del usuario. | `100` |
| `username` | Nombre de usuario para login. | `"jperez"` |
| `email` | Correo electrónico del usuario. | `"juan.perez@upeu.edu.pe"` |
| `password_hash` | Contraseña encriptada. | `"$2a$10$ABC..."` |
| `first_name` | Nombres del usuario. | `"Juan Carlos"` |
| `last_name` | Apellidos del usuario. | `"Pérez Gómez"` |
| `phone` | Teléfono de contacto. | `"987654321"` |
| `role` | Rol asignado (formato legacy). | `"ESTUDIANTE"` |
| `status` | Estado de la cuenta. | `"ACTIVO"` |
| `last_login` | Último acceso al sistema. | `"2024-03-20 14:30:00"` |
| `persona_id` | FK a la tabla Persona. | `1005` |
| `intentos_fallidos` | Contador de intentos fallidos. | `0` |
| `fecha_bloqueo` | Fecha de bloqueo de la cuenta. | `null` |
| `token_recuperacion` | Token para recuperar contraseña. | `null` |
| `fecha_expiracion_token` | Expiración del token. | `null` |
| `created_at` | Fecha de creación del registro. | `"2024-01-15 10:30:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-20 14:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 9. ROL
**Descripción:** Perfiles de seguridad que agrupan un conjunto de permisos.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del rol. | `2` |
| `nombre` | Nombre técnico del rol (prefijo ROLE_). | `"ROLE_ESTUDIANTE"` |
| `descripcion` | Descripción legible de su alcance. | `"Acceso al portal del alumno y matrícula"` |
| `es_sistema` | Indica si es un rol protegido (no borrable). | `true` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 10. PERMISO
**Descripción:** Acciones atómicas específicas que se pueden realizar en el sistema.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del permiso. | `50` |
| `nombre_clave` | Identificador técnico para uso en código. | `"curso:crear"` |
| `descripcion` | Descripción legible del permiso. | `"Permite registrar nuevos cursos"` |
| `modulo` | Módulo funcional al que pertenece. | `"ACADEMICO"` |
| `recurso` | Entidad sobre la que actúa. | `"CURSO"` |
| `accion` |    . | `"WRITE"` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 11. ROL_PERMISO
**Descripción:** Tabla intermedia que asigna permisos específicos a cada rol.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la asignación. | `100` |
| `rol_id` | FK al rol que recibe el permiso. | `1` |
| `permiso_id` | FK al permiso asignado. | `50` |
| `puede_delegar` | Indica si el usuario puede asignar este permiso a otros. | `true` |
| `restriccion` | Restricciones adicionales en formato JSON. | `{"solo_propios": true}` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 12. REFRESH_TOKENS
**Descripción:** Tokens de actualización para mantener sesiones activas sin reautenticación.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del token. | `5000` |
| `token` | Valor único del refresh token. | `"eyJhbGciOiJIUzI1NiIs..."` |
| `expires_at` | Fecha y hora de expiración del token. | `"2024-04-20 14:30:00"` |
| `auth_usuario_id` | FK al usuario dueño del token. | `500` |
| `is_revoked` | Indica si el token ha sido revocado. | `false` |
| `created_at` | Fecha de creación del registro. | `"2024-03-20 14:30:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-20 14:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

## 👥 MÓDULO 3: PEOPLE (Gestión de Personas)

### 13. ESTUDIANTE
**Descripción:** Extensión del perfil de Persona con datos puramente académicos del alumno.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del estudiante. | `200` |
| `persona_id` | FK a los datos personales base. | `1005` |
| `programa_id` | FK a la carrera que estudia. | `15` |
| `codigo_estudiante` | Código de matrícula único institucional. | `"202410056"` |
| `fecha_ingreso` | Fecha de ingreso a la universidad. | `"2024-03-01"` |
| `ciclo_actual` | Ciclo académico relativo en el que se encuentra. | `1` |
| `creditos_aprobados` | Total de créditos aprobados acumulados. | `0` |
| `creditos_cursando` | Créditos que está cursando actualmente. | `22` |
| `creditos_obligatorios_aprobados` | Créditos obligatorios aprobados. | `0` |
| `creditos_electivos_aprobados` | Créditos electivos aprobados. | `0` |
| `promedio_ponderado` | Promedio acumulado de notas. | `0.00` |
| `modalidad_ingreso` | Forma de ingreso (Examen, Traslado, etc.). | `"EXAMEN_ORDINARIO"` |
| `estado_academico` | Situación actual del estudiante. | `"REGULAR"` |
| `tipo_estudiante` | Clasificación del estudiante. | `"PREGRADO"` |
| `created_at` | Fecha de creación del registro. | `"2024-03-01 10:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-01 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 14. PROFESOR
**Descripción:** Perfil académico y laboral del docente.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del profesor. | `50` |
| `persona_id` | FK a los datos personales base. | `800` |
| `unidad_id` | FK al departamento académico al que pertenece. | `50` |
| `grado_academico` | Máximo grado académico obtenido. | `"MAGISTER"` |
| `especialidad` | Área de especialización principal. | `"Inteligencia Artificial"` |
| `categoria_docente` | Categoría según escalafón. | `"PRINCIPAL"` |
| `condicion_docente` | Condición laboral del docente. | `"NOMBRADO"` |
| `dedicacion` | Tipo de carga horaria. | `"TIEMPO_COMPLETO"` |
| `codigo_orcid` | Identificador ORCID para publicaciones. | `"0000-0002-1234-5678"` |
| `codigo_renacyt` | Código RENACYT (Perú). | `"P0012345"` |
| `created_at` | Fecha de creación del registro. | `"2020-03-01 10:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-15 09:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 15. EMPLEADO
**Descripción:** Personal administrativo y de servicio de la institución.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del empleado. | `30` |
| `persona_id` | FK a los datos personales base. | `900` |
| `unidad_organizativa_id` | FK a la oficina donde labora. | `10` |
| `codigo_empleado` | Código de planilla o identificación interna. | `"EMP-0045"` |
| `fecha_ingreso` | Fecha de inicio de labores. | `"2022-06-01"` |
| `fecha_cese` | Fecha de cese (si aplica). | `null` |
| `cargo` | Puesto que ocupa. | `"Secretaria Académica"` |
| `tipo_contrato` | Modalidad de contratación. | `"CAS"` |
| `regimen_laboral` | Régimen laboral aplicable. | `"PRIVADO"` |
| `salario` | Remuneración mensual. | `2500.00` |
| `estado_laboral` | Estado actual del empleado. | `"ACTIVO"` |
| `created_at` | Fecha de creación del registro. | `"2022-06-01 08:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-15 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 16. TIPO_AUTORIDAD
**Descripción:** Catálogo de cargos de autoridad (Rector, Decano, Director, etc.).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del tipo de autoridad. | `1` |
| `nombre` | Nombre del cargo de autoridad. | `"Rector"` |
| `codigo` | Código interno del cargo. | `"RECT"` |
| `nivel_jerarquia` | Nivel en la jerarquía (1=más alto). | `1` |
| `descripcion` | Descripción de las funciones del cargo. | `"Máxima autoridad de la universidad"` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 17. AUTORIDAD
**Descripción:** Registro histórico de personas que ocupan cargos de autoridad.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del registro de autoridad. | `10` |
| `persona_id` | FK a la persona que ocupa el cargo. | `500` |
| `tipo_autoridad_id` | FK al tipo de autoridad. | `1` |
| `fecha_inicio` | Fecha de inicio del mandato. | `"2023-01-01"` |
| `fecha_fin` | Fecha de fin del mandato (null si vigente). | `null` |
| `es_vigente` | Indica si el cargo está vigente. | `true` |
| `created_at` | Fecha de creación del registro. | `"2023-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2023-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

## 📚 MÓDULO 4: CURRICULUM (Diseño Curricular)

### 18. PROGRAMA_ACADEMICO
**Descripción:** Carreras profesionales o programas de estudio ofertados por la institución.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del programa. | `15` |
| `unidad_organizativa_id` | FK a la Facultad que lo administra. | `50` |
| `nombre` | Nombre comercial de la carrera. | `"Ingeniería de Sistemas"` |
| `codigo` | Código interno del programa. | `"P-IS"` |
| `nivel_academico` | Nivel del programa. | `"PREGRADO"` |
| `modalidad` | Modalidad de estudio. | `"PRESENCIAL"` |
| `duracion_anios` | Duración en años. | `5` |
| `duracion_semestres` | Duración en semestres. | `10` |
| `creditos_totales` | Créditos totales para egresar. | `220` |
| `titulo_otorgado` | Título profesional que se obtiene. | `"Ingeniero de Sistemas"` |
| `grado_academico` | Grado académico otorgado. | `"Bachiller en Ingeniería de Sistemas"` |
| `cupo_maximo_anual` | Vacantes máximas por año. | `120` |
| `nota_minima_ingreso` | Nota mínima requerida para ingreso. | `11.00` |
| `programa_padre_id` | FK a programa padre (para especialidades). | `null` |
| `fecha_creacion_programa` | Fecha de creación oficial del programa. | `"2010-03-15"` |
| `fecha_ultima_modificacion_plan` | Última modificación del plan de estudios. | `"2022-01-10"` |
| `estado` | Estado del programa. | `"ACTIVO"` |
| `created_at` | Fecha de creación del registro. | `"2010-03-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-10 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 19. PLAN_ACADEMICO
**Descripción:** Malla curricular específica vigente en un periodo (Plan de Estudios).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del plan. | `5` |
| `programa_academico_id` | FK al programa al que pertenece. | `15` |
| `codigo` | Código identificador del plan. | `"PLAN-2022"` |
| `version` | Número de versión del plan. | `"1.0"` |
| `nombre` | Nombre descriptivo del plan. | `"Malla Curricular 2022"` |
| `fecha_aprobacion` | Fecha de aprobación oficial. | `"2021-12-15"` |
| `fecha_vigencia_inicio` | Inicio de vigencia del plan. | `"2022-03-01"` |
| `fecha_vigencia_fin` | Fin de vigencia (null si vigente). | `null` |
| `creditos_totales` | Suma total de créditos del plan. | `220` |
| `creditos_obligatorios` | Créditos de cursos obligatorios. | `180` |
| `creditos_electivos` | Créditos de cursos electivos requeridos. | `40` |
| `duracion_semestres` | Duración en semestres. | `10` |
| `estado` | Estado del plan. | `"VIGENTE"` |
| `creditos_maximos_por_ciclo` | Máximo de créditos por ciclo. | `24` |
| `creditos_minimos_tiempo_completo` | Mínimo para tiempo completo. | `12` |
| `duracion_ciclo_meses` | Duración de cada ciclo en meses. | `4` |
| `created_at` | Fecha de creación del registro. | `"2021-12-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2022-01-10 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 20. CURSO
**Descripción:** Catálogo general de asignaturas disponibles (independiente de cuándo se dicten).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del curso. | `1001` |
| `codigo_curso` | Código único de la asignatura. | `"IS-101"` |
| `nombre` | Nombre oficial del curso. | `"Algoritmos y Estructura de Datos I"` |
| `descripcion` | Descripción detallada del contenido. | `"Introducción a la programación y lógica"` |
| `horas_teoricas` | Horas de teoría semanales. | `2` |
| `horas_practicas` | Horas de práctica semanales. | `4` |
| `horas_semanales` | Total de horas semanales. | `6` |
| `tipo_curso` | Clasificación del curso. | `"ESPECIALIDAD"` |
| `area_curricular` | Área curricular a la que pertenece. | `"FORMACION_PROFESIONAL"` |
| `created_at` | Fecha de creación del registro. | `"2020-01-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-10 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 21. PLAN_CURSO
**Descripción:** Tabla intermedia que asigna un Curso a un ciclo específico de un Plan.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la asignación. | `500` |
| `plan_academico_id` | FK al plan de estudios. | `5` |
| `curso_id` | FK al curso del catálogo. | `1001` |
| `creditos` | Valor en créditos para este plan específico. | `4` |
| `ciclo` | Ciclo sugerido (1 al 10). | `1` |
| `tipo_curso` | Tipo dentro del plan. | `"OBLIGATORIO"` |
| `es_obligatorio` | Indica si es obligatorio para egresar. | `true` |
| `created_at` | Fecha de creación del registro. | `"2021-12-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2021-12-15 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 22. REQUISITO_CURSO
**Descripción:** Define los prerrequisitos y correquisitos entre cursos.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del requisito. | `100` |
| `curso_id` | FK al curso que tiene el requisito. | `1002` |
| `curso_requisito_id` | FK al curso que es requisito. | `1001` |
| `tipo_requisito` | Tipo de requisito. | `"PRERREQUISITO"` |
| `es_obligatorio` | Indica si el requisito es obligatorio. | `true` |
| `nota_minima_requerida` | Nota mínima requerida en el curso requisito. | `11` |
| `observacion` | Observaciones adicionales. | `"Aprobar con nota mínima 11"` |
| `created_at` | Fecha de creación del registro. | `"2021-12-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2021-12-15 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 23. SILABO
**Descripción:** Documento oficial de planificación del curso con contenidos, competencias y evaluación.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del sílabo. | `80` |
| `curso_id` | FK al curso correspondiente. | `1001` |
| `anio_academico` | Año académico de vigencia. | `"2024"` |
| `version` | Número de versión del sílabo. | `1` |
| `estado` | Estado de aprobación. | `"APROBADO"` |
| `porcentaje_calidad` | Porcentaje de calidad del sílabo. | `95.5` |
| `fecha_aprobacion` | Fecha de aprobación oficial. | `"2024-02-15"` |
| `aprobado_por` | Nombre de quien aprobó. | `"Dr. Juan Pérez"` |
| `observaciones` | Observaciones generales. | `"Actualizado según competencias 2024"` |
| `competencias` | Competencias del curso. | `"Desarrolla lógica de programación..."` |
| `sumilla` | Resumen ejecutivo del curso. | `"El curso aborda los fundamentos..."` |
| `bibliografia` | Referencias bibliográficas. | `"Cormen, T. Introduction to Algorithms..."` |
| `metodologia` | Metodología de enseñanza. | `"Aprendizaje basado en proyectos..."` |
| `recursos_didacticos` | Recursos necesarios. | `"Laboratorio de cómputo, proyector..."` |
| `created_at` | Fecha de creación del registro. | `"2024-02-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-02-15 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 24. SILABO_UNIDAD
**Descripción:** Unidades temáticas que componen un sílabo.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la unidad. | `200` |
| `silabo_id` | FK al sílabo padre. | `80` |
| `numero_unidad` | Número secuencial de la unidad. | `1` |
| `titulo` | Título de la unidad. | `"Introducción a los Algoritmos"` |
| `semana_inicio` | Semana de inicio. | `1` |
| `semana_fin` | Semana de finalización. | `4` |
| `contenidos` | Contenidos temáticos detallados. | `"Definición, características, ejemplos..."` |
| `logro_aprendizaje` | Logro esperado al finalizar la unidad. | `"Identifica y diseña algoritmos básicos"` |
| `estrategias_ensenanza` | Estrategias didácticas a utilizar. | `"Clase magistral, ejercicios prácticos"` |
| `created_at` | Fecha de creación del registro. | `"2024-02-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-02-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 25. SILABO_ACTIVIDAD
**Descripción:** Actividades evaluativas dentro de cada unidad del sílabo.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la actividad. | `300` |
| `silabo_unidad_id` | FK a la unidad del sílabo. | `200` |
| `tipo` | Tipo de actividad. | `"PRACTICA"` |
| `nombre` | Nombre de la actividad. | `"Práctica Calificada 1"` |
| `descripcion` | Descripción de la actividad. | `"Ejercicios de pseudocódigo"` |
| `ponderacion` | Peso en la nota de la unidad. | `20.00` |
| `semana_programada` | Semana programada para la actividad. | `4` |
| `instrumento_evaluacion` | Instrumento de evaluación. | `"Rúbrica"` |
| `indicadores` | Indicadores de logro. | `"Correctitud, eficiencia, estilo"` |
| `criterios_evaluacion` | Criterios de calificación. | `"Algoritmo correcto: 10pts, Eficiente: 5pts"` |
| `created_at` | Fecha de creación del registro. | `"2024-02-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-02-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 26. SILABO_HISTORIAL
**Descripción:** Registro de auditoría de cambios realizados en los sílabos.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del registro. | `50` |
| `silabo_id` | FK al sílabo modificado. | `80` |
| `fecha` | Fecha y hora del cambio. | `"2024-02-15 10:30:00"` |
| `accion` | Tipo de acción realizada. | `"APROBACION"` |
| `usuario` | Usuario que realizó el cambio. | `"admin@upeu.edu.pe"` |
| `comentarios` | Comentarios sobre el cambio. | `"Sílabo aprobado por el director"` |
| `version_anterior` | Versión antes del cambio. | `0` |
| `version_nueva` | Versión después del cambio. | `1` |

---

## 🎓 MÓDULO 5: ENROLLMENT (Matrícula y Oferta)

### 27. PERIODO_ACADEMICO
**Descripción:** Ciclo temporal en el que se desarrollan las actividades académicas (Semestre).

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del periodo. | `20` |
| `codigo_periodo` | Código legible del periodo. | `"2024-1"` |
| `nombre` | Nombre descriptivo. | `"Semestre Académico 2024-I"` |
| `anio` | Año del periodo. | `2024` |
| `tipo_periodo` | Tipo de periodo. | `"SEMESTRE"` |
| `numero_periodo` | Número del periodo en el año. | `1` |
| `fecha_inicio` | Fecha de inicio del periodo. | `"2024-03-15"` |
| `fecha_fin` | Fecha de fin del periodo. | `"2024-07-15"` |
| `fecha_inicio_matricula` | Inicio del proceso de matrícula. | `"2024-03-01"` |
| `fecha_fin_matricula` | Fin del proceso de matrícula. | `"2024-03-14"` |
| `fecha_inicio_clases` | Inicio de clases. | `"2024-03-18"` |
| `fecha_fin_clases` | Fin de clases. | `"2024-07-12"` |
| `estado` | Estado del periodo. | `"ACTIVO"` |
| `es_actual` | Indica si es el periodo corriente. | `true` |
| `descripcion` | Descripción adicional. | `"Primer semestre del año 2024"` |
| `created_at` | Fecha de creación del registro. | `"2024-01-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-01 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 28. MODALIDAD
**Descripción:** Tipos de modalidad de dictado de clases.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la modalidad. | `1` |
| `codigo` | Código de la modalidad. | `"PRES"` |
| `nombre` | Nombre de la modalidad. | `"Presencial"` |
| `descripcion` | Descripción de la modalidad. | `"Clases 100% en campus"` |
| `requiere_aula` | Indica si requiere aula física. | `true` |
| `requiere_plataforma` | Indica si requiere plataforma virtual. | `false` |
| `porcentaje_presencialidad` | Porcentaje de presencialidad requerido. | `100` |
| `color_hex` | Color para visualización en calendarios. | `"#4CAF50"` |
| `created_at` | Fecha de creación del registro. | `"2024-01-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-01-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 29. CURSO_OFERTADO
**Descripción:** Instancia real de un curso ("Sección" o "Grupo") que se abre en un periodo específico.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la sección. | `3000` |
| `plan_curso_id` | FK al curso del plan que se dicta. | `500` |
| `periodo_academico_id` | FK al periodo académico. | `20` |
| `profesor_id` | FK al docente asignado. | `50` |
| `codigo_seccion` | Código del grupo o sección. | `"G1"` |
| `capacidad_maxima` | Aforo máximo de estudiantes. | `40` |
| `vacantes_disponibles` | Cupos restantes disponibles. | `5` |
| `modalidad_id` | FK a la modalidad de dictado. | `1` |
| `localizacion_id` | FK a la sede principal. | `101` |
| `url_plataforma` | URL de la plataforma virtual (si aplica). | `"https://aula.upeu.edu.pe/course/3000"` |
| `estado` | Estado de la oferta. | `"ABIERTO"` |
| `observaciones` | Observaciones adicionales. | `"Grupo con horario extendido"` |
| `created_at` | Fecha de creación del registro. | `"2024-02-20 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-10 10:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 30. HORARIO
**Descripción:** Programación semanal de sesiones para una sección de curso.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del horario. | `4000` |
| `curso_ofertado_id` | FK a la sección del curso. | `3000` |
| `dia_semana` | Día de la semana (1=Lunes, 7=Domingo). | `1` |
| `hora_inicio` | Hora de inicio de la sesión. | `"08:00:00"` |
| `hora_fin` | Hora de fin de la sesión. | `"10:00:00"` |
| `localizacion_id` | FK al aula o espacio donde se dicta. | `101` |
| `tipo_sesion` | Tipo de sesión académica. | `"TEORIA"` |
| `observaciones` | Observaciones del horario. | `"Clase con laboratorio anexo"` |
| `created_at` | Fecha de creación del registro. | `"2024-02-20 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-02-20 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 31. MATRICULA
**Descripción:** Registro oficial que vincula a un estudiante con una sección de curso.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la matrícula. | `9000` |
| `estudiante_id` | FK al estudiante matriculado. | `200` |
| `curso_ofertado_id` | FK a la sección elegida. | `3000` |
| `fecha_matricula` | Fecha y hora del registro de matrícula. | `"2024-03-10 10:30:00"` |
| `tipo_matricula` | Tipo de matrícula. | `"REGULAR"` |
| `creditos_matriculados` | Créditos que representa este curso. | `4` |
| `estado_matricula` | Estado actual de la matrícula. | `"MATRICULADO"` |
| `fecha_retiro` | Fecha de retiro (si aplica). | `null` |
| `created_at` | Fecha de creación del registro. | `"2024-03-10 10:30:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-10 10:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

## 📊 MÓDULO 6: ASSESSMENT (Evaluación Académica)

### 32. EVALUACION_CRITERIO
**Descripción:** Definición de los criterios y pesos de evaluación para cada sección de curso.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del criterio. | `60` |
| `curso_ofertado_id` | FK a la sección del curso. | `3000` |
| `nombre` | Nombre de la evaluación. | `"Examen Parcial"` |
| `descripcion` | Descripción de la evaluación. | `"Evaluación de las unidades 1 y 2"` |
| `peso` | Porcentaje de peso en la nota final. | `30` |
| `tipo_evaluacion` | Tipo de evaluación. | `"EXAMEN"` |
| `nota_maxima` | Puntaje máximo posible. | `20` |
| `nota_minima_aprobatoria` | Nota mínima para aprobar. | `11` |
| `orden` | Orden de presentación. | `1` |
| `es_recuperable` | Indica si se puede recuperar. | `true` |
| `estado` | Estado del criterio. | `"ACTIVO"` |
| `created_at` | Fecha de creación del registro. | `"2024-03-15 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-15 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 33. EVALUACION_NOTA
**Descripción:** Registro de calificaciones obtenidas por el estudiante en cada criterio.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la nota. | `12000` |
| `matricula_id` | FK a la matrícula del alumno. | `9000` |
| `criterio_id` | FK al criterio de evaluación. | `60` |
| `nota` | Nota obtenida en la evaluación. | `15.5` |
| `nota_recuperacion` | Nota de recuperación (si aplica). | `null` |
| `nota_final` | Nota final procesada. | `16` |
| `observacion` | Observaciones sobre la calificación. | `"Buen desempeño"` |
| `fecha_evaluacion` | Fecha en que se tomó la evaluación. | `"2024-05-10 10:00:00"` |
| `fecha_calificacion` | Fecha en que se registró la nota. | `"2024-05-12 14:00:00"` |
| `estado` | Estado de la calificación. | `"CALIFICADO"` |
| `created_at` | Fecha de creación del registro. | `"2024-05-12 14:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-05-12 14:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 34. ASISTENCIA_ALUMNO
**Descripción:** Control de presencia del estudiante en las sesiones de clase programadas.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del registro. | `50000` |
| `estudiante_id` | FK al estudiante. | `200` |
| `horario_id` | FK al horario de la sesión. | `4000` |
| `fecha_clase` | Fecha específica de la sesión. | `"2024-03-18"` |
| `estado` | Estado de asistencia. | `"PRESENTE"` |
| `observaciones` | Observaciones sobre la asistencia. | `null` |
| `minutos_tardanza` | Minutos de retraso (si aplica). | `0` |
| `created_at` | Fecha de creación del registro. | `"2024-03-18 08:05:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-18 08:05:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

## 💰 MÓDULO 7: FINANCE (Gestión Financiera)

### 35. CUENTA_CORRIENTE_ALUMNO
**Descripción:** Registro de obligaciones financieras (deudas) generadas al estudiante.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único de la deuda. | `700` |
| `estudiante_id` | FK al estudiante deudor. | `200` |
| `monto` | Monto total original de la deuda. | `500.00` |
| `monto_pagado` | Monto ya abonado. | `0.00` |
| `monto_pendiente` | Saldo pendiente de pago. | `500.00` |
| `concepto` | Descripción del cargo. | `"Matrícula Semestre 2024-1"` |
| `tipo_cargo` | Tipo de cargo financiero. | `"MATRICULA"` |
| `fecha_vencimiento` | Fecha límite de pago. | `"2024-03-31"` |
| `fecha_emision` | Fecha de emisión del cargo. | `"2024-03-01"` |
| `estado` | Estado de la deuda. | `"PENDIENTE"` |
| `periodo_academico` | Periodo al que corresponde. | `"2024-1"` |
| `numero_cuota` | Número de cuota (si es fraccionado). | `1` |
| `observaciones` | Observaciones adicionales. | `null` |
| `created_at` | Fecha de creación del registro. | `"2024-03-01 00:00:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-01 00:00:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 36. PAGO
**Descripción:** Registro de ingresos monetarios (recibos de pago) realizados por el estudiante.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del pago. | `800` |
| `estudiante_id` | FK al estudiante que paga. | `200` |
| `numero_recibo` | Código del comprobante de pago. | `"REC-001-000999"` |
| `monto_pagado` | Cantidad total de dinero ingresado. | `500.00` |
| `monto_aplicado` | Monto aplicado a deudas. | `500.00` |
| `monto_pendiente_aplicar` | Monto a favor sin aplicar. | `0.00` |
| `fecha_pago` | Fecha y hora de la transacción. | `"2024-03-30 11:30:00"` |
| `metodo_pago` | Forma de pago utilizada. | `"TARJETA_CREDITO"` |
| `referencia_pago` | Número de referencia de la transacción. | `"TXN-123456789"` |
| `banco` | Banco donde se realizó el pago. | `"BCP"` |
| `cajero` | Identificación del cajero (si aplica). | `"CAJA-01"` |
| `estado` | Estado del pago. | `"PROCESADO"` |
| `observaciones` | Observaciones del pago. | `"Pago completo de matrícula"` |
| `fecha_anulacion` | Fecha de anulación (si aplica). | `null` |
| `motivo_anulacion` | Motivo de anulación (si aplica). | `null` |
| `created_at` | Fecha de creación del registro. | `"2024-03-30 11:30:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-30 11:30:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

### 37. PAGO_DETALLE_DEUDA
**Descripción:** Tabla de conciliación que vincula un pago con las deudas específicas que cancela.

| Campo | Descripción | Ejemplo |
| :--- | :--- | :--- |
| `id` | Identificador único del detalle. | `900` |
| `pago_id` | FK al pago realizado. | `800` |
| `deuda_id` | FK a la cuenta corriente afectada. | `700` |
| `monto_aplicado` | Parte del pago que cubre esta deuda. | `500.00` |
| `fecha_aplicacion` | Fecha en que se procesó el cruce. | `"2024-03-30 11:35:00"` |
| `aplicado_por` | Usuario que realizó la aplicación. | `"cajero@upeu.edu.pe"` |
| `observaciones` | Observaciones de la aplicación. | `"Aplicación automática"` |
| `estado` | Estado del detalle. | `"APLICADO"` |
| `fecha_reversion` | Fecha de reversión (si aplica). | `null` |
| `motivo_reversion` | Motivo de reversión (si aplica). | `null` |
| `created_at` | Fecha de creación del registro. | `"2024-03-30 11:35:00"` |
| `updated_at` | Fecha de última modificación. | `"2024-03-30 11:35:00"` |
| `active` | Indica si el registro está activo. | `true` |

---

## 📊 Resumen del Sistema

| Módulo | Cantidad de Tablas | Descripción |
| :--- | :---: | :--- |
| 🏢 CORE | 6 | Configuración, personas y estructura organizacional |
| 🔐 SECURITY | 6 | Autenticación, roles y permisos |
| 👥 PEOPLE | 5 | Estudiantes, profesores, empleados y autoridades |
| 📚 CURRICULUM | 9 | Programas, planes, cursos y sílabos |
| 🎓 ENROLLMENT | 5 | Periodos, oferta, horarios y matrículas |
| 📊 ASSESSMENT | 3 | Criterios, notas y asistencia |
| 💰 FINANCE | 3 | Deudas, pagos y conciliación |
| **TOTAL** | **37** | **Tablas del sistema** |

---

## 🔑 Convenciones Utilizadas

| Convención | Descripción |
| :--- | :--- |
| `id` | Llave primaria autoincremental (BigInt) |
| `*_id` | Llave foránea a otra tabla |
| `created_at` | Timestamp de creación del registro |
| `updated_at` | Timestamp de última modificación |
| `active` | Soft delete (false = registro eliminado lógicamente) |
| `estado` | Estado de negocio del registro (varía por tabla) |

---

> **Versión:** 1.0  
> **Última actualización:** Diciembre 2024  
> **Sistema:** Sistema de Gestión Académica Universitaria - Single Tenant