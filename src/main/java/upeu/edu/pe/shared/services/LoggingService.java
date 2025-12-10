package upeu.edu.pe.shared.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.shared.entities.SistemaLog;
import upeu.edu.pe.shared.repositories.SistemaLogRepository;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;

/**
 * Servicio para logging centralizado del sistema.
 * Registra eventos, errores, advertencias y auditoría en base de datos.
 */
@ApplicationScoped
public class LoggingService {
    
    private static final Logger LOG = Logger.getLogger(LoggingService.class);
    
    @Inject
    SistemaLogRepository logRepository;
    
    /**
     * Registra un log de nivel INFO
     */
    @Transactional
    public void logInfo(String modulo, String mensaje, String usuario, String contexto) {
        registrarLog("INFO", modulo, mensaje, usuario, contexto, null);
    }
    
    /**
     * Registra un log de nivel WARNING
     */
    @Transactional
    public void logWarning(String modulo, String mensaje, String usuario, String contexto) {
        registrarLog("WARNING", modulo, mensaje, usuario, contexto, null);
    }
    
    /**
     * Registra un log de nivel ERROR
     */
    @Transactional
    public void logError(String modulo, String mensaje, String usuario, String contexto, String ipOrigen) {
        registrarLog("ERROR", modulo, mensaje, usuario, contexto, ipOrigen);
    }
    
    /**
     * Registra un log de nivel CRITICAL
     */
    @Transactional
    public void logCritical(String modulo, String mensaje, String usuario, String contexto, String ipOrigen) {
        registrarLog("CRITICAL", modulo, mensaje, usuario, contexto, ipOrigen);
    }
    
    /**
     * Registra un evento de autenticación
     */
    @Transactional
    public void logAuth(String mensaje, String usuario, String ipOrigen, boolean exitoso) {
        String nivel = exitoso ? "INFO" : "WARNING";
        String contexto = String.format("{\"exitoso\": %s, \"ip\": \"%s\"}", exitoso, ipOrigen);
        registrarLog(nivel, "AUTH", mensaje, usuario, contexto, ipOrigen);
    }
    
    /**
     * Registra un evento de acceso denegado
     */
    @Transactional
    public void logAccessDenied(String usuario, String recurso, String ipOrigen) {
        String mensaje = String.format("Intento de acceso no autorizado al recurso: %s", recurso);
        String contexto = String.format("{\"recurso\": \"%s\", \"usuario\": \"%s\"}", recurso, usuario);
        registrarLog("WARNING", "SEGURIDAD", mensaje, usuario, contexto, ipOrigen);
    }
    
    /**
     * Registra un evento de integración externa (API, servicios)
     */
    @Transactional
    public void logIntegracion(String mensaje, String usuario, boolean exitoso, String detalles) {
        String nivel = exitoso ? "INFO" : "ERROR";
        String contexto = String.format("{\"exitoso\": %s, \"detalles\": \"%s\"}", exitoso, detalles);
        registrarLog(nivel, "INTEGRACION", mensaje, usuario, contexto, null);
    }
    
    /**
     * Registra un evento de backup
     */
    @Transactional
    public void logBackup(String mensaje, String tipo, boolean exitoso, String detalles) {
        String nivel = exitoso ? "INFO" : "ERROR";
        String contexto = String.format("{\"tipo\": \"%s\", \"exitoso\": %s, \"detalles\": \"%s\"}", 
                                       tipo, exitoso, detalles);
        registrarLog(nivel, "BACKUP", mensaje, "SYSTEM", contexto, null);
    }
    
    /**
     * Registra un evento de base de datos
     */
    @Transactional
    public void logDatabase(String mensaje, String nivel, String detalles) {
        String contexto = String.format("{\"detalles\": \"%s\"}", detalles);
        registrarLog(nivel, "DATABASE", mensaje, "SYSTEM", contexto, null);
    }
    
    /**
     * Registra una consulta lenta detectada
     */
    @Transactional
    public void logSlowQuery(String query, long duracionMs) {
        String mensaje = String.format("Consulta lenta detectada (~%dms)", duracionMs);
        String contexto = String.format("{\"query\": \"%s\", \"duracion_ms\": %d}", 
                                       query.substring(0, Math.min(query.length(), 200)), duracionMs);
        registrarLog("WARNING", "DATABASE", mensaje, "SYSTEM", contexto, null);
    }
    
    /**
     * Método privado para registrar log con todos los parámetros
     */
    private void registrarLog(String nivel, String modulo, String mensaje, String usuario, 
                             String contexto, String ipOrigen) {
        try {
            SistemaLog log = new SistemaLog();
            log.setFechaHora(LocalDateTime.now());
            log.setNivel(nivel);
            log.setModulo(modulo);
            log.setMensaje(mensaje);
            log.setUsuario(usuario);
            log.setContexto(contexto);
            log.setIpOrigen(ipOrigen);
            log.setActive(true);
            
            logRepository.persist(log);
            
            // También logear en consola para desarrollo
            switch (nivel) {
                case "CRITICAL" -> LOG.fatal(String.format("[%s] %s - %s", modulo, usuario, mensaje));
                case "ERROR" -> LOG.error(String.format("[%s] %s - %s", modulo, usuario, mensaje));
                case "WARNING" -> LOG.warn(String.format("[%s] %s - %s", modulo, usuario, mensaje));
                default -> LOG.info(String.format("[%s] %s - %s", modulo, usuario, mensaje));
            }
            
        } catch (Exception e) {
            // Si falla el logging en BD, al menos logear en consola
            LOG.error("Error al registrar log en base de datos: " + e.getMessage(), e);
        }
    }
}
