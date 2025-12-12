#!/usr/bin/env python3
"""
Script para analizar si todas las tablas/entidades están documentadas en Swagger
"""

import os
import re
import json
from pathlib import Path
from collections import defaultdict

# Colores para terminal
class Colors:
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BLUE = '\033[94m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'

def extract_table_name_from_entity(file_path):
    """Extrae el nombre de la tabla de un archivo de entidad JPA"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        
    # Buscar @Table(name = "nombre_tabla")
    table_match = re.search(r'@Table\s*\(\s*name\s*=\s*"([^"]+)"', content)
    if table_match:
        return table_match.group(1)
    
    # Si no hay @Table, usar el nombre de la clase en snake_case
    class_match = re.search(r'public\s+class\s+(\w+)', content)
    if class_match:
        class_name = class_match.group(1)
        # Convertir a snake_case
        snake_case = re.sub(r'(?<!^)(?=[A-Z])', '_', class_name).lower()
        return snake_case
    
    return None

def get_entity_info(file_path):
    """Obtiene información de una entidad"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Nombre de la clase
    class_match = re.search(r'public\s+class\s+(\w+)', content)
    class_name = class_match.group(1) if class_match else None
    
    # Nombre de la tabla
    table_name = extract_table_name_from_entity(file_path)
    
    # Módulo (assessment, core, curriculum, etc.)
    module = None
    if '/assessment/' in file_path:
        module = 'assessment'
    elif '/core/' in file_path:
        module = 'core'
    elif '/curriculum/' in file_path:
        module = 'curriculum'
    elif '/enrollment/' in file_path:
        module = 'enrollment'
    elif '/finance/' in file_path:
        module = 'finance'
    elif '/people/' in file_path:
        module = 'people'
    elif '/security/' in file_path:
        module = 'security'
    elif '/shared/' in file_path:
        module = 'shared'
    
    return {
        'class_name': class_name,
        'table_name': table_name,
        'module': module,
        'file_path': file_path
    }

def get_controller_path(file_path):
    """Extrae el path del controlador REST"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Buscar @Path("/api/...")
    path_match = re.search(r'@Path\s*\(\s*"([^"]+)"', content)
    if path_match:
        return path_match.group(1)
    
    return None

def find_all_entities(base_path):
    """Encuentra todas las entidades JPA en el proyecto"""
    entities = []
    
    # Buscar todos los archivos de entidades
    entity_patterns = [
        '**/domain/entities/*.java',
        '**/entities/*.java'
    ]
    
    for pattern in entity_patterns:
        for file_path in Path(base_path).rglob(pattern):
            file_path_str = str(file_path)
            # Verificar que tenga @Entity
            with open(file_path, 'r', encoding='utf-8') as f:
                if '@Entity' in f.read():
                    entity_info = get_entity_info(file_path_str)
                    if entity_info['class_name']:
                        entities.append(entity_info)
    
    return entities

def find_all_controllers(base_path):
    """Encuentra todos los controladores REST"""
    controllers = {}
    
    # Buscar todos los controladores
    for file_path in Path(base_path).rglob('**/*Controller.java'):
        controller_path = get_controller_path(str(file_path))
        if controller_path:
            # Extraer el nombre de la entidad del controlador
            controller_name = file_path.stem.replace('Controller', '')
            controllers[controller_name] = {
                'path': controller_path,
                'file': str(file_path)
            }
    
    return controllers

def generate_report(base_path):
    """Genera el reporte completo"""
    print(f"\n{Colors.BOLD}{Colors.BLUE}{'='*80}{Colors.ENDC}")
    print(f"{Colors.BOLD}{Colors.BLUE}ANÁLISIS DE COBERTURA DE SWAGGER/OpenAPI{Colors.ENDC}")
    print(f"{Colors.BOLD}{Colors.BLUE}{'='*80}{Colors.ENDC}\n")
    
    # Obtener entidades y controladores
    entities = find_all_entities(base_path)
    controllers = find_all_controllers(base_path)
    
    # Organizar por módulo
    entities_by_module = defaultdict(list)
    for entity in entities:
        entities_by_module[entity['module']].append(entity)
    
    # Estadísticas
    total_entities = len(entities)
    total_with_controller = 0
    total_without_controller = 0
    
    print(f"{Colors.BOLD}RESUMEN GENERAL{Colors.ENDC}")
    print(f"Total de entidades encontradas: {total_entities}")
    print(f"Total de controladores encontrados: {len(controllers)}\n")
    
    # Analizar por módulo
    for module in sorted(entities_by_module.keys()):
        module_entities = entities_by_module[module]
        print(f"\n{Colors.BOLD}{Colors.BLUE}{'─'*80}{Colors.ENDC}")
        print(f"{Colors.BOLD}Módulo: {module.upper() if module else 'SIN MÓDULO'}{Colors.ENDC}")
        print(f"{Colors.BOLD}{Colors.BLUE}{'─'*80}{Colors.ENDC}\n")
        
        with_controller = []
        without_controller = []
        
        for entity in module_entities:
            entity_name = entity['class_name']
            table_name = entity['table_name']
            
            # Buscar controlador
            has_controller = entity_name in controllers
            
            if has_controller:
                with_controller.append(entity)
                total_with_controller += 1
            else:
                without_controller.append(entity)
                total_without_controller += 1
        
        # Mostrar entidades CON controlador
        if with_controller:
            print(f"{Colors.GREEN}✓ Entidades CON endpoint en Swagger ({len(with_controller)}):{Colors.ENDC}")
            for entity in sorted(with_controller, key=lambda x: x['class_name']):
                controller = controllers.get(entity['class_name'])
                print(f"  • {entity['class_name']:30} → {controller['path']}")
        
        # Mostrar entidades SIN controlador
        if without_controller:
            print(f"\n{Colors.RED}✗ Entidades SIN endpoint en Swagger ({len(without_controller)}):{Colors.ENDC}")
            for entity in sorted(without_controller, key=lambda x: x['class_name']):
                print(f"  • {entity['class_name']:30} (tabla: {entity['table_name']})")
    
    # Resumen final
    coverage_percent = (total_with_controller / total_entities * 100) if total_entities > 0 else 0
    
    print(f"\n{Colors.BOLD}{Colors.BLUE}{'='*80}{Colors.ENDC}")
    print(f"{Colors.BOLD}RESUMEN FINAL{Colors.ENDC}")
    print(f"{Colors.BOLD}{Colors.BLUE}{'='*80}{Colors.ENDC}\n")
    print(f"{Colors.GREEN}Entidades con endpoint:{Colors.ENDC} {total_with_controller}")
    print(f"{Colors.RED}Entidades sin endpoint:{Colors.ENDC} {total_without_controller}")
    print(f"{Colors.BOLD}Cobertura total:{Colors.ENDC} {coverage_percent:.1f}%")
    
    # Barra de progreso
    bar_length = 50
    filled_length = int(bar_length * total_with_controller // total_entities)
    bar = '█' * filled_length + '░' * (bar_length - filled_length)
    
    color = Colors.GREEN if coverage_percent >= 80 else Colors.YELLOW if coverage_percent >= 50 else Colors.RED
    print(f"\n{color}{bar}{Colors.ENDC} {coverage_percent:.1f}%\n")
    
    # Recomendaciones
    if total_without_controller > 0:
        print(f"{Colors.YELLOW}RECOMENDACIONES:{Colors.ENDC}")
        print(f"• Considera crear controladores REST para las {total_without_controller} entidades sin endpoint")
        print(f"• Esto mejorará la documentación automática de Swagger/OpenAPI")
        print(f"• Facilitará el acceso a estos recursos desde el frontend\n")

if __name__ == '__main__':
    # Ruta base del proyecto
    base_path = r'C:\Cursos\alpha\glender\backendalpha\src\main\java'
    
    if not os.path.exists(base_path):
        print(f"{Colors.RED}Error: No se encontró la ruta {base_path}{Colors.ENDC}")
        exit(1)
    
    generate_report(base_path)
