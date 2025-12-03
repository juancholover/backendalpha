package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.commands.AgregarActividadUnidadCommand;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboActividad;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.entities.SilaboUnidad;
import upeu.edu.pe.academic.domain.repositories.SilaboActividadRepository;
import upeu.edu.pe.academic.domain.repositories.SilaboUnidadRepository;

import java.math.BigDecimal;

/**
 * Caso de uso: Agregar una actividad a una unidad de sílabo
 * 
 * Responsabilidades:
 * - Validar que la unidad exista
 * - Validar que el sílabo sea modificable
 * - Validar que la semana programada esté dentro del rango de la unidad
 * - Validar ponderación para actividades sumativas
 * - Validar que el total de ponderaciones no exceda 100%
 * - Crear y agregar la actividad
 * - Registrar en historial
 */
@ApplicationScoped
public class AgregarActividadUnidadUseCase {
    
    @Inject
    SilaboUnidadRepository silaboUnidadRepository;
    
    @Inject
    SilaboActividadRepository silaboActividadRepository;
    
    @Transactional
    public SilaboActividad execute(AgregarActividadUnidadCommand command, String usuarioCreador) {
        // Buscar la unidad
        SilaboUnidad unidad = silaboUnidadRepository.findByIdOptional(command.unidadId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Unidad no encontrada con ID: " + command.unidadId()
                ));
        
        Silabo silabo = unidad.getSilabo();
        
        // Validar que el sílabo sea modificable
        if (!silabo.esModificable()) {
            throw new IllegalStateException(
                "No se pueden agregar actividades a un sílabo en estado " + silabo.getEstado()
            );
        }
        
        // Validar que la semana esté en el rango de la unidad
        if (!unidad.incluyeSemana(command.semanaProgramada())) {
            throw new IllegalArgumentException(
                "La semana " + command.semanaProgramada() + 
                " no está en el rango de la unidad (semanas " + unidad.getSemanaInicio() + "-" + unidad.getSemanaFin() + ")"
            );
        }
        
        // Validar ponderación para actividades sumativas
        if ("SUMATIVA".equals(command.tipo())) {
            if (command.ponderacion() == null) {
                throw new IllegalArgumentException(
                    "Las actividades sumativas deben tener ponderación"
                );
            }
            if (command.ponderacion().compareTo(BigDecimal.ZERO) <= 0 || 
                command.ponderacion().compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException(
                    "La ponderación debe estar entre 0 y 100"
                );
            }
            
            // Validar que no se exceda el 100%
            BigDecimal totalPonderacion = silaboActividadRepository.sumPonderacionesBySilabo(silabo.getId());
            BigDecimal nuevaTotal = totalPonderacion.add(command.ponderacion());
            if (nuevaTotal.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException(
                    "La suma de ponderaciones excedería el 100%. Actual: " + totalPonderacion + 
                    "%, Intentando agregar: " + command.ponderacion() + "%"
                );
            }
        } else if ("FORMATIVA".equals(command.tipo())) {
            if (command.ponderacion() != null && command.ponderacion().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException(
                    "Las actividades formativas no deben tener ponderación"
                );
            }
        }
        
        // Crear la actividad
        SilaboActividad actividad = new SilaboActividad(
            unidad,
            command.tipo(),
            command.nombre(),
            command.semanaProgramada()
        );
        
        if (command.descripcion() != null) {
            actividad.setDescripcion(command.descripcion());
        }
        if (command.ponderacion() != null) {
            actividad.setPonderacion(command.ponderacion());
        }
        if (command.instrumentoEvaluacion() != null) {
            actividad.setInstrumentoEvaluacion(command.instrumentoEvaluacion());
        }
        if (command.criteriosEvaluacion() != null) {
            actividad.setCriteriosEvaluacion(command.criteriosEvaluacion());
        }
        
        // Persistir
        silaboActividadRepository.persist(actividad);
        
        // Agregar a la colección de la unidad
        unidad.getActividades().add(actividad);
        
        // Registrar en historial
        String tipoStr = "FORMATIVA".equals(command.tipo()) ? "formativa" : "sumativa";
        String comentario = "Agregada actividad " + tipoStr + ": " + command.nombre() + 
                          " (Unidad " + unidad.getNumeroUnidad() + ")";
        
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "AGREGAR_ACTIVIDAD",
            usuarioCreador != null ? usuarioCreador : "SYSTEM",
            comentario
        );
        silabo.getHistorial().add(historial);
        
        return actividad;
    }
}
