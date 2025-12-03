"""
Visualizador Interactivo de Base de Datos
==========================================

Script para explorar la documentación generada de forma interactiva.
Muestra información estructurada de tablas, relaciones y campos.

Uso: python visualizador_db.py [tabla_nombre]
"""

import json
import sys
from pathlib import Path
from typing import Dict, Any


class Colors:
    """Colores ANSI para terminal."""
    HEADER = '\033[95m'
    BLUE = '\033[94m'
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


class VisualizadorDB:
    """Visualizador interactivo de base de datos."""
    
    def __init__(self, archivo_json: str):
        """Inicializa el visualizador."""
        self.archivo = Path(archivo_json)
        self.datos = self._cargar_datos()
    
    def _cargar_datos(self) -> Dict[str, Any]:
        """Carga los datos desde el JSON."""
        if not self.archivo.exists():
            print(f"{Colors.RED}❌ Error: No se encontró {self.archivo}{Colors.ENDC}")
            print(f"{Colors.YELLOW}Ejecuta primero: python generador_documentacion_db.py{Colors.ENDC}")
            sys.exit(1)
        
        with open(self.archivo, encoding='utf-8') as f:
            return json.load(f)
    
    def mostrar_menu_principal(self):
        """Muestra el menú principal."""
        while True:
            self._limpiar_pantalla()
            self._mostrar_banner()
            
            print(f"\n{Colors.BOLD}📋 MENÚ PRINCIPAL{Colors.ENDC}\n")
            print("1. 📊 Ver resumen general")
            print("2. 🔍 Buscar tabla")
            print("3. 📁 Listar tablas por módulo")
            print("4. 🔗 Ver relaciones de una tabla")
            print("5. 📖 Ver diccionario completo")
            print("0. 🚪 Salir")
            
            opcion = input(f"\n{Colors.CYAN}Selecciona una opción: {Colors.ENDC}").strip()
            
            if opcion == '0':
                print(f"\n{Colors.GREEN}👋 ¡Hasta luego!{Colors.ENDC}")
                break
            elif opcion == '1':
                self.mostrar_resumen()
            elif opcion == '2':
                self.buscar_tabla()
            elif opcion == '3':
                self.listar_por_modulo()
            elif opcion == '4':
                self.ver_relaciones()
            elif opcion == '5':
                self.ver_diccionario()
            else:
                print(f"{Colors.RED}❌ Opción inválida{Colors.ENDC}")
                input("\nPresiona Enter para continuar...")
    
    def mostrar_resumen(self):
        """Muestra resumen general del sistema."""
        self._limpiar_pantalla()
        print(f"\n{Colors.BOLD}{Colors.BLUE}📊 RESUMEN GENERAL{Colors.ENDC}\n")
        
        metadata = self.datos['metadata']
        tablas = self.datos['tablas']
        
        print(f"📅 Generado: {metadata['generado']}")
        print(f"📦 Total de tablas: {Colors.GREEN}{metadata['total_tablas']}{Colors.ENDC}")
        
        # Agrupar por módulo
        modulos = {}
        for nombre, tabla in tablas.items():
            modulo = tabla['modulo']
            if modulo not in modulos:
                modulos[modulo] = []
            modulos[modulo].append(nombre)
        
        print(f"\n{Colors.BOLD}Distribución por módulos:{Colors.ENDC}\n")
        
        emojis = {
            'academic': '🎓',
            'security': '🔐',
            'finance': '💰',
            'catalog': '📚',
            'shared': '🔧'
        }
        
        for modulo, tablas_modulo in sorted(modulos.items()):
            emoji = emojis.get(modulo, '📦')
            print(f"  {emoji} {Colors.CYAN}{modulo.upper()}{Colors.ENDC}: {len(tablas_modulo)} tablas")
        
        # Estadísticas
        total_campos = sum(len(t['campos']) for t in tablas.values())
        total_relaciones = sum(len(t['relaciones']) for t in tablas.values())
        
        print(f"\n{Colors.BOLD}Estadísticas:{Colors.ENDC}")
        print(f"  📝 Total de campos: {total_campos}")
        print(f"  🔗 Total de relaciones: {total_relaciones}")
        print(f"  📊 Promedio campos/tabla: {total_campos // len(tablas)}")
        
        input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
    
    def buscar_tabla(self):
        """Busca y muestra información de una tabla."""
        self._limpiar_pantalla()
        print(f"\n{Colors.BOLD}{Colors.BLUE}🔍 BUSCAR TABLA{Colors.ENDC}\n")
        
        nombre = input(f"{Colors.CYAN}Nombre de la tabla (o clase): {Colors.ENDC}").strip()
        
        if not nombre:
            return
        
        # Buscar por nombre de tabla o clase
        tabla_encontrada = None
        nombre_clase = None
        
        for clase, tabla in self.datos['tablas'].items():
            if (tabla['nombre_tabla'].lower() == nombre.lower() or 
                clase.lower() == nombre.lower()):
                tabla_encontrada = tabla
                nombre_clase = clase
                break
        
        if not tabla_encontrada:
            print(f"\n{Colors.RED}❌ Tabla no encontrada: {nombre}{Colors.ENDC}")
            input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
            return
        
        self._mostrar_detalle_tabla(nombre_clase, tabla_encontrada)
    
    def _mostrar_detalle_tabla(self, nombre_clase: str, tabla: Dict):
        """Muestra el detalle completo de una tabla."""
        self._limpiar_pantalla()
        
        print(f"\n{Colors.BOLD}{Colors.HEADER}╔══════════════════════════════════════════════════════════════╗{Colors.ENDC}")
        print(f"{Colors.BOLD}{Colors.HEADER}║  📋 TABLA: {tabla['nombre_tabla'].upper():<48}║{Colors.ENDC}")
        print(f"{Colors.BOLD}{Colors.HEADER}╚══════════════════════════════════════════════════════════════╝{Colors.ENDC}\n")
        
        print(f"{Colors.BOLD}Información General:{Colors.ENDC}")
        print(f"  Clase Java: {Colors.CYAN}{tabla['clase']}{Colors.ENDC}")
        print(f"  Módulo: {Colors.YELLOW}{tabla['modulo']}{Colors.ENDC}")
        
        if tabla.get('hereda_de'):
            print(f"  Hereda de: {Colors.GREEN}{tabla['hereda_de']}{Colors.ENDC}")
        
        if tabla.get('funcionalidad'):
            print(f"\n{Colors.BOLD}Funcionalidad:{Colors.ENDC}")
            print(f"  {tabla['funcionalidad']}")
        
        # Campos
        print(f"\n{Colors.BOLD}📝 CAMPOS ({len(tabla['campos'])}):{Colors.ENDC}\n")
        print(f"  {'Campo':<25} {'Tipo SQL':<20} {'Null':<6} {'Único':<6} {'Ejemplo':<20}")
        print(f"  {'-'*25} {'-'*20} {'-'*6} {'-'*6} {'-'*20}")
        
        for campo in tabla['campos'][:15]:  # Primeros 15
            nullable = "✅" if campo['nullable'] else "❌"
            unique = "✅" if campo['unique'] else ""
            
            tipo_sql = campo['tipo_sql']
            if campo.get('length'):
                tipo_sql += f"({campo['length']})"
            elif campo.get('precision'):
                tipo_sql += f"({campo['precision']},{campo.get('scale', 0)})"
            
            ejemplo = campo.get('ejemplo', '')[:18]
            
            print(f"  {campo['column_name']:<25} {tipo_sql:<20} {nullable:<6} {unique:<6} {ejemplo:<20}")
        
        if len(tabla['campos']) > 15:
            print(f"\n  ... y {len(tabla['campos']) - 15} campos más")
        
        # Relaciones
        if tabla['relaciones']:
            print(f"\n{Colors.BOLD}🔗 RELACIONES ({len(tabla['relaciones'])}):{Colors.ENDC}\n")
            
            for rel in tabla['relaciones']:
                print(f"  {Colors.GREEN}{rel['tipo']}{Colors.ENDC} → {Colors.CYAN}{rel['entidad_destino']}{Colors.ENDC}")
                print(f"    Campo: {rel['campo']}")
                if rel.get('join_column'):
                    print(f"    Join: {rel['join_column']}")
                print()
        
        # Constraints
        if tabla['constraints']:
            print(f"{Colors.BOLD}🔒 RESTRICCIONES:{Colors.ENDC}\n")
            
            for constraint in tabla['constraints']:
                campos_str = ', '.join(constraint['campos'])
                print(f"  {Colors.YELLOW}{constraint['tipo']}{Colors.ENDC}: {campos_str}")
        
        # Índices
        if tabla['indices']:
            print(f"\n{Colors.BOLD}📊 ÍNDICES:{Colors.ENDC}\n")
            
            for indice in tabla['indices']:
                columnas_str = ', '.join(indice['columnas'])
                print(f"  {indice['nombre']}: {columnas_str}")
        
        # Reglas de negocio
        if tabla.get('reglas_negocio'):
            print(f"\n{Colors.BOLD}📋 REGLAS DE NEGOCIO:{Colors.ENDC}\n")
            
            for regla in tabla['reglas_negocio']:
                print(f"  • {regla}")
        
        # Ejemplo de datos
        if tabla.get('ejemplo_datos'):
            print(f"\n{Colors.BOLD}💡 EJEMPLO DE REGISTRO:{Colors.ENDC}\n")
            
            for campo, valor in list(tabla['ejemplo_datos'].items())[:8]:
                print(f"  {campo}: {Colors.CYAN}{valor}{Colors.ENDC}")
        
        input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
    
    def listar_por_modulo(self):
        """Lista tablas agrupadas por módulo."""
        self._limpiar_pantalla()
        print(f"\n{Colors.BOLD}{Colors.BLUE}📁 TABLAS POR MÓDULO{Colors.ENDC}\n")
        
        # Agrupar por módulo
        modulos = {}
        for nombre, tabla in self.datos['tablas'].items():
            modulo = tabla['modulo']
            if modulo not in modulos:
                modulos[modulo] = []
            modulos[modulo].append((nombre, tabla))
        
        emojis = {
            'academic': '🎓',
            'security': '🔐',
            'finance': '💰',
            'catalog': '📚',
            'shared': '🔧'
        }
        
        for modulo, tablas in sorted(modulos.items()):
            emoji = emojis.get(modulo, '📦')
            print(f"\n{Colors.BOLD}{emoji} {modulo.upper()}{Colors.ENDC} ({len(tablas)} tablas)")
            print("-" * 60)
            
            for nombre, tabla in sorted(tablas, key=lambda x: x[1]['nombre_tabla']):
                funcionalidad = tabla.get('funcionalidad', '')[:50]
                print(f"  • {Colors.CYAN}{tabla['nombre_tabla']:<25}{Colors.ENDC} {funcionalidad}")
        
        input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
    
    def ver_relaciones(self):
        """Muestra las relaciones de una tabla."""
        self._limpiar_pantalla()
        print(f"\n{Colors.BOLD}{Colors.BLUE}🔗 RELACIONES DE TABLA{Colors.ENDC}\n")
        
        nombre = input(f"{Colors.CYAN}Nombre de la tabla: {Colors.ENDC}").strip()
        
        if not nombre:
            return
        
        # Buscar tabla
        tabla_encontrada = None
        
        for clase, tabla in self.datos['tablas'].items():
            if (tabla['nombre_tabla'].lower() == nombre.lower() or 
                clase.lower() == nombre.lower()):
                tabla_encontrada = tabla
                break
        
        if not tabla_encontrada:
            print(f"\n{Colors.RED}❌ Tabla no encontrada{Colors.ENDC}")
            input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
            return
        
        print(f"\n{Colors.BOLD}Relaciones de {tabla_encontrada['nombre_tabla']}:{Colors.ENDC}\n")
        
        if not tabla_encontrada['relaciones']:
            print(f"{Colors.YELLOW}  No tiene relaciones definidas{Colors.ENDC}")
        else:
            for rel in tabla_encontrada['relaciones']:
                color = {
                    'ManyToOne': Colors.GREEN,
                    'OneToMany': Colors.BLUE,
                    'ManyToMany': Colors.YELLOW,
                    'OneToOne': Colors.CYAN
                }.get(rel['tipo'], Colors.ENDC)
                
                print(f"  {color}{rel['tipo']}{Colors.ENDC}")
                print(f"    → {Colors.CYAN}{rel['entidad_destino']}{Colors.ENDC}")
                print(f"    Campo: {rel['campo']}")
                
                if rel.get('join_column'):
                    print(f"    Join Column: {rel['join_column']}")
                if rel.get('mapped_by'):
                    print(f"    Mapped By: {rel['mapped_by']}")
                
                print()
        
        input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
    
    def ver_diccionario(self):
        """Muestra el diccionario completo."""
        self._limpiar_pantalla()
        print(f"\n{Colors.BOLD}{Colors.BLUE}📖 DICCIONARIO COMPLETO{Colors.ENDC}\n")
        
        print("Generando archivo diccionario_completo.txt...")
        
        with open('diccionario_completo.txt', 'w', encoding='utf-8') as f:
            for nombre, tabla in sorted(self.datos['tablas'].items(), 
                                       key=lambda x: (x[1]['modulo'], x[1]['nombre_tabla'])):
                f.write(f"\n{'='*80}\n")
                f.write(f"TABLA: {tabla['nombre_tabla']}\n")
                f.write(f"Clase: {tabla['clase']}\n")
                f.write(f"Módulo: {tabla['modulo']}\n")
                f.write(f"{'='*80}\n\n")
                
                if tabla.get('funcionalidad'):
                    f.write(f"Funcionalidad:\n{tabla['funcionalidad']}\n\n")
                
                f.write("CAMPOS:\n")
                f.write("-" * 80 + "\n")
                
                for campo in tabla['campos']:
                    f.write(f"\n{campo['column_name']}\n")
                    f.write(f"  Tipo: {campo['tipo_sql']} ({campo['tipo_java']})\n")
                    f.write(f"  Nullable: {'Sí' if campo['nullable'] else 'No'}\n")
                    
                    if campo['unique']:
                        f.write(f"  Único: Sí\n")
                    if campo.get('ejemplo'):
                        f.write(f"  Ejemplo: {campo['ejemplo']}\n")
                
                if tabla['relaciones']:
                    f.write("\n\nRELACIONES:\n")
                    f.write("-" * 80 + "\n")
                    
                    for rel in tabla['relaciones']:
                        f.write(f"\n{rel['tipo']} → {rel['entidad_destino']}\n")
                        f.write(f"  Campo: {rel['campo']}\n")
                
                f.write("\n\n")
        
        print(f"\n{Colors.GREEN}✅ Archivo generado: diccionario_completo.txt{Colors.ENDC}")
        input(f"\n{Colors.YELLOW}Presiona Enter para continuar...{Colors.ENDC}")
    
    def _mostrar_banner(self):
        """Muestra el banner del sistema."""
        print(f"""
{Colors.BOLD}{Colors.CYAN}
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║     🎓  SISTEMA DE GESTIÓN UNIVERSITARIA                    ║
║     📊  Visualizador de Base de Datos                       ║
║                                                              ║
║     Total de tablas: {len(self.datos['tablas']):<3}                                   ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
{Colors.ENDC}
        """)
    
    @staticmethod
    def _limpiar_pantalla():
        """Limpia la pantalla."""
        import os
        os.system('cls' if os.name == 'nt' else 'clear')


def main():
    """Función principal."""
    archivo_json = 'base_datos.json'
    
    # Si se pasa argumento, buscar esa tabla directamente
    if len(sys.argv) > 1:
        visualizador = VisualizadorDB(archivo_json)
        nombre_tabla = sys.argv[1]
        
        for clase, tabla in visualizador.datos['tablas'].items():
            if (tabla['nombre_tabla'].lower() == nombre_tabla.lower() or 
                clase.lower() == nombre_tabla.lower()):
                visualizador._mostrar_detalle_tabla(clase, tabla)
                return
        
        print(f"{Colors.RED}❌ Tabla no encontrada: {nombre_tabla}{Colors.ENDC}")
        return
    
    # Modo interactivo
    visualizador = VisualizadorDB(archivo_json)
    visualizador.mostrar_menu_principal()


if __name__ == '__main__':
    main()
