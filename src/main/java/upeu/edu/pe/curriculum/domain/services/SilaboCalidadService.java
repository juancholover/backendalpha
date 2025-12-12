package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboCalidad;
import upeu.edu.pe.curriculum.domain.repositories.SilaboCalidadRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para evaluar la calidad de un sílabo.
 * 
 * Criterios de evaluación:
 * 1. Descripción clara y completa (0-100)
 * 2. Objetivos medibles y alcanzables (0-100)
 * 3. Bibliografía actualizada y relevante (0-100)
 * 4. Sistema de evaluación completo (0-100)
 * 5. Metodología bien definida (0-100)
 * 6. Competencias claras (0-100)
 * 7. Unidades bien estructuradas (0-100)
 * 8. Actividades coherentes (0-100)
 * 
 * Puntaje mínimo para aprobación: 80/100
 */
@ApplicationScoped
@Slf4j
public class SilaboCalidadService {

    @Inject
    SilaboCalidadRepository calidadRepository;

    // Constantes de evaluación
    private static final int MIN_DESCRIPCION_LENGTH = 100;
    private static final int MIN_SUMILLA_LENGTH = 200;
    private static final int MIN_COMPETENCIAS_LENGTH = 150;
    private static final int MIN_METODOLOGIA_LENGTH = 100;
    private static final int MIN_BIBLIOGRAFIA_LENGTH = 100;
    private static final int MIN_UNIDADES = 3;
    private static final int MIN_ACTIVIDADES_POR_UNIDAD = 2;

    /**
     * Evalúa la calidad completa de un sílabo
     */
    @Transactional
    public SilaboCalidad evaluarCalidad(Silabo silabo, String evaluadoPor) {
        log.info("🔍 Iniciando evaluación de calidad para sílabo ID: {}", silabo.getId());

        SilaboCalidad calidad = new SilaboCalidad();
        calidad.setSilabo(silabo);
        calidad.setEvaluadoPor(evaluadoPor);
        calidad.setFechaEvaluacion(LocalDateTime.now());

        // Evaluar cada criterio
        calidad.setDescripcionScore(evaluarDescripcion(silabo));
        calidad.setObjetivosScore(evaluarObjetivos(silabo));
        calidad.setBibliografiaScore(evaluarBibliografia(silabo));
        calidad.setEvaluacionesScore(evaluarEvaluaciones(silabo));
        calidad.setMetodologiaScore(evaluarMetodologia(silabo));
        calidad.setCompetenciasScore(evaluarCompetencias(silabo));
        calidad.setUnidadesScore(evaluarUnidades(silabo));
        calidad.setActividadesScore(evaluarActividades(silabo));

        // Calcular puntaje total
        calidad.calcularPuntajeTotal();
        
        // Calcular aprobación (normalmente se hace en @PrePersist, pero lo necesitamos antes)
        calidad.calcularAprobacion();

        // Generar observaciones
        calidad.setObservaciones(generarObservaciones(calidad));

        // Guardar evaluación
        calidadRepository.persist(calidad);

        log.info("✅ Evaluación completada: Puntaje Total = {}/100, Aprobado = {}", 
            calidad.getPuntajeTotal(), calidad.getAprobado());

        return calidad;
    }

    /**
     * Evalúa la calidad de la descripción/sumilla
     */
    private Integer evaluarDescripcion(Silabo silabo) {
        int puntaje = 0;
        String sumilla = silabo.getSumilla();

        if (sumilla == null || sumilla.trim().isEmpty()) {
            return 0;
        }

        // Longitud adecuada (40 puntos)
        if (sumilla.length() >= MIN_SUMILLA_LENGTH) {
            puntaje += 40;
        } else if (sumilla.length() >= MIN_DESCRIPCION_LENGTH) {
            puntaje += 20;
        }

        // Claridad y estructura (30 puntos)
        if (sumilla.contains(".") && sumilla.split("\\.").length >= 2) {
            puntaje += 15;
        }
        if (sumilla.matches(".*\\b(curso|asignatura|materia)\\b.*")) {
            puntaje += 15;
        }

        // Contenido relevante (30 puntos)
        String[] palabrasClave = {"objetivo", "competencia", "estudiante", "aprendizaje", "desarrollar"};
        int palabrasEncontradas = 0;
        for (String palabra : palabrasClave) {
            if (sumilla.toLowerCase().contains(palabra)) {
                palabrasEncontradas++;
            }
        }
        puntaje += Math.min(30, palabrasEncontradas * 6);

        return Math.min(100, puntaje);
    }

    /**
     * Evalúa los objetivos del curso
     */
    private Integer evaluarObjetivos(Silabo silabo) {
        // Por ahora evaluamos las competencias como objetivos
        return evaluarCompetencias(silabo);
    }

    /**
     * Evalúa la bibliografía
     */
    private Integer evaluarBibliografia(Silabo silabo) {
        int puntaje = 0;
        String bibliografia = silabo.getBibliografia();

        if (bibliografia == null || bibliografia.trim().isEmpty()) {
            return 0;
        }

        // Longitud mínima (30 puntos)
        if (bibliografia.length() >= MIN_BIBLIOGRAFIA_LENGTH) {
            puntaje += 30;
        }

        // Número de referencias (30 puntos)
        int numReferencias = contarReferencias(bibliografia);
        if (numReferencias >= 5) {
            puntaje += 30;
        } else if (numReferencias >= 3) {
            puntaje += 20;
        } else if (numReferencias >= 1) {
            puntaje += 10;
        }

        // Referencias actualizadas (20 puntos)
        if (bibliografia.matches(".*20(1[5-9]|2[0-5]).*")) {
            puntaje += 20;
        }

        // Formato adecuado (20 puntos)
        if (bibliografia.contains("ISBN") || bibliografia.contains("DOI") || 
            bibliografia.matches(".*\\b(ed\\.|pp\\.|vol\\.)\\b.*")) {
            puntaje += 20;
        }

        return Math.min(100, puntaje);
    }

    /**
     * Evalúa el sistema de evaluación (criterios, ponderaciones)
     */
    private Integer evaluarEvaluaciones(Silabo silabo) {
        int puntaje = 0;

        // Por ahora evaluamos si tiene actividades con evaluación
        if (silabo.getUnidades() != null && !silabo.getUnidades().isEmpty()) {
            boolean tieneEvaluaciones = silabo.getUnidades().stream()
                .flatMap(u -> u.getActividades().stream())
                .anyMatch(a -> a.getPonderacion() != null && a.getPonderacion().intValue() > 0);

            if (tieneEvaluaciones) {
                puntaje += 50;

                // Verificar que las ponderaciones sumen 100%
                double sumaPonderaciones = silabo.getUnidades().stream()
                    .flatMap(u -> u.getActividades().stream())
                    .filter(a -> a.getPonderacion() != null)
                    .mapToDouble(a -> a.getPonderacion().doubleValue())
                    .sum();

                if (Math.abs(sumaPonderaciones - 100.0) < 1.0) {
                    puntaje += 50;
                } else if (sumaPonderaciones > 0) {
                    puntaje += 25;
                }
            }
        }

        return Math.min(100, puntaje);
    }

    /**
     * Evalúa la metodología
     */
    private Integer evaluarMetodologia(Silabo silabo) {
        int puntaje = 0;
        String metodologia = silabo.getMetodologia();

        if (metodologia == null || metodologia.trim().isEmpty()) {
            return 0;
        }

        // Longitud adecuada (40 puntos)
        if (metodologia.length() >= MIN_METODOLOGIA_LENGTH) {
            puntaje += 40;
        }

        // Palabras clave metodológicas (30 puntos)
        String[] palabrasMetodologia = {"activa", "colaborativa", "práctica", "teórica", 
                                        "experimental", "investigación", "proyecto"};
        int palabrasEncontradas = 0;
        for (String palabra : palabrasMetodologia) {
            if (metodologia.toLowerCase().contains(palabra)) {
                palabrasEncontradas++;
            }
        }
        puntaje += Math.min(30, palabrasEncontradas * 10);

        // Estructura clara (30 puntos)
        if (metodologia.contains("-") || metodologia.contains("•") || 
            metodologia.split("\\.").length >= 2) {
            puntaje += 30;
        }

        return Math.min(100, puntaje);
    }

    /**
     * Evalúa las competencias
     */
    private Integer evaluarCompetencias(Silabo silabo) {
        int puntaje = 0;
        String competencias = silabo.getCompetencias();

        if (competencias == null || competencias.trim().isEmpty()) {
            return 0;
        }

        // Longitud adecuada (30 puntos)
        if (competencias.length() >= MIN_COMPETENCIAS_LENGTH) {
            puntaje += 30;
        }

        // Número de competencias (30 puntos)
        int numCompetencias = contarCompetencias(competencias);
        if (numCompetencias >= 5) {
            puntaje += 30;
        } else if (numCompetencias >= 3) {
            puntaje += 20;
        } else if (numCompetencias >= 1) {
            puntaje += 10;
        }

        // Verbos de acción (40 puntos)
        String[] verbosAccion = {"analizar", "diseñar", "desarrollar", "evaluar", 
                                "aplicar", "crear", "implementar", "resolver"};
        int verbosEncontrados = 0;
        for (String verbo : verbosAccion) {
            if (competencias.toLowerCase().contains(verbo)) {
                verbosEncontrados++;
            }
        }
        puntaje += Math.min(40, verbosEncontrados * 8);

        return Math.min(100, puntaje);
    }

    /**
     * Evalúa la estructura de unidades
     */
    private Integer evaluarUnidades(Silabo silabo) {
        int puntaje = 0;

        if (silabo.getUnidades() == null || silabo.getUnidades().isEmpty()) {
            return 0;
        }

        int numUnidades = silabo.getUnidades().size();

        // Número adecuado de unidades (40 puntos)
        if (numUnidades >= MIN_UNIDADES && numUnidades <= 8) {
            puntaje += 40;
        } else if (numUnidades >= 2) {
            puntaje += 20;
        }

        // Unidades con título (30 puntos)
        long unidadesConTitulo = silabo.getUnidades().stream()
            .filter(u -> u.getTitulo() != null && !u.getTitulo().trim().isEmpty())
            .count();
        puntaje += (int) Math.min(30, (unidadesConTitulo * 30.0) / numUnidades);

        // Unidades con contenido (30 puntos)
        long unidadesConContenido = silabo.getUnidades().stream()
            .filter(u -> u.getContenidos() != null && u.getContenidos().length() >= 50)
            .count();
        puntaje += (int) Math.min(30, (unidadesConContenido * 30.0) / numUnidades);

        return Math.min(100, puntaje);
    }

    /**
     * Evalúa las actividades de aprendizaje
     */
    private Integer evaluarActividades(Silabo silabo) {
        int puntaje = 0;

        if (silabo.getUnidades() == null || silabo.getUnidades().isEmpty()) {
            return 0;
        }

        int totalActividades = silabo.getUnidades().stream()
            .mapToInt(u -> u.getActividades() != null ? u.getActividades().size() : 0)
            .sum();

        if (totalActividades == 0) {
            return 0;
        }

        // Número adecuado de actividades (40 puntos)
        int numUnidades = silabo.getUnidades().size();
        double actividadesPorUnidad = (double) totalActividades / numUnidades;
        if (actividadesPorUnidad >= MIN_ACTIVIDADES_POR_UNIDAD) {
            puntaje += 40;
        } else if (actividadesPorUnidad >= 1) {
            puntaje += 20;
        }

        // Actividades con nombre (30 puntos)
        long actividadesConNombre = silabo.getUnidades().stream()
            .flatMap(u -> u.getActividades().stream())
            .filter(a -> a.getNombre() != null && !a.getNombre().trim().isEmpty())
            .count();
        puntaje += (int) Math.min(30, (actividadesConNombre * 30.0) / totalActividades);

        // Actividades con tipo definido (30 puntos)
        long actividadesConTipo = silabo.getUnidades().stream()
            .flatMap(u -> u.getActividades().stream())
            .filter(a -> a.getTipo() != null && !a.getTipo().trim().isEmpty())
            .count();
        puntaje += (int) Math.min(30, (actividadesConTipo * 30.0) / totalActividades);

        return Math.min(100, puntaje);
    }

    /**
     * Genera observaciones basadas en los puntajes
     */
    private String generarObservaciones(SilaboCalidad calidad) {
        StringBuilder obs = new StringBuilder();
        List<String> problemas = new ArrayList<>();
        List<String> fortalezas = new ArrayList<>();

        // Identificar problemas (< 60)
        if (calidad.getDescripcionScore() < 60) {
            problemas.add("La descripción/sumilla necesita ser más completa y clara");
        }
        if (calidad.getObjetivosScore() < 60) {
            problemas.add("Los objetivos deben ser más específicos y medibles");
        }
        if (calidad.getBibliografiaScore() < 60) {
            problemas.add("La bibliografía requiere más referencias actualizadas");
        }
        if (calidad.getEvaluacionesScore() < 60) {
            problemas.add("El sistema de evaluación debe estar mejor definido");
        }
        if (calidad.getMetodologiaScore() < 60) {
            problemas.add("La metodología necesita mayor detalle");
        }
        if (calidad.getCompetenciasScore() < 60) {
            problemas.add("Las competencias deben ser más claras y específicas");
        }
        if (calidad.getUnidadesScore() < 60) {
            problemas.add("Las unidades requieren mejor estructuración");
        }
        if (calidad.getActividadesScore() < 60) {
            problemas.add("Las actividades de aprendizaje necesitan mayor desarrollo");
        }

        // Identificar fortalezas (>= 80)
        if (calidad.getDescripcionScore() >= 80) {
            fortalezas.add("Excelente descripción del curso");
        }
        if (calidad.getBibliografiaScore() >= 80) {
            fortalezas.add("Bibliografía completa y actualizada");
        }
        if (calidad.getUnidadesScore() >= 80) {
            fortalezas.add("Unidades bien estructuradas");
        }

        // Construir mensaje
        if (calidad.getAprobado()) {
            obs.append("✅ SÍLABO APROBADO - Calidad suficiente para publicación.\n\n");
        } else {
            obs.append("❌ SÍLABO REQUIERE MEJORAS - Puntaje insuficiente (mínimo 80/100).\n\n");
        }

        if (!fortalezas.isEmpty()) {
            obs.append("FORTALEZAS:\n");
            fortalezas.forEach(f -> obs.append("• ").append(f).append("\n"));
            obs.append("\n");
        }

        if (!problemas.isEmpty()) {
            obs.append("ÁREAS A MEJORAR:\n");
            problemas.forEach(p -> obs.append("• ").append(p).append("\n"));
        }

        return obs.toString();
    }

    /**
     * Cuenta el número de referencias bibliográficas
     */
    private int contarReferencias(String bibliografia) {
        if (bibliografia == null) return 0;
        
        // Buscar patrones comunes de referencias
        int count = 0;
        String[] lineas = bibliografia.split("\\n");
        for (String linea : lineas) {
            if (linea.matches(".*\\d{4}.*") || // Contiene año
                linea.matches(".*\\b[A-Z][a-z]+,.*") || // Formato apellido,
                linea.contains("ISBN") || 
                linea.contains("DOI")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Cuenta el número de competencias
     */
    private int contarCompetencias(String competencias) {
        if (competencias == null) return 0;
        
        // Buscar patrones de listado
        int count = 0;
        if (competencias.contains("-")) {
            count = competencias.split("-").length - 1;
        } else if (competencias.contains("•")) {
            count = competencias.split("•").length - 1;
        } else if (competencias.contains("\n")) {
            count = competencias.split("\\n").length;
        } else {
            count = competencias.split("\\.").length;
        }
        return Math.max(1, count);
    }

    /**
     * Verifica si un sílabo cumple con el estándar de calidad
     */
    public boolean cumpleEstandarCalidad(Long silaboId) {
        return calidadRepository.hasApprovedQuality(silaboId);
    }

    /**
     * Obtiene la última evaluación de calidad de un sílabo
     */
    public SilaboCalidad obtenerUltimaEvaluacion(Long silaboId) {
        return calidadRepository.findLatestBySilabo(silaboId);
    }
}
