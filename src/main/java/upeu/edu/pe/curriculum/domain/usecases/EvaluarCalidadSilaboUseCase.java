package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import upeu.edu.pe.curriculum.domain.commands.EvaluarCalidadSilaboCommand;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboCalidad;
import upeu.edu.pe.curriculum.domain.exceptions.CalidadInsuficienteException;
import upeu.edu.pe.curriculum.domain.services.SilaboCalidadService;
import upeu.edu.pe.curriculum.domain.repositories.SilaboRepository;

/**
 * Caso de uso para evaluar la calidad de un sílabo
 */
@ApplicationScoped
@Slf4j
public class EvaluarCalidadSilaboUseCase {

    @Inject
    SilaboCalidadService calidadService;

    @Inject
    SilaboRepository silaboRepository;

    /**
     * Ejecuta la evaluación de calidad de un sílabo
     */
    @Transactional
    public SilaboCalidad execute(EvaluarCalidadSilaboCommand command) {
        log.info("🔍 Ejecutando evaluación de calidad para sílabo ID: {}", command.getSilaboId());

        // Validar que el sílabo exista
        Silabo silabo = silaboRepository.findById(command.getSilaboId());
        if (silabo == null) {
            throw new IllegalArgumentException("Sílabo no encontrado con ID: " + command.getSilaboId());
        }

        // Verificar si ya tiene evaluación aprobada (opcional: solo si no se fuerza reevaluación)
        if (!Boolean.TRUE.equals(command.getForzarReevaluacion())) {
            SilaboCalidad evaluacionExistente = calidadService.obtenerUltimaEvaluacion(command.getSilaboId());
            if (evaluacionExistente != null && evaluacionExistente.getAprobado()) {
                log.info("✅ Sílabo ya tiene evaluación aprobada. Retornando evaluación existente.");
                return evaluacionExistente;
            }
        }

        // Evaluar calidad
        SilaboCalidad calidad = calidadService.evaluarCalidad(silabo, command.getEvaluadoPor());

        log.info("📊 Evaluación completada: Puntaje = {}/100, Aprobado = {}", 
            calidad.getPuntajeTotal(), calidad.getAprobado());

        return calidad;
    }

    /**
     * Ejecuta la evaluación y lanza excepción si no cumple el estándar
     */
    @Transactional
    public SilaboCalidad executeOrThrow(EvaluarCalidadSilaboCommand command) {
        SilaboCalidad calidad = execute(command);
        
        if (!calidad.getAprobado()) {
            throw new CalidadInsuficienteException(
                command.getSilaboId(), 
                calidad.getPuntajeTotal(), 
                80
            );
        }
        
        return calidad;
    }
}
