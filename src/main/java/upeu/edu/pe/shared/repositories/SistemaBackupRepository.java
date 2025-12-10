package upeu.edu.pe.shared.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.shared.entities.SistemaBackup;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para gestión de backups del sistema.
 */
@ApplicationScoped
public class SistemaBackupRepository implements PanacheRepository<SistemaBackup> {

    /**
     * Busca todos los backups activos ordenados por fecha descendente
     */
    public List<SistemaBackup> findAllActive() {
        return find("active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca backups por tipo (AUTOMATICO, MANUAL)
     */
    public List<SistemaBackup> findByTipo(String tipo) {
        return find("tipo = ?1 and active = true ORDER BY fechaHora DESC", tipo).list();
    }

    /**
     * Busca backups por estado (EXITOSO, ERROR)
     */
    public List<SistemaBackup> findByEstado(String estado) {
        return find("estado = ?1 and active = true ORDER BY fechaHora DESC", estado).list();
    }

    /**
     * Busca backups exitosos
     */
    public List<SistemaBackup> findExitosos() {
        return find("estado = 'EXITOSO' and active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca backups con error
     */
    public List<SistemaBackup> findConError() {
        return find("estado = 'ERROR' and active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca backups en un rango de fechas
     */
    public List<SistemaBackup> findByRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return find("fechaHora >= ?1 and fechaHora <= ?2 and active = true ORDER BY fechaHora DESC", 
                   inicio, fin).list();
    }

    /**
     * Busca backups por usuario solicitante (solo MANUAL)
     */
    public List<SistemaBackup> findByUsuario(String usuario) {
        return find("usuarioSolicitante = ?1 and tipo = 'MANUAL' and active = true ORDER BY fechaHora DESC", 
                   usuario).list();
    }

    /**
     * Obtiene el último backup exitoso
     */
    public SistemaBackup findUltimoExitoso() {
        return find("estado = 'EXITOSO' and active = true ORDER BY fechaHora DESC")
                .firstResult();
    }

    /**
     * Cuenta backups exitosos en un período
     */
    public long countExitososEnPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return count("estado = 'EXITOSO' and fechaHora >= ?1 and fechaHora <= ?2 and active = true", 
                    inicio, fin);
    }

    /**
     * Cuenta backups con error en un período
     */
    public long countErroresEnPeriodo(LocalDateTime inicio, LocalDateTime fin) {
        return count("estado = 'ERROR' and fechaHora >= ?1 and fechaHora <= ?2 and active = true", 
                    inicio, fin);
    }

    /**
     * Calcula el espacio total usado por backups exitosos (en GB)
     */
    public Double calcularEspacioTotalUsado() {
        return find("SELECT COALESCE(SUM(b.tamanoGb), 0) FROM SistemaBackup b WHERE b.estado = 'EXITOSO' and b.active = true")
                .project(Double.class)
                .firstResult();
    }

    /**
     * Busca backups recientes (últimas 24 horas)
     */
    public List<SistemaBackup> findRecientes() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusDays(1);
        return find("fechaHora >= ?1 and active = true ORDER BY fechaHora DESC", hace24Horas).list();
    }
}
