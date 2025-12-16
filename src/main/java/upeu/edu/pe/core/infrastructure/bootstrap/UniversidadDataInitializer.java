package upeu.edu.pe.core.infrastructure.bootstrap;

import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import upeu.edu.pe.core.domain.entities.Universidad;
import upeu.edu.pe.core.domain.repositories.UniversidadRepository;

/**
 * Inicializa la universidad por defecto (UPEU) al arrancar la aplicación.
 * Priority 4 = ejecuta después de usuarios/permisos
 * Solo inserta datos si la tabla está vacía.
 */
@ApplicationScoped
public class UniversidadDataInitializer {

    private static final Logger LOG = Logger.getLogger(UniversidadDataInitializer.class);

    @Inject
    UniversidadRepository universidadRepository;

    @Transactional
    void onStart(@Observes @Priority(4) StartupEvent ev) {
        if (universidadRepository.count() > 0) {
            LOG.info("Universidad already exists, skipping initialization.");
            return;
        }

        LOG.info("Seeding Universidad data (UPEU)...");

        Universidad upeu = new Universidad();
        upeu.setCodigo("UPEU");
        upeu.setNombre("Universidad Peruana Unión");
        upeu.setRuc("20144918681");
        upeu.setTipo("PRIVADA");
        upeu.setDominio("upeu.edu.pe");
        upeu.setWebsite("https://upeu.edu.pe");
        upeu.setLogoUrl(
                "https://deliverirecursos.blob.core.windows.net/institution-images/universidades%2F1%2Flogo.png");
        upeu.setEstado("ACTIVA");
        upeu.setPlan("PREMIUM");
        upeu.setLocale("es_PE");
        upeu.setZonaHoraria("America/Lima");
        upeu.setMaxEstudiantes(10000);
        upeu.setMaxDocentes(1000);
        upeu.setTotalEstudiantes(0);
        upeu.setTotalDocentes(0);
        upeu.setActive(true);

        // Configuración JSON completa del landing page
        String configuracionJson = """
                {
                  "landing": {
                    "hero": {
                      "slides": [
                        {"titulo": "Excelencia Académica", "duracion": 5000, "gradiente": "from-blue-900/80 to-transparent", "imagen_url": "", "descripcion": "Programas acreditados internacionalmente"},
                        {"titulo": "Formación Integral", "duracion": 5000, "gradiente": "from-purple-900/80 to-transparent", "imagen_url": "", "descripcion": "Desarrollo espiritual, físico e intelectual"},
                        {"titulo": "Investigación e Innovación", "duracion": 5000, "gradiente": "from-green-900/80 to-transparent", "imagen_url": "", "descripcion": "Contribuyendo al desarrollo del país"}
                      ],
                      "titulo": "Universidad Peruana Unión",
                      "subtitulo": "Formando líderes con valores cristianos",
                      "video_url": "",
                      "cta_buttons": [
                        {"url": "/nosotros", "color": "#1e40af", "texto": "Conoce más"},
                        {"url": "/admision", "color": "#7c3aed", "texto": "Admisión 2025"}
                      ],
                      "descripcion": "Somos una institución educativa adventista comprometida con la formación integral de profesionales."
                    },
                    "campus": {
                      "sedes": [
                        {"id": "lima", "email": "informes.lima@upeu.edu.pe", "ciudad": "Lima", "nombre": "Campus Lima", "latitud": -12.0464, "longitud": -76.8506, "telefono": "+51 1 618-6300", "direccion": "Carretera Central Km 19.5, Ñaña", "imagen_url": ""},
                        {"id": "juliaca", "email": "informes.juliaca@upeu.edu.pe", "ciudad": "Juliaca", "nombre": "Campus Juliaca", "latitud": -15.5, "longitud": -70.1333, "telefono": "+51 51 321-500", "direccion": "Salida Arequipa Km 06", "imagen_url": ""},
                        {"id": "tarapoto", "email": "informes.tarapoto@upeu.edu.pe", "ciudad": "Tarapoto", "nombre": "Campus Tarapoto", "latitud": -6.4818, "longitud": -76.3668, "telefono": "+51 42 522-804", "direccion": "Jr. Los Mártires 218", "imagen_url": ""}
                      ],
                      "titulo": "Nuestros Campus",
                      "descripcion": "Presentes en las principales ciudades del Perú"
                    },
                    "footer": {
                      "contacto": {"email": "informes@upeu.edu.pe", "telefono": "+51 1 618-6300", "direccion": "Carretera Central Km 19.5, Ñaña, Lima, Perú"},
                      "copyright": "© 2025 Universidad Peruana Unión. Todos los derechos reservados.",
                      "links_rapidos": [
                        {"url": "/admision", "texto": "Admisión"},
                        {"url": "/empleo", "texto": "Bolsa de Trabajo"},
                        {"url": "/biblioteca", "texto": "Biblioteca"},
                        {"url": "/servicios", "texto": "Servicios"},
                        {"url": "/contacto", "texto": "Contacto"}
                      ],
                      "redes_sociales": {"twitter": "https://twitter.com/upeu", "youtube": "https://youtube.com/upeu", "facebook": "https://facebook.com/upeu.edu.pe", "linkedin": "https://linkedin.com/school/upeu", "instagram": "https://instagram.com/upeu"},
                      "descripcion_corta": "Universidad Peruana Unión - Formando líderes con valores cristianos desde 1919."
                    },
                    "header": {
                      "links": [
                        {"url": "/admision", "texto": "Admisión", "target": "_self"},
                        {"url": "/pregrado", "texto": "Pregrado", "target": "_self"},
                        {"url": "/posgrado", "texto": "Posgrado", "target": "_self"},
                        {"url": "/investigacion", "texto": "Investigación", "target": "_self"},
                        {"url": "/nosotros", "texto": "Nosotros", "target": "_self"}
                      ],
                      "logo_url": "",
                      "cta_button": {"url": "/login", "color": "#1e40af", "texto": "Portal Académico"},
                      "nombre_corto": "UPEU"
                    }
                  },
                  "sistema": {
                    "log_url": {"id": "log_url", "url": "https://deliverirecursos.blob.core.windows.net/institution-images/universidades%2F1%2Fconfig%2Flog_url.jpg", "descripcion": "Fondo de la pantalla de login"},
                    "pantalla_principal": {"id": "pantalla_principal", "url": "https://deliverirecursos.blob.core.windows.net/institution-images/universidades%2F1%2Fconfig%2Fpantalla_principal.webp", "descripcion": "Fondo del portal principal"}
                  }
                }
                """;

        upeu.setConfiguracion(configuracionJson);
        universidadRepository.persist(upeu);

        LOG.infof("✅ Universidad seeded: %s (%s)", upeu.getNombre(), upeu.getCodigo());
    }
}
