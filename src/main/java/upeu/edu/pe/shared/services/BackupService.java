package upeu.edu.pe.shared.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.shared.entities.SistemaBackup;
import upeu.edu.pe.shared.repositories.SistemaBackupRepository;
import upeu.edu.pe.shared.dto.BackupEstadisticasDTO;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de backups del sistema.
 * Coordina la ejecución y registro de backups automáticos y manuales.
 */
@ApplicationScoped
public class BackupService {
    
    private static final Logger LOG = Logger.getLogger(BackupService.class);
    
    @Inject
    SistemaBackupRepository backupRepository;
    
    @Inject
    LoggingService loggingService;
    
    /**
     * Registra el inicio de un backup
     */
    @Transactional
    public SistemaBackup iniciarBackup(String tipo, String usuarioSolicitante) {
        SistemaBackup backup = new SistemaBackup();
        backup.setFechaHora(LocalDateTime.now());
        backup.setTipo(tipo);
        backup.setEstado("EN_PROGRESO");
        backup.setUsuarioSolicitante(usuarioSolicitante);
        backup.setActive(true);
        
        backupRepository.persist(backup);
        
        loggingService.logInfo("BACKUP", 
            String.format("Backup %s iniciado", tipo), 
            usuarioSolicitante != null ? usuarioSolicitante : "SYSTEM", 
            String.format("{\"backup_id\": %d, \"tipo\": \"%s\"}", backup.getIdBackup(), tipo));
        
        return backup;
    }
    
    /**
     * Registra la finalización exitosa de un backup
     */
    @Transactional
    public void finalizarBackupExitoso(Long backupId, BigDecimal tamanoGb, Integer registrosProcesados, 
                                       String archivoDestino) {
        SistemaBackup backup = backupRepository.findById(backupId);
        if (backup != null) {
            LocalDateTime ahora = LocalDateTime.now();
            long duracionMinutos = Duration.between(backup.getFechaHora(), ahora).toMinutes();
            
            backup.setEstado("EXITOSO");
            backup.setTamanoGb(tamanoGb);
            backup.setDuracionMinuto((int) duracionMinutos);
            backup.setRegistrosProcesados(registrosProcesados);
            backup.setArchivoDestino(archivoDestino);
            
            loggingService.logBackup(
                String.format("Backup completado exitosamente (%.2f GB, %d min)", 
                             tamanoGb, duracionMinutos),
                backup.getTipo(),
                true,
                String.format("%,d registros procesados", registrosProcesados)
            );
        }
    }
    
    /**
     * Registra la finalización fallida de un backup
     */
    @Transactional
    public void finalizarBackupConError(Long backupId, String observaciones) {
        SistemaBackup backup = backupRepository.findById(backupId);
        if (backup != null) {
            LocalDateTime ahora = LocalDateTime.now();
            long duracionMinutos = Duration.between(backup.getFechaHora(), ahora).toMinutes();
            
            backup.setEstado("ERROR");
            backup.setDuracionMinuto((int) duracionMinutos);
            backup.setObservaciones(observaciones);
            
            loggingService.logBackup(
                String.format("Fallo en backup (%d min): %s", duracionMinutos, observaciones),
                backup.getTipo(),
                false,
                observaciones
            );
        }
    }
    
    /**
     * Simula la ejecución de un backup automático (para pruebas)
     */
    @Transactional
    public SistemaBackup ejecutarBackupAutomatico() {
        LOG.info("Iniciando backup automático del sistema...");
        
        SistemaBackup backup = iniciarBackup("AUTOMATICO", null);
        
        try {
            // Aquí iría la lógica real de backup (pg_dump, S3 upload, etc.)
            // Por ahora simulamos con valores de ejemplo
            
            Thread.sleep(2000); // Simular proceso de backup
            
            BigDecimal tamano = new BigDecimal("2.35");
            Integer registros = 1_234_567;
            String archivo = String.format("backup_%s_%s.sql.gz", 
                LocalDateTime.now().toLocalDate(), 
                backup.getIdBackup());
            
            finalizarBackupExitoso(backup.getIdBackup(), tamano, registros, archivo);
            
            LOG.info("Backup automático completado exitosamente");
            
        } catch (Exception e) {
            LOG.error("Error en backup automático: " + e.getMessage(), e);
            finalizarBackupConError(backup.getIdBackup(), 
                "Error en conexión con API externa REINEC: " + e.getMessage());
        }
        
        return backupRepository.findById(backup.getIdBackup());
    }
    
    /**
     * Solicita un backup manual
     */
    @Transactional
    public SistemaBackup solicitarBackupManual(String usuarioSolicitante) {
        LOG.info(String.format("Backup manual solicitado por %s", usuarioSolicitante));
        
        SistemaBackup backup = iniciarBackup("MANUAL", usuarioSolicitante);
        
        try {
            // Aquí iría la lógica real de backup manual
            Thread.sleep(3000); // Simular proceso más largo para backup manual
            
            BigDecimal tamano = new BigDecimal("2.38");
            Integer registros = 1_238_045;
            String archivo = String.format("backup_manual_%s_admin_it.sql.gz", 
                LocalDateTime.now().toLocalDate());
            
            finalizarBackupExitoso(backup.getIdBackup(), tamano, registros, archivo);
            
            LOG.info("Backup manual completado exitosamente");
            
        } catch (Exception e) {
            LOG.error("Error en backup manual: " + e.getMessage(), e);
            finalizarBackupConError(backup.getIdBackup(), e.getMessage());
        }
        
        return backupRepository.findById(backup.getIdBackup());
    }
    
    /**
     * Obtiene estadísticas de backups
     */
    public BackupEstadisticasDTO obtenerEstadisticas() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime finMes = LocalDateTime.now();
        
        long totalBackups = backupRepository.count("active = true");
        long exitosos = backupRepository.countExitososEnPeriodo(inicioMes, finMes);
        long errores = backupRepository.countErroresEnPeriodo(inicioMes, finMes);
        Double espacioTotal = backupRepository.calcularEspacioTotalUsado();
        SistemaBackup ultimoExitoso = backupRepository.findUltimoExitoso();
        
        return new BackupEstadisticasDTO(
            (int) totalBackups,
            (int) exitosos,
            (int) errores,
            espacioTotal,
            ultimoExitoso != null ? ultimoExitoso.getFechaHora() : null
        );
    }
    
    /**
     * Lista todos los backups activos
     */
    public List<SistemaBackup> listarBackups() {
        return backupRepository.findAllActive();
    }
    
    /**
     * Obtiene detalles de un backup específico
     */
    public SistemaBackup obtenerBackup(Long id) {
        return backupRepository.findById(id);
    }
}
