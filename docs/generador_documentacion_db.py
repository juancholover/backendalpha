"""
Generador de Documentación de Base de Datos
=============================================

Este script analiza las entidades JPA del proyecto y genera:
1. Diagrama ERD en formato Mermaid
2. Documentación detallada de cada tabla
3. Relaciones entre entidades
4. Campos de ejemplo y reglas de negocio
5. Exportación a Markdown y JSON

Autor: Sistema de Gestión Universitaria
Fecha: 2025
"""

import os
import re
import json
from pathlib import Path
from dataclasses import dataclass, field, asdict
from typing import List, Dict, Optional, Set
from datetime import datetime


@dataclass
class Campo:
    """Representa un campo de una tabla."""
    nombre: str
    tipo_java: str
    tipo_sql: str
    nullable: bool = True
    unique: bool = False
    length: Optional[int] = None
    precision: Optional[int] = None
    scale: Optional[int] = None
    column_name: str = ""
    descripcion: str = ""
    ejemplo: str = ""
    valores_posibles: List[str] = field(default_factory=list)
    
    def __post_init__(self):
        if not self.column_name:
            self.column_name = self._convertir_a_snake_case(self.nombre)
    
    @staticmethod
    def _convertir_a_snake_case(nombre: str) -> str:
        """Convierte camelCase a snake_case."""
        return re.sub(r'(?<!^)(?=[A-Z])', '_', nombre).lower()


@dataclass
class Relacion:
    """Representa una relación entre tablas."""
    tipo: str  # ManyToOne, OneToMany, ManyToMany, OneToOne
    entidad_destino: str
    campo: str
    cascade: List[str] = field(default_factory=list)
    fetch_type: str = "LAZY"
    mapped_by: Optional[str] = None
    join_column: Optional[str] = None
    descripcion: str = ""


@dataclass
class Constraint:
    """Representa una restricción de tabla."""
    tipo: str  # UNIQUE, CHECK, INDEX
    campos: List[str]
    nombre: str = ""


@dataclass
class Tabla:
    """Representa una tabla de la base de datos."""
    nombre: str
    clase: str
    modulo: str  # academic, security, finance, catalog
    descripcion: str = ""
    campos: List[Campo] = field(default_factory=list)
    relaciones: List[Relacion] = field(default_factory=list)
    constraints: List[Constraint] = field(default_factory=list)
    indices: List[Dict[str, str]] = field(default_factory=list)
    hereda_de: Optional[str] = None
    funcionalidad: str = ""
    reglas_negocio: List[str] = field(default_factory=list)
    ejemplo_datos: Dict[str, str] = field(default_factory=dict)


class AnalizadorEntidades:
    """Analiza archivos Java de entidades JPA."""
    
    # Mapeo de tipos Java a SQL
    TIPO_JAVA_A_SQL = {
        'String': 'VARCHAR',
        'Long': 'BIGINT',
        'Integer': 'INTEGER',
        'int': 'INTEGER',
        'long': 'BIGINT',
        'Boolean': 'BOOLEAN',
        'boolean': 'BOOLEAN',
        'LocalDate': 'DATE',
        'LocalDateTime': 'TIMESTAMP',
        'LocalTime': 'TIME',
        'BigDecimal': 'DECIMAL',
        'Double': 'DOUBLE',
        'double': 'DOUBLE',
        'Float': 'REAL',
        'float': 'REAL',
        'byte[]': 'BYTEA',
        'Byte[]': 'BYTEA',
    }
    
    # Ejemplos de datos por campo común
    EJEMPLOS_CAMPOS = {
        'nombre': 'Universidad Nacional del Altiplano',
        'codigo': 'UNA001',
        'descripcion': 'Descripción detallada del registro',
        'email': 'contacto@universidad.edu.pe',
        'telefono': '051-363456',
        'celular': '987654321',
        'direccion': 'Av. Ejercito 329, Puno',
        'fecha_nacimiento': '1995-06-15',
        'fecha_ingreso': '2024-03-01',
        'nombres': 'Juan Carlos',
        'apellido_paterno': 'Pérez',
        'apellido_materno': 'García',
        'numero_documento': '72345678',
        'ruc': '20345678901',
        'dominio': 'unap.edu.pe',
        'estado': 'ACTIVO',
        'activo': 'true',
        'usuario': 'jperez',
        'password': '$2a$10$encrypted...',
        'creditos': '4',
        'nota': '16.5',
        'promedio': '15.8',
        'ciclo': '5',
        'semestre': '2024-I',
        'codigo_estudiante': '2024001234',
        'codigo_empleado': 'EMP001',
    }
    
    # Funcionalidades por módulo
    FUNCIONALIDADES = {
        'Universidad': 'Gestiona información de universidades/instituciones. Central para multitenancy.',
        'Persona': 'Registro centralizado de personas (estudiantes, profesores, empleados).',
        'Estudiante': 'Gestiona datos académicos de estudiantes matriculados.',
        'Profesor': 'Información de docentes y sus datos académicos.',
        'Empleado': 'Personal administrativo y de servicios.',
        'ProgramaAcademico': 'Carreras profesionales ofrecidas por la universidad.',
        'PlanAcademico': 'Planes de estudio por programa y versión.',
        'Curso': 'Asignaturas del catálogo académico.',
        'CursoOfertado': 'Cursos programados para un periodo académico.',
        'Matricula': 'Inscripción de estudiantes en cursos.',
        'PeriodoAcademico': 'Semestres/ciclos académicos.',
        'Horario': 'Programación de clases (día, hora, aula).',
        'EvaluacionCriterio': 'Criterios de evaluación (exámenes, prácticas).',
        'EvaluacionNota': 'Calificaciones de estudiantes.',
        'AsistenciaAlumno': 'Control de asistencia a clases.',
        'UnidadOrganizativa': 'Estructura organizacional (facultades, escuelas).',
        'Localizacion': 'Espacios físicos (aulas, laboratorios).',
        'Autoridad': 'Cargos directivos (rector, decano, director).',
        'TipoAutoridad': 'Catálogo de tipos de autoridades.',
        'TipoUnidad': 'Catálogo de tipos de unidades organizativas.',
        'TipoLocalizacion': 'Catálogo de tipos de localizaciones.',
        'RequisitoCurso': 'Prerequisitos entre cursos.',
        'PlanCurso': 'Cursos que pertenecen a un plan académico.',
        'AuthUsuario': 'Usuarios del sistema (autenticación).',
        'Rol': 'Roles de acceso (ADMIN, DOCENTE, ESTUDIANTE).',
        'Permiso': 'Permisos granulares del sistema.',
        'RolPermiso': 'Asignación de permisos a roles.',
        'RefreshToken': 'Tokens de refresco JWT.',
        'Pago': 'Registro de pagos realizados.',
        'CuentaCorrienteAlumno': 'Estado de cuenta de estudiantes.',
        'PagoDetalleDeuda': 'Detalle de aplicación de pagos a deudas.',
        'Category': 'Categorías del catálogo general.',
    }
    
    def __init__(self, directorio_base: str):
        self.directorio_base = Path(directorio_base)
        self.tablas: Dict[str, Tabla] = {}
        
    def analizar_proyecto(self):
        """Analiza todas las entidades del proyecto."""
        print("🔍 Analizando entidades JPA...")
        
        # Buscar archivos de entidades
        patrones = [
            'src/main/java/**/domain/entities/**/*.java',
            'src/main/java/**/entities/**/*.java',
        ]
        
        archivos_encontrados = []
        for patron in patrones:
            archivos_encontrados.extend(self.directorio_base.glob(patron))
        
        print(f"✅ Encontrados {len(archivos_encontrados)} archivos de entidades")
        
        for archivo in archivos_encontrados:
            try:
                self._analizar_entidad(archivo)
            except Exception as e:
                print(f"⚠️  Error analizando {archivo.name}: {e}")
        
        print(f"✅ Total de tablas analizadas: {len(self.tablas)}")
        return self.tablas
    
    def _analizar_entidad(self, archivo: Path):
        """Analiza un archivo de entidad Java."""
        contenido = archivo.read_text(encoding='utf-8')
        
        # Verificar si es una entidad JPA
        if '@Entity' not in contenido:
            return
        
        # Extraer nombre de clase
        match_clase = re.search(r'public class (\w+)', contenido)
        if not match_clase:
            return
        
        nombre_clase = match_clase.group(1)
        
        # Determinar módulo
        modulo = self._determinar_modulo(archivo)
        
        # Extraer nombre de tabla
        match_tabla = re.search(r'@Table\s*\(\s*name\s*=\s*"([^"]+)"', contenido)
        nombre_tabla = match_tabla.group(1) if match_tabla else self._clase_a_tabla(nombre_clase)
        
        # Crear tabla
        tabla = Tabla(
            nombre=nombre_tabla,
            clase=nombre_clase,
            modulo=modulo,
            funcionalidad=self.FUNCIONALIDADES.get(nombre_clase, ''),
        )
        
        # Analizar herencia
        match_extends = re.search(r'extends\s+(\w+)', contenido)
        if match_extends:
            tabla.hereda_de = match_extends.group(1)
        
        # Analizar campos
        self._extraer_campos(contenido, tabla)
        
        # Analizar relaciones
        self._extraer_relaciones(contenido, tabla)
        
        # Analizar constraints
        self._extraer_constraints(contenido, tabla)
        
        # Generar reglas de negocio
        self._extraer_reglas_negocio(contenido, tabla)
        
        # Generar datos de ejemplo
        self._generar_datos_ejemplo(tabla)
        
        self.tablas[nombre_clase] = tabla
    
    def _determinar_modulo(self, archivo: Path) -> str:
        """Determina el módulo basado en la ruta del archivo."""
        partes = archivo.parts
        if 'academic' in partes:
            return 'academic'
        elif 'security' in partes:
            return 'security'
        elif 'finance' in partes:
            return 'finance'
        elif 'catalog' in partes:
            return 'catalog'
        return 'shared'
    
    @staticmethod
    def _clase_a_tabla(nombre_clase: str) -> str:
        """Convierte nombre de clase a nombre de tabla."""
        return re.sub(r'(?<!^)(?=[A-Z])', '_', nombre_clase).lower()
    
    def _extraer_campos(self, contenido: str, tabla: Tabla):
        """Extrae campos de la entidad."""
        # Patrón para campos con anotaciones
        patron_campo = r'@Column[^;]*?private\s+(\w+(?:<[^>]+>)?)\s+(\w+);'
        
        for match in re.finditer(patron_campo, contenido, re.DOTALL):
            tipo_java = match.group(1).strip()
            nombre_campo = match.group(2).strip()
            
            # Extraer metadatos de @Column
            anotacion_column = match.group(0)
            
            campo = Campo(
                nombre=nombre_campo,
                tipo_java=tipo_java,
                tipo_sql=self._tipo_java_a_sql(tipo_java),
            )
            
            # Nullable
            if 'nullable = false' in anotacion_column or 'nullable=false' in anotacion_column:
                campo.nullable = False
            
            # Unique
            if 'unique = true' in anotacion_column or 'unique=true' in anotacion_column:
                campo.unique = True
            
            # Length
            match_length = re.search(r'length\s*=\s*(\d+)', anotacion_column)
            if match_length:
                campo.length = int(match_length.group(1))
            
            # Precision y Scale
            match_precision = re.search(r'precision\s*=\s*(\d+)', anotacion_column)
            if match_precision:
                campo.precision = int(match_precision.group(1))
            
            match_scale = re.search(r'scale\s*=\s*(\d+)', anotacion_column)
            if match_scale:
                campo.scale = int(match_scale.group(1))
            
            # Column name
            match_name = re.search(r'name\s*=\s*"([^"]+)"', anotacion_column)
            if match_name:
                campo.column_name = match_name.group(1)
            
            # Generar ejemplo
            campo.ejemplo = self._generar_ejemplo_campo(campo.column_name or nombre_campo, tipo_java)
            
            tabla.campos.append(campo)
    
    def _extraer_relaciones(self, contenido: str, tabla: Tabla):
        """Extrae relaciones de la entidad."""
        # Patrones para relaciones
        patrones = [
            (r'@ManyToOne[^;]*?private\s+(\w+)\s+(\w+);', 'ManyToOne'),
            (r'@OneToMany[^;]*?private\s+\w+<(\w+)>\s+(\w+);', 'OneToMany'),
            (r'@ManyToMany[^;]*?private\s+\w+<(\w+)>\s+(\w+);', 'ManyToMany'),
            (r'@OneToOne[^;]*?private\s+(\w+)\s+(\w+);', 'OneToOne'),
        ]
        
        for patron, tipo_relacion in patrones:
            for match in re.finditer(patron, contenido, re.DOTALL):
                entidad_destino = match.group(1).strip()
                campo = match.group(2).strip()
                
                relacion = Relacion(
                    tipo=tipo_relacion,
                    entidad_destino=entidad_destino,
                    campo=campo,
                )
                
                # Extraer metadatos
                anotacion = match.group(0)
                
                # Fetch type
                if 'fetch = FetchType.EAGER' in anotacion or 'fetch=FetchType.EAGER' in anotacion:
                    relacion.fetch_type = 'EAGER'
                
                # MappedBy
                match_mapped = re.search(r'mappedBy\s*=\s*"([^"]+)"', anotacion)
                if match_mapped:
                    relacion.mapped_by = match_mapped.group(1)
                
                # JoinColumn
                match_join = re.search(r'@JoinColumn[^)]*name\s*=\s*"([^"]+)"', anotacion)
                if match_join:
                    relacion.join_column = match_join.group(1)
                
                tabla.relaciones.append(relacion)
    
    def _extraer_constraints(self, contenido: str, tabla: Tabla):
        """Extrae constraints de la tabla."""
        # UniqueConstraints
        patron_unique = r'@UniqueConstraint\s*\(\s*columnNames\s*=\s*\{([^}]+)\}'
        
        for match in re.finditer(patron_unique, contenido):
            columnas_str = match.group(1)
            columnas = [c.strip().strip('"') for c in columnas_str.split(',')]
            
            constraint = Constraint(
                tipo='UNIQUE',
                campos=columnas,
            )
            tabla.constraints.append(constraint)
        
        # Indices
        patron_index = r'@Index\s*\([^)]*name\s*=\s*"([^"]+)"[^)]*columnList\s*=\s*"([^"]+)"'
        
        for match in re.finditer(patron_index, contenido):
            nombre = match.group(1)
            columnas = match.group(2).split(',')
            
            tabla.indices.append({
                'nombre': nombre,
                'columnas': [c.strip() for c in columnas]
            })
    
    def _extraer_reglas_negocio(self, contenido: str, tabla: Tabla):
        """Extrae reglas de negocio de comentarios."""
        # Buscar comentarios que describan reglas
        patron_comentario = r'//\s*(.+)'
        
        reglas = set()
        for match in re.finditer(patron_comentario, contenido):
            texto = match.group(1).strip()
            if any(keyword in texto.lower() for keyword in ['validar', 'debe', 'no puede', 'solo', 'regla']):
                reglas.add(texto)
        
        tabla.reglas_negocio = list(reglas)[:5]  # Limitar a 5 reglas
    
    def _generar_datos_ejemplo(self, tabla: Tabla):
        """Genera datos de ejemplo para la tabla."""
        for campo in tabla.campos[:10]:  # Primeros 10 campos
            tabla.ejemplo_datos[campo.column_name] = campo.ejemplo
    
    def _tipo_java_a_sql(self, tipo_java: str) -> str:
        """Convierte tipo Java a tipo SQL."""
        tipo_base = tipo_java.split('<')[0]  # Manejar genéricos
        return self.TIPO_JAVA_A_SQL.get(tipo_base, 'VARCHAR')
    
    def _generar_ejemplo_campo(self, nombre_campo: str, tipo_java: str) -> str:
        """Genera un ejemplo para un campo."""
        # Buscar en diccionario de ejemplos
        for clave, valor in self.EJEMPLOS_CAMPOS.items():
            if clave in nombre_campo.lower():
                return valor
        
        # Ejemplos por tipo
        if tipo_java == 'String':
            return 'Texto de ejemplo'
        elif tipo_java in ['Long', 'Integer', 'int', 'long']:
            return '1'
        elif tipo_java in ['Boolean', 'boolean']:
            return 'true'
        elif tipo_java in ['LocalDate', 'LocalDateTime']:
            return '2024-01-15'
        elif tipo_java == 'BigDecimal':
            return '0.00'
        else:
            return 'valor'


class GeneradorDocumentacion:
    """Genera documentación de la base de datos."""
    
    def __init__(self, tablas: Dict[str, Tabla]):
        self.tablas = tablas
    
    def generar_markdown(self, archivo_salida: str):
        """Genera documentación en Markdown."""
        print(f"📝 Generando documentación Markdown en {archivo_salida}...")
        
        with open(archivo_salida, 'w', encoding='utf-8') as f:
            f.write(self._generar_encabezado())
            f.write(self._generar_indice())
            f.write(self._generar_diagrama_erd())
            f.write(self._generar_resumen_modulos())
            f.write(self._generar_detalle_tablas())
            f.write(self._generar_diccionario_datos())
        
        print("✅ Documentación Markdown generada")
    
    def generar_json(self, archivo_salida: str):
        """Genera documentación en JSON."""
        print(f"📝 Generando documentación JSON en {archivo_salida}...")
        
        datos = {
            'metadata': {
                'generado': datetime.now().isoformat(),
                'total_tablas': len(self.tablas),
            },
            'tablas': {
                nombre: {
                    'nombre_tabla': tabla.nombre,
                    'clase': tabla.clase,
                    'modulo': tabla.modulo,
                    'descripcion': tabla.descripcion,
                    'funcionalidad': tabla.funcionalidad,
                    'hereda_de': tabla.hereda_de,
                    'campos': [asdict(c) for c in tabla.campos],
                    'relaciones': [asdict(r) for r in tabla.relaciones],
                    'constraints': [asdict(c) for c in tabla.constraints],
                    'indices': tabla.indices,
                    'reglas_negocio': tabla.reglas_negocio,
                    'ejemplo_datos': tabla.ejemplo_datos,
                }
                for nombre, tabla in self.tablas.items()
            }
        }
        
        with open(archivo_salida, 'w', encoding='utf-8') as f:
            json.dump(datos, f, indent=2, ensure_ascii=False)
        
        print("✅ Documentación JSON generada")
    
    def _generar_encabezado(self) -> str:
        return f"""# 📊 Documentación de Base de Datos
## Sistema de Gestión Universitaria - SaaS Multitenancy

**Fecha de generación:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}  
**Total de tablas:** {len(self.tablas)}  
**Framework:** Quarkus 3.x + Hibernate Panache  
**Base de datos:** PostgreSQL 14+

---

"""
    
    def _generar_indice(self) -> str:
        doc = "## 📑 Índice de Contenidos\n\n"
        
        # Agrupar por módulo
        modulos = {}
        for tabla in self.tablas.values():
            if tabla.modulo not in modulos:
                modulos[tabla.modulo] = []
            modulos[tabla.modulo].append(tabla)
        
        for modulo, tablas in sorted(modulos.items()):
            doc += f"### Módulo: {modulo.upper()}\n"
            for tabla in sorted(tablas, key=lambda t: t.nombre):
                doc += f"- [{tabla.nombre}](#{tabla.nombre.replace('_', '-')}) - {tabla.funcionalidad[:60]}...\n"
            doc += "\n"
        
        doc += "---\n\n"
        return doc
    
    def _generar_diagrama_erd(self) -> str:
        doc = "## 🗺️ Diagrama Entidad-Relación (ERD)\n\n"
        doc += "```mermaid\nerDiagram\n"
        
        # Generar entidades
        for tabla in self.tablas.values():
            # Nombre de tabla
            doc += f"    {tabla.nombre} {{\n"
            
            # Campos principales (primeros 8)
            for campo in tabla.campos[:8]:
                tipo_display = campo.tipo_sql
                if campo.length:
                    tipo_display += f"({campo.length})"
                elif campo.precision:
                    tipo_display += f"({campo.precision},{campo.scale or 0})"
                
                atributos = []
                if not campo.nullable:
                    atributos.append("NOT NULL")
                if campo.unique:
                    atributos.append("UNIQUE")
                
                atributos_str = " " + " ".join(atributos) if atributos else ""
                doc += f"        {tipo_display} {campo.column_name}{atributos_str}\n"
            
            if len(tabla.campos) > 8:
                doc += f"        ... {len(tabla.campos) - 8} campos más\n"
            
            doc += "    }\n\n"
        
        # Generar relaciones
        for tabla in self.tablas.values():
            for relacion in tabla.relaciones:
                if relacion.tipo == 'ManyToOne':
                    simbolo = "}o--||"
                elif relacion.tipo == 'OneToMany':
                    simbolo = "||--o{"
                elif relacion.tipo == 'ManyToMany':
                    simbolo = "}o--o{"
                elif relacion.tipo == 'OneToOne':
                    simbolo = "||--||"
                else:
                    simbolo = "--"
                
                entidad_destino_tabla = self._clase_a_tabla(relacion.entidad_destino)
                doc += f"    {tabla.nombre} {simbolo} {entidad_destino_tabla} : \"{relacion.campo}\"\n"
        
        doc += "```\n\n---\n\n"
        return doc
    
    def _generar_resumen_modulos(self) -> str:
        doc = "## 📦 Resumen por Módulos\n\n"
        
        # Agrupar por módulo
        modulos = {}
        for tabla in self.tablas.values():
            if tabla.modulo not in modulos:
                modulos[tabla.modulo] = []
            modulos[tabla.modulo].append(tabla)
        
        descripciones_modulos = {
            'academic': '🎓 **Módulo Académico**: Gestión de estudiantes, profesores, cursos, matrículas y evaluaciones.',
            'security': '🔐 **Módulo de Seguridad**: Autenticación, autorización, usuarios y permisos.',
            'finance': '💰 **Módulo Financiero**: Pagos, cuentas corrientes y gestión económica.',
            'catalog': '📚 **Módulo de Catálogos**: Tipos y categorías para uso transversal.',
            'shared': '🔧 **Módulo Compartido**: Entidades base y utilidades comunes.',
        }
        
        for modulo, tablas in sorted(modulos.items()):
            doc += f"### {descripciones_modulos.get(modulo, modulo.upper())}\n\n"
            doc += f"**Total de tablas:** {len(tablas)}\n\n"
            
            doc += "| Tabla | Descripción | Campos | Relaciones |\n"
            doc += "|-------|-------------|--------|------------|\n"
            
            for tabla in sorted(tablas, key=lambda t: t.nombre):
                doc += f"| `{tabla.nombre}` | {tabla.funcionalidad[:50]} | {len(tabla.campos)} | {len(tabla.relaciones)} |\n"
            
            doc += "\n"
        
        doc += "---\n\n"
        return doc
    
    def _generar_detalle_tablas(self) -> str:
        doc = "## 📋 Detalle de Tablas\n\n"
        
        for tabla in sorted(self.tablas.values(), key=lambda t: (t.modulo, t.nombre)):
            doc += f"### {tabla.nombre}\n\n"
            doc += f"**Clase Java:** `{tabla.clase}`  \n"
            doc += f"**Módulo:** `{tabla.modulo}`  \n"
            
            if tabla.hereda_de:
                doc += f"**Hereda de:** `{tabla.hereda_de}`  \n"
            
            doc += f"\n**Funcionalidad:**  \n{tabla.funcionalidad}\n\n"
            
            # Campos
            doc += "#### Campos\n\n"
            doc += "| Campo | Tipo SQL | Tipo Java | Null | Único | Ejemplo |\n"
            doc += "|-------|----------|-----------|------|-------|----------|\n"
            
            for campo in tabla.campos:
                tipo_sql = campo.tipo_sql
                if campo.length:
                    tipo_sql += f"({campo.length})"
                elif campo.precision:
                    tipo_sql += f"({campo.precision},{campo.scale or 0})"
                
                nullable = "✅" if campo.nullable else "❌"
                unique = "✅" if campo.unique else ""
                
                doc += f"| `{campo.column_name}` | {tipo_sql} | {campo.tipo_java} | {nullable} | {unique} | {campo.ejemplo} |\n"
            
            doc += "\n"
            
            # Relaciones
            if tabla.relaciones:
                doc += "#### Relaciones\n\n"
                doc += "| Tipo | Entidad Destino | Campo | Descripción |\n"
                doc += "|------|-----------------|-------|-------------|\n"
                
                for rel in tabla.relaciones:
                    doc += f"| {rel.tipo} | `{rel.entidad_destino}` | `{rel.campo}` | "
                    if rel.mapped_by:
                        doc += f"Mapeado por `{rel.mapped_by}` "
                    if rel.join_column:
                        doc += f"Join: `{rel.join_column}`"
                    doc += "|\n"
                
                doc += "\n"
            
            # Constraints
            if tabla.constraints:
                doc += "#### Restricciones\n\n"
                for constraint in tabla.constraints:
                    campos_str = ", ".join([f"`{c}`" for c in constraint.campos])
                    doc += f"- **{constraint.tipo}**: {campos_str}\n"
                doc += "\n"
            
            # Índices
            if tabla.indices:
                doc += "#### Índices\n\n"
                for indice in tabla.indices:
                    columnas_str = ", ".join([f"`{c}`" for c in indice['columnas']])
                    doc += f"- **{indice['nombre']}**: {columnas_str}\n"
                doc += "\n"
            
            # Reglas de negocio
            if tabla.reglas_negocio:
                doc += "#### Reglas de Negocio\n\n"
                for regla in tabla.reglas_negocio:
                    doc += f"- {regla}\n"
                doc += "\n"
            
            # Ejemplo de datos
            if tabla.ejemplo_datos:
                doc += "#### Ejemplo de Registro\n\n"
                doc += "```json\n"
                doc += json.dumps(tabla.ejemplo_datos, indent=2, ensure_ascii=False)
                doc += "\n```\n\n"
            
            doc += "---\n\n"
        
        return doc
    
    def _generar_diccionario_datos(self) -> str:
        doc = "## 📖 Diccionario de Datos\n\n"
        doc += "### Campos Comunes en Todas las Tablas (AuditableEntity)\n\n"
        doc += "| Campo | Tipo | Descripción |\n"
        doc += "|-------|------|-------------|\n"
        doc += "| `id` | BIGINT | Identificador único (PK) |\n"
        doc += "| `created_at` | TIMESTAMP | Fecha de creación |\n"
        doc += "| `updated_at` | TIMESTAMP | Fecha de última modificación |\n"
        doc += "| `created_by` | VARCHAR(100) | Usuario que creó el registro |\n"
        doc += "| `updated_by` | VARCHAR(100) | Usuario que modificó el registro |\n"
        doc += "| `active` | BOOLEAN | Estado lógico (borrado lógico) |\n"
        doc += "| `universidad_id` | BIGINT | FK Universidad (multitenancy) |\n"
        doc += "\n"
        
        doc += "### Convenciones de Nombrado\n\n"
        doc += "- **Tablas**: `snake_case` (ej: `programa_academico`)\n"
        doc += "- **Columnas**: `snake_case` (ej: `fecha_nacimiento`)\n"
        doc += "- **Foreign Keys**: `<tabla>_id` (ej: `persona_id`)\n"
        doc += "- **Índices**: `idx_<tabla>_<campo>` (ej: `idx_estudiante_codigo`)\n"
        doc += "- **Constraints**: `uk_<tabla>_<campos>` (ej: `uk_persona_documento_universidad`)\n"
        doc += "\n"
        
        doc += "### Estados Comunes\n\n"
        doc += "- **ACTIVO**: Registro en uso normal\n"
        doc += "- **INACTIVO**: Temporalmente deshabilitado\n"
        doc += "- **SUSPENDIDO**: Bloqueado por incumplimiento\n"
        doc += "- **EGRESADO**: Completó el programa (estudiantes)\n"
        doc += "- **GRADUADO**: Obtuvo el título (estudiantes)\n"
        doc += "- **RETIRADO**: Dio de baja voluntaria\n"
        doc += "- **PENDIENTE**: En proceso de aprobación\n"
        doc += "- **APROBADO**: Validado y autorizado\n"
        doc += "- **RECHAZADO**: No cumple requisitos\n"
        doc += "\n"
        
        return doc
    
    @staticmethod
    def _clase_a_tabla(nombre_clase: str) -> str:
        """Convierte nombre de clase a nombre de tabla."""
        return re.sub(r'(?<!^)(?=[A-Z])', '_', nombre_clase).lower()


def main():
    """Función principal."""
    print("=" * 80)
    print("🎓 GENERADOR DE DOCUMENTACIÓN DE BASE DE DATOS")
    print("   Sistema de Gestión Universitaria - SaaS Multitenancy")
    print("=" * 80)
    print()
    
    # Directorio del proyecto
    directorio_proyecto = Path(__file__).parent.parent
    print(f"📂 Directorio del proyecto: {directorio_proyecto}")
    print()
    
    # Analizar entidades
    analizador = AnalizadorEntidades(directorio_proyecto)
    tablas = analizador.analizar_proyecto()
    
    if not tablas:
        print("❌ No se encontraron entidades para analizar")
        return
    
    print()
    
    # Generar documentación
    generador = GeneradorDocumentacion(tablas)
    
    # Crear directorio de salida
    directorio_docs = directorio_proyecto / 'docs'
    directorio_docs.mkdir(exist_ok=True)
    
    # Generar Markdown
    archivo_md = directorio_docs / 'DICCIONARIO_BASE_DATOS.md'
    generador.generar_markdown(str(archivo_md))
    
    # Generar JSON
    archivo_json = directorio_docs / 'base_datos.json'
    generador.generar_json(str(archivo_json))
    
    print()
    print("=" * 80)
    print("✅ DOCUMENTACIÓN GENERADA EXITOSAMENTE")
    print("=" * 80)
    print(f"📄 Markdown: {archivo_md}")
    print(f"📄 JSON: {archivo_json}")
    print()
    print("🎉 ¡Proceso completado!")


if __name__ == '__main__':
    main()
