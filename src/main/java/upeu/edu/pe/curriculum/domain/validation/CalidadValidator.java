package upeu.edu.pe.curriculum.domain.validation;

import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboUnidad;

import java.util.ArrayList;
import java.util.List;

/**
 * Validador de reglas de negocio para calidad de sílabos
 */
public class CalidadValidator {

    // Constantes de validación
    public static final int PUNTAJE_MINIMO_APROBACION = 80;
    public static final int MIN_LONGITUD_SUMILLA = 200;
    public static final int MIN_LONGITUD_COMPETENCIAS = 150;
    public static final int MIN_LONGITUD_METODOLOGIA = 100;
    public static final int MIN_LONGITUD_BIBLIOGRAFIA = 100;
    public static final int MIN_NUMERO_UNIDADES = 3;
    public static final int MAX_NUMERO_UNIDADES = 8;
    public static final int MIN_ACTIVIDADES_POR_UNIDAD = 2;
    public static final int MIN_REFERENCIAS_BIBLIOGRAFIA = 3;

    /**
     * Valida que el sílabo tenga la estructura mínima requerida
     */
    public static List<String> validarEstructuraMinima(Silabo silabo) {
        List<String> errores = new ArrayList<>();

        // Validar sumilla
        if (silabo.getSumilla() == null || silabo.getSumilla().trim().isEmpty()) {
            errores.add("La sumilla es obligatoria");
        } else if (silabo.getSumilla().length() < MIN_LONGITUD_SUMILLA) {
            errores.add("La sumilla debe tener al menos " + MIN_LONGITUD_SUMILLA + " caracteres");
        }

        // Validar competencias
        if (silabo.getCompetencias() == null || silabo.getCompetencias().trim().isEmpty()) {
            errores.add("Las competencias son obligatorias");
        } else if (silabo.getCompetencias().length() < MIN_LONGITUD_COMPETENCIAS) {
            errores.add("Las competencias deben tener al menos " + MIN_LONGITUD_COMPETENCIAS + " caracteres");
        }

        // Validar metodología
        if (silabo.getMetodologia() == null || silabo.getMetodologia().trim().isEmpty()) {
            errores.add("La metodología es obligatoria");
        } else if (silabo.getMetodologia().length() < MIN_LONGITUD_METODOLOGIA) {
            errores.add("La metodología debe tener al menos " + MIN_LONGITUD_METODOLOGIA + " caracteres");
        }

        // Validar bibliografía
        if (silabo.getBibliografia() == null || silabo.getBibliografia().trim().isEmpty()) {
            errores.add("La bibliografía es obligatoria");
        } else if (silabo.getBibliografia().length() < MIN_LONGITUD_BIBLIOGRAFIA) {
            errores.add("La bibliografía debe tener al menos " + MIN_LONGITUD_BIBLIOGRAFIA + " caracteres");
        }

        // Validar unidades
        if (silabo.getUnidades() == null || silabo.getUnidades().isEmpty()) {
            errores.add("Debe tener al menos " + MIN_NUMERO_UNIDADES + " unidades");
        } else {
            int numUnidades = silabo.getUnidades().size();
            if (numUnidades < MIN_NUMERO_UNIDADES) {
                errores.add("Se requieren al menos " + MIN_NUMERO_UNIDADES + " unidades (tiene " + numUnidades + ")");
            } else if (numUnidades > MAX_NUMERO_UNIDADES) {
                errores.add("Número máximo de unidades es " + MAX_NUMERO_UNIDADES + " (tiene " + numUnidades + ")");
            }

            // Validar cada unidad
            for (int i = 0; i < silabo.getUnidades().size(); i++) {
                SilaboUnidad unidad = silabo.getUnidades().get(i);
                List<String> erroresUnidad = validarUnidad(unidad, i + 1);
                errores.addAll(erroresUnidad);
            }
        }

        return errores;
    }

    /**
     * Valida una unidad específica
     */
    private static List<String> validarUnidad(SilaboUnidad unidad, int numero) {
        List<String> errores = new ArrayList<>();
        String prefijo = "Unidad " + numero + ": ";

        if (unidad.getTitulo() == null || unidad.getTitulo().trim().isEmpty()) {
            errores.add(prefijo + "El título es obligatorio");
        }

        if (unidad.getContenidos() == null || unidad.getContenidos().trim().isEmpty()) {
            errores.add(prefijo + "Los contenidos son obligatorios");
        }

        if (unidad.getActividades() == null || unidad.getActividades().isEmpty()) {
            errores.add(prefijo + "Debe tener al menos " + MIN_ACTIVIDADES_POR_UNIDAD + " actividades");
        } else if (unidad.getActividades().size() < MIN_ACTIVIDADES_POR_UNIDAD) {
            errores.add(prefijo + "Se requieren al menos " + MIN_ACTIVIDADES_POR_UNIDAD + 
                       " actividades (tiene " + unidad.getActividades().size() + ")");
        }

        return errores;
    }

    /**
     * Valida que las ponderaciones de evaluación sumen 100%
     */
    public static List<String> validarPonderaciones(Silabo silabo) {
        List<String> errores = new ArrayList<>();

        if (silabo.getUnidades() == null || silabo.getUnidades().isEmpty()) {
            return errores;
        }

        double sumaPonderaciones = silabo.getUnidades().stream()
            .flatMap(u -> u.getActividades().stream())
            .filter(a -> a.getPonderacion() != null)
            .mapToDouble(a -> a.getPonderacion().doubleValue())
            .sum();

        if (sumaPonderaciones > 0 && Math.abs(sumaPonderaciones - 100.0) > 1.0) {
            errores.add("Las ponderaciones de evaluación deben sumar 100% (suma actual: " + 
                       String.format("%.2f", sumaPonderaciones) + "%)");
        }

        return errores;
    }

    /**
     * Valida que el sílabo esté listo para publicación
     */
    public static List<String> validarListoParaPublicacion(Silabo silabo) {
        List<String> errores = new ArrayList<>();

        // Validaciones de estructura
        errores.addAll(validarEstructuraMinima(silabo));

        // Validaciones de ponderación
        errores.addAll(validarPonderaciones(silabo));

        // El sílabo debe estar aprobado
        if (silabo.getEstado() == null || !silabo.getEstado().equalsIgnoreCase("APROBADO")) {
            errores.add("El sílabo debe estar en estado APROBADO para publicación");
        }

        return errores;
    }

    /**
     * Verifica si un puntaje cumple con el mínimo de aprobación
     */
    public static boolean cumplePuntajeMinimo(Integer puntaje) {
        return puntaje != null && puntaje >= PUNTAJE_MINIMO_APROBACION;
    }

    /**
     * Genera un mensaje de resultado de evaluación
     */
    public static String generarMensajeEvaluacion(Integer puntaje) {
        if (puntaje == null) {
            return "No evaluado";
        }

        if (puntaje >= 90) {
            return "Excelente (" + puntaje + "/100)";
        } else if (puntaje >= PUNTAJE_MINIMO_APROBACION) {
            return "Aprobado (" + puntaje + "/100)";
        } else if (puntaje >= 60) {
            return "Requiere mejoras (" + puntaje + "/100)";
        } else {
            return "Insuficiente (" + puntaje + "/100)";
        }
    }
}
