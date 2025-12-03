"""
Generador de Scripts SQL DDL
=============================

Genera scripts SQL CREATE TABLE a partir de la documentación JSON.
Útil para recrear la estructura de la base de datos.
"""

import json
from pathlib import Path
from typing import Dict, List


class GeneradorSQL:
    """Generador de scripts SQL DDL."""
    
    def __init__(self, archivo_json: str):
        self.archivo = Path(archivo_json)
        self.datos = self._cargar_datos()
    
    def _cargar_datos(self) -> Dict:
        """Carga los datos desde el JSON."""
        with open(self.archivo, encoding='utf-8') as f:
            return json.load(f)
    
    def generar_ddl_completo(self, archivo_salida: str):
        """Genera el DDL completo de todas las tablas."""
        print(f"📝 Generando DDL en {archivo_salida}...")
        
        with open(archivo_salida, 'w', encoding='utf-8') as f:
            f.write("-- ============================================================================\n")
            f.write("-- SISTEMA DE GESTIÓN UNIVERSITARIA - DDL COMPLETO\n")
            f.write("-- ============================================================================\n")
            f.write(f"-- Generado automáticamente\n")
            f.write(f"-- Total de tablas: {len(self.datos['tablas'])}\n")
            f.write("-- ============================================================================\n\n")
            
            # Agrupar por módulo
            modulos = {}
            for nombre, tabla in self.datos['tablas'].items():
                modulo = tabla['modulo']
                if modulo not in modulos:
                    modulos[modulo] = []
                modulos[modulo].append((nombre, tabla))
            
            # Generar por módulo
            for modulo in sorted(modulos.keys()):
                f.write(f"\n-- ============================================================================\n")
                f.write(f"-- MÓDULO: {modulo.upper()}\n")
                f.write(f"-- ============================================================================\n\n")
                
                for nombre, tabla in sorted(modulos[modulo], key=lambda x: x[1]['nombre_tabla']):
                    ddl = self._generar_ddl_tabla(nombre, tabla)
                    f.write(ddl)
                    f.write("\n\n")
            
            # Foreign Keys al final
            f.write("\n-- ============================================================================\n")
            f.write("-- FOREIGN KEYS\n")
            f.write("-- ============================================================================\n\n")
            
            for nombre, tabla in self.datos['tablas'].items():
                fks = self._generar_foreign_keys(tabla)
                if fks:
                    f.write(fks)
                    f.write("\n")
        
        print(f"✅ DDL generado exitosamente")
    
    def _generar_ddl_tabla(self, nombre_clase: str, tabla: Dict) -> str:
        """Genera el DDL de una tabla."""
        ddl = []
        
        # Comentario
        ddl.append(f"-- {tabla['nombre_tabla']}")
        if tabla.get('funcionalidad'):
            ddl.append(f"-- {tabla['funcionalidad']}")
        ddl.append("")
        
        # CREATE TABLE
        ddl.append(f"CREATE TABLE {tabla['nombre_tabla']} (")
        
        # Campos
        campos = []
        
        # ID si no hereda de AuditableEntity
        if not tabla.get('hereda_de') or tabla['hereda_de'] != 'AuditableEntity':
            campos.append("    id BIGSERIAL PRIMARY KEY")
        
        for campo in tabla['campos']:
            campos.append(self._generar_campo_ddl(campo))
        
        # Campos de auditoría (AuditableEntity)
        if tabla.get('hereda_de') == 'AuditableEntity':
            campos.extend([
                "    id BIGSERIAL PRIMARY KEY",
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
                "    created_by VARCHAR(100)",
                "    updated_by VARCHAR(100)",
                "    active BOOLEAN DEFAULT TRUE",
                "    universidad_id BIGINT"
            ])
        
        ddl.append(",\n".join(campos))
        ddl.append(");")
        
        # Comentarios en columnas
        if any(campo.get('descripcion') for campo in tabla['campos']):
            ddl.append("")
            for campo in tabla['campos']:
                if campo.get('descripcion'):
                    ddl.append(
                        f"COMMENT ON COLUMN {tabla['nombre_tabla']}.{campo['column_name']} IS '{campo['descripcion']}';"
                    )
        
        # Índices
        if tabla.get('indices'):
            ddl.append("")
            for indice in tabla['indices']:
                columnas = ', '.join(indice['columnas'])
                ddl.append(
                    f"CREATE INDEX {indice['nombre']} ON {tabla['nombre_tabla']} ({columnas});"
                )
        
        # Constraints únicos
        if tabla.get('constraints'):
            ddl.append("")
            for i, constraint in enumerate(tabla['constraints']):
                if constraint['tipo'] == 'UNIQUE':
                    campos_str = ', '.join(constraint['campos'])
                    nombre_constraint = f"uk_{tabla['nombre_tabla']}_{i+1}"
                    ddl.append(
                        f"ALTER TABLE {tabla['nombre_tabla']} ADD CONSTRAINT {nombre_constraint} UNIQUE ({campos_str});"
                    )
        
        return "\n".join(ddl)
    
    def _generar_campo_ddl(self, campo: Dict) -> str:
        """Genera el DDL de un campo."""
        partes = [f"    {campo['column_name']}"]
        
        # Tipo
        tipo = campo['tipo_sql']
        if campo.get('length'):
            tipo += f"({campo['length']})"
        elif campo.get('precision'):
            tipo += f"({campo['precision']},{campo.get('scale', 0)})"
        
        partes.append(tipo)
        
        # NOT NULL
        if not campo['nullable']:
            partes.append("NOT NULL")
        
        # UNIQUE
        if campo['unique']:
            partes.append("UNIQUE")
        
        return " ".join(partes)
    
    def _generar_foreign_keys(self, tabla: Dict) -> str:
        """Genera las foreign keys de una tabla."""
        fks = []
        
        for relacion in tabla.get('relaciones', []):
            if relacion['tipo'] in ['ManyToOne', 'OneToOne']:
                if relacion.get('join_column'):
                    tabla_destino = self._clase_a_tabla(relacion['entidad_destino'])
                    nombre_fk = f"fk_{tabla['nombre_tabla']}_{relacion['join_column']}"
                    
                    fks.append(
                        f"ALTER TABLE {tabla['nombre_tabla']} "
                        f"ADD CONSTRAINT {nombre_fk} "
                        f"FOREIGN KEY ({relacion['join_column']}) "
                        f"REFERENCES {tabla_destino}(id);"
                    )
        
        # FK a universidad (multitenancy)
        if tabla.get('hereda_de') == 'AuditableEntity':
            fks.append(
                f"ALTER TABLE {tabla['nombre_tabla']} "
                f"ADD CONSTRAINT fk_{tabla['nombre_tabla']}_universidad "
                f"FOREIGN KEY (universidad_id) "
                f"REFERENCES universidades(id);"
            )
        
        return "\n".join(fks)
    
    @staticmethod
    def _clase_a_tabla(nombre_clase: str) -> str:
        """Convierte nombre de clase a nombre de tabla."""
        import re
        return re.sub(r'(?<!^)(?=[A-Z])', '_', nombre_clase).lower()


def main():
    """Función principal."""
    print("=" * 80)
    print("🗄️  GENERADOR DE SCRIPTS SQL DDL")
    print("=" * 80)
    print()
    
    generador = GeneradorSQL('base_datos.json')
    generador.generar_ddl_completo('schema_completo.sql')
    
    print()
    print("✅ Script SQL generado: schema_completo.sql")
    print()
    print("💡 Puedes ejecutar el script en PostgreSQL:")
    print("   psql -U usuario -d base_datos -f schema_completo.sql")


if __name__ == '__main__':
    main()
