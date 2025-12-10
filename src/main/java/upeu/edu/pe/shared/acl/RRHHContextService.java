package upeu.edu.pe.shared.acl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.people.domain.repositories.EmpleadoRepository;
import upeu.edu.pe.people.domain.entities.Empleado;
import java.time.LocalDate;

/**
 * Anti-Corruption Layer (ACL): Protege el Contexto RRHH de consultas directas.
 * El Contexto Académico NO debe hacer JOIN directo a tabla Empleado.
 * Usa este servicio para verificar disponibilidad de empleados.
 */
@ApplicationScoped
public class RRHHContextService {
    
    @Inject
    EmpleadoRepository empleadoRepository;
    
    /**
     * Verifica si un empleado está activo (sin fecha de cese).
     * El Contexto Académico no conoce la estructura interna de Empleado.
     * 
     * @param empleadoId ID del empleado en RRHH
     * @return true si el empleado está activo y puede ser asignado como profesor
     */
    public boolean empleadoEstaActivo(Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId);
        
        if (empleado == null) {
            return false;
        }
        
        // Lógica de RRHH: activo si no tiene fecha de cese o si es futura
        return empleado.getFechaCese() == null || 
               empleado.getFechaCese().isAfter(LocalDate.now());
    }
    
    /**
     * Obtiene el nombre completo del empleado sin exponer la entidad Persona.
     * Protege la estructura interna del Contexto RRHH.
     * 
     * @param empleadoId ID del empleado
     * @return Nombre completo formateado (ej: "García López, Juan Carlos")
     */
    public String obtenerNombreCompleto(Long empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId);
        
        if (empleado == null || empleado.getPersona() == null) {
            return "Empleado desconocido";
        }
        
        var persona = empleado.getPersona();
        return String.format("%s %s, %s", 
            persona.getApellidoPaterno(),
            persona.getApellidoMaterno(),
            persona.getNombres()
        );
    }
}
