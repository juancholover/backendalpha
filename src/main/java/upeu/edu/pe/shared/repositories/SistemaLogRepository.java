package upeu.edu.pe.shared.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.shared.entities.SistemaLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para gestión de logs del sistema.
 */
@ApplicationScoped
public class SistemaLogRepository implements PanacheRepository<SistemaLog> {

    /**
     * Busca todos los logs activos ordenados por fecha descendente
     */
    public List<SistemaLog> findAllActive() {
        return find("active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca logs por nivel (INFO, WARNING, ERROR, CRITICAL)
     */
    public List<SistemaLog> findByNivel(String nivel) {
        return find("nivel = ?1 and active = true ORDER BY fechaHora DESC", nivel).list();
    }

    /**
     * Busca logs por módulo
     */
    public List<SistemaLog> findByModulo(String modulo) {
        return find("modulo = ?1 and active = true ORDER BY fechaHora DESC", modulo).list();
    }

    /**
     * Busca logs por usuario
     */
    public List<SistemaLog> findByUsuario(String usuario) {
        return find("usuario = ?1 and active = true ORDER BY fechaHora DESC", usuario).list();
    }

    /**
     * Busca logs en un rango de fechas
     */
    public List<SistemaLog> findByRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return find("fechaHora >= ?1 and fechaHora <= ?2 and active = true ORDER BY fechaHora DESC", 
                   inicio, fin).list();
    }

    /**
     * Busca logs de errores (ERROR + CRITICAL)
     */
    public List<SistemaLog> findErrores() {
        return find("(nivel = 'ERROR' or nivel = 'CRITICAL') and active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca logs críticos
     */
    public List<SistemaLog> findCriticos() {
        return find("nivel = 'CRITICAL' and active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca logs de advertencias
     */
    public List<SistemaLog> findWarnings() {
        return find("nivel = 'WARNING' and active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca logs informativos
     */
    public List<SistemaLog> findInfo() {
        return find("nivel = 'INFO' and active = true ORDER BY fechaHora DESC").list();
    }

    /**
     * Busca logs recientes (últimas 24 horas)
     */
    public List<SistemaLog> findRecientes() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusDays(1);
        return find("fechaHora >= ?1 and active = true ORDER BY fechaHora DESC", hace24Horas).list();
    }

    /**
     * Cuenta logs por nivel en un período
     */
    public long countByNivelEnPeriodo(String nivel, LocalDateTime inicio, LocalDateTime fin) {
        return count("nivel = ?1 and fechaHora >= ?2 and fechaHora <= ?3 and active = true", 
                    nivel, inicio, fin);
    }

    /**
     * Cuenta errores en las últimas 24 horas
     */
    public long countErroresRecientes() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusDays(1);
        return count("(nivel = 'ERROR' or nivel = 'CRITICAL') and fechaHora >= ?1 and active = true", 
                    hace24Horas);
    }

    /**
     * Obtiene estadísticas de logs por nivel
     */
    public List<Object[]> getEstadisticasPorNivel() {
        return getEntityManager()
            .createQuery("SELECT l.nivel, COUNT(l) FROM SistemaLog l WHERE l.active = true GROUP BY l.nivel", Object[].class)
            .getResultList();
    }

    /**
     * Busca logs por texto en mensaje (búsqueda parcial)
     */
    public List<SistemaLog> searchByMensaje(String texto) {
        return find("LOWER(mensaje) LIKE LOWER(?1) and active = true ORDER BY fechaHora DESC", 
                   "%" + texto + "%").list();
    }
}
