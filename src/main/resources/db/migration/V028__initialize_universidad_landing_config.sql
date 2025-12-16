-- V028__initialize_universidad_landing_config.sql
-- Inicializar la columna configuracion de universidad con estructura JSON completa

-- Actualizar universidad con ID 1 con configuración completa de landing
UPDATE universidad
SET configuracion = '{
  "landing": {
    "header": {
      "logo_url": "",
      "nombre_corto": "UPEU",
      "links": [
        {"texto": "Admisión", "url": "/admision", "target": "_self"},
        {"texto": "Pregrado", "url": "/pregrado", "target": "_self"},
        {"texto": "Posgrado", "url": "/posgrado", "target": "_self"},
        {"texto": "Investigación", "url": "/investigacion", "target": "_self"},
        {"texto": "Nosotros", "url": "/nosotros", "target": "_self"}
      ],
      "cta_button": {
        "texto": "Portal Académico",
        "url": "/login",
        "color": "#1e40af"
      }
    },
    "hero": {
      "titulo": "Universidad Peruana Unión",
      "subtitulo": "Formando líderes con valores cristianos",
      "descripcion": "Somos una institución educativa adventista comprometida con la formación integral de profesionales.",
      "video_url": "",
      "slides": [
        {
          "imagen_url": "",
          "titulo": "Excelencia Académica",
          "descripcion": "Programas acreditados internacionalmente",
          "duracion": 5000,
          "gradiente": "from-blue-900/80 to-transparent"
        },
        {
          "imagen_url": "",
          "titulo": "Formación Integral",
          "descripcion": "Desarrollo espiritual, físico e intelectual",
          "duracion": 5000,
          "gradiente": "from-purple-900/80 to-transparent"
        },
        {
          "imagen_url": "",
          "titulo": "Investigación e Innovación",
          "descripcion": "Contribuyendo al desarrollo del país",
          "duracion": 5000,
          "gradiente": "from-green-900/80 to-transparent"
        }
      ],
      "cta_buttons": [
        {"texto": "Conoce más", "url": "/nosotros", "color": "#1e40af"},
        {"texto": "Admisión 2025", "url": "/admision", "color": "#7c3aed"}
      ]
    },
    "campus": {
      "titulo": "Nuestros Campus",
      "descripcion": "Presentes en las principales ciudades del Perú",
      "sedes": [
        {
          "id": "lima",
          "nombre": "Campus Lima",
          "ciudad": "Lima",
          "direccion": "Carretera Central Km 19.5, Ñaña",
          "telefono": "+51 1 618-6300",
          "email": "informes.lima@upeu.edu.pe",
          "imagen_url": "",
          "latitud": -12.0464,
          "longitud": -76.8506
        },
        {
          "id": "juliaca",
          "nombre": "Campus Juliaca",
          "ciudad": "Juliaca",
          "direccion": "Salida Arequipa Km 06",
          "telefono": "+51 51 321-500",
          "email": "informes.juliaca@upeu.edu.pe",
          "imagen_url": "",
          "latitud": -15.5000,
          "longitud": -70.1333
        },
        {
          "id": "tarapoto",
          "nombre": "Campus Tarapoto",
          "ciudad": "Tarapoto",
          "direccion": "Jr. Los Mártires 218",
          "telefono": "+51 42 522-804",
          "email": "informes.tarapoto@upeu.edu.pe",
          "imagen_url": "",
          "latitud": -6.4818,
          "longitud": -76.3668
        }
      ]
    },
    "footer": {
      "descripcion_corta": "Universidad Peruana Unión - Formando líderes con valores cristianos desde 1919.",
      "links_rapidos": [
        {"texto": "Admisión", "url": "/admision"},
        {"texto": "Bolsa de Trabajo", "url": "/empleo"},
        {"texto": "Biblioteca", "url": "/biblioteca"},
        {"texto": "Servicios", "url": "/servicios"},
        {"texto": "Contacto", "url": "/contacto"}
      ],
      "redes_sociales": {
        "facebook": "https://facebook.com/upeu.edu.pe",
        "twitter": "https://twitter.com/upeu",
        "instagram": "https://instagram.com/upeu",
        "youtube": "https://youtube.com/upeu",
        "linkedin": "https://linkedin.com/school/upeu"
      },
      "contacto": {
        "email": "informes@upeu.edu.pe",
        "telefono": "+51 1 618-6300",
        "direccion": "Carretera Central Km 19.5, Ñaña, Lima, Perú"
      },
      "copyright": "© 2025 Universidad Peruana Unión. Todos los derechos reservados."
    }
  },
  "sistema": {
    "pantalla_principal": {
      "id": "pantalla_principal",
      "url": "",
      "descripcion": "Fondo del portal principal"
    },
    "log_url": {
      "id": "log_url",
      "url": "",
      "descripcion": "Fondo de la pantalla de login"
    }
  }
}'::jsonb,
updated_at = NOW(),
updated_by = 'SYSTEM'
WHERE id = 1;

-- Log de la migración
DO $$
BEGIN
    RAISE NOTICE 'Configuración de landing inicializada para universidad ID 1';
END $$;
