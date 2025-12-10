package upeu.edu.pe.shared.handlers;

import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.shared.events.EmpleadoDadoDeBajaEvent;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.people.domain.entities.Profesor;
import org.jboss.logging.Logger;

/**
 * Event Handler: Reacciona cuando un empleado es dado de baja en RRHH.
 * Desvincula automáticamente al profesor de cursos futuros.
 */
@ApplicationScoped
public class EmpleadoEventHandler {
    
    private static final Logger LOG = Logger.getLogger(EmpleadoEventHandler.class);
    
    @Inject
    ProfesorRepository profesorRepository;
    
    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;
    
    /**
     * Escucha el evento "empleado.dado-de-baja" publicado desde el contexto RRHH.
     * No hace JOIN directo a tabla Empleado.
     */
    @ConsumeEvent("empleado.dado-de-baja")
    @Transactional
    public void onEmpleadoDadoDeBaja(EmpleadoDadoDeBajaEvent event) {
        LOG.infof("📩 Evento recibido: EmpleadoDadoDeBaja[empleadoId=%d, motivo=%s, fechaCese=%s]",
            event.getEmpleadoId(), event.getMotivo(), event.getFechaCese());
        
        // Buscar profesor por empleadoId (sin JOIN directo a RRHH)
        var profesorOpt = profesorRepository.findByEmpleadoId(event.getEmpleadoId());
        
        if (profesorOpt.isPresent()) {
            Profesor profesor = profesorOpt.get();
            
            // Desvincular de cursos futuros (no afecta cursos ya iniciados)
            int cursosAfectados = cursoOfertadoRepository.desvincularProfesor(
                profesor.getId(), 
                event.getFechaCese()
            );
            
            // Marcar profesor como inactivo
            profesor.setActive(false);
            profesorRepository.persist(profesor);
            
            LOG.infof("✅ Profesor[id=%d] desvinculado de %d cursos futuros. Motivo: %s",
                profesor.getId(), cursosAfectados, event.getMotivo());
        } else {
            LOG.warnf("⚠️  No se encontró profesor asociado a empleadoId=%d", event.getEmpleadoId());
        }
    }
}
