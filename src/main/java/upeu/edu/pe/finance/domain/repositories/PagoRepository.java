package upeu.edu.pe.finance.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.Pago;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PagoRepository implements PanacheRepository<Pago> {

    /**
     * Busca pagos por estudiante
     */
    public List<Pago> findByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and active = true ORDER BY fechaPago DESC", 
                   estudianteId).list();
    }

    /**
     * Busca pagos por universidad
     */
    public List<Pago> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true ORDER BY fechaPago DESC", 
                   universidadId).list();
    }

    /**
     * Busca pago por número de recibo
     */
    public Optional<Pago> findByNumeroRecibo(String numeroRecibo, Long universidadId) {
        return find("UPPER(numeroRecibo) = UPPER(?1) and universidad.id = ?2 and active = true", 
                   numeroRecibo, universidadId).firstResultOptional();
    }

    /**
     * Verifica si existe un número de recibo
     */
    public boolean existsByNumeroRecibo(String numeroRecibo, Long universidadId) {
        return count("UPPER(numeroRecibo) = UPPER(?1) and universidad.id = ?2", 
                    numeroRecibo, universidadId) > 0;
    }

    /**
     * Busca pagos pendientes de aplicar
     */
    public List<Pago> findPendientesAplicarByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and UPPER(estado) = 'PENDIENTE_APLICAR' and montoPendienteAplicar > 0 and active = true ORDER BY fechaPago", 
                   universidadId).list();
    }

    /**
     * Busca pagos por método de pago
     */
    public List<Pago> findByMetodoPagoAndUniversidad(String metodoPago, Long universidadId) {
        return find("UPPER(metodoPago) = UPPER(?1) and universidad.id = ?2 and active = true ORDER BY fechaPago DESC", 
                   metodoPago, universidadId).list();
    }

    /**
     * Busca pagos por estado
     */
    public List<Pago> findByEstadoAndUniversidad(String estado, Long universidadId) {
        return find("UPPER(estado) = UPPER(?1) and universidad.id = ?2 and active = true ORDER BY fechaPago DESC", 
                   estado, universidadId).list();
    }

    /**
     * Busca pagos por rango de fechas
     */
    public List<Pago> findByFechasAndUniversidad(LocalDate fechaInicio, LocalDate fechaFin, Long universidadId) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();
        return find("universidad.id = ?1 and fechaPago >= ?2 and fechaPago < ?3 and active = true ORDER BY fechaPago", 
                   universidadId, inicio, fin).list();
    }

    /**
     * Busca pagos por cajero
     */
    public List<Pago> findByCajeroAndFecha(String cajero, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return find("UPPER(cajero) = UPPER(?1) and fechaPago >= ?2 and fechaPago < ?3 and active = true ORDER BY fechaPago", 
                   cajero, inicio, fin).list();
    }

    /**
     * Calcula total de pagos por fecha
     */
    public BigDecimal calcularTotalPagosByFecha(LocalDate fecha, Long universidadId) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        Object result = find("SELECT COALESCE(SUM(montoPagado), 0) FROM Pago " +
                            "WHERE universidad.id = ?1 and fechaPago >= ?2 and fechaPago < ?3 and UPPER(estado) != 'ANULADO' and active = true", 
                            universidadId, inicio, fin).project(BigDecimal.class).firstResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    /**
     * Busca pagos por referencia
     */
    public List<Pago> findByReferencia(String referenciaPago, Long universidadId) {
        return find("UPPER(referenciaPago) LIKE UPPER(?1) and universidad.id = ?2 and active = true", 
                   "%" + referenciaPago + "%", universidadId).list();
    }

    /**
     * Cuenta pagos de un estudiante
     */
    public long countByEstudiante(Long estudianteId) {
        return count("estudiante.id = ?1 and active = true", estudianteId);
    }

    /**
     * Busca pagos anulados
     */
    public List<Pago> findAnuladosByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and UPPER(estado) = 'ANULADO' and active = true ORDER BY fechaAnulacion DESC", 
                   universidadId).list();
    }

    /**
     * Busca pagos por estado (sin filtro de universidad)
     */
    public List<Pago> findByEstado(String estado) {
        return find("UPPER(estado) = UPPER(?1) and active = true ORDER BY fechaPago DESC", estado).list();
    }

    /**
     * Busca pagos por método de pago (sin filtro de universidad)
     */
    public List<Pago> findByMetodoPago(String metodoPago) {
        return find("UPPER(metodoPago) = UPPER(?1) and active = true ORDER BY fechaPago DESC", metodoPago).list();
    }

    /**
     * Busca pagos por fecha específica
     */
    public List<Pago> findByFecha(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        return find("fechaPago >= ?1 and fechaPago < ?2 and active = true ORDER BY fechaPago", inicio, fin).list();
    }

    /**
     * Busca pagos por cajero
     */
    public List<Pago> findByCajero(String cajero) {
        return find("UPPER(cajero) = UPPER(?1) and active = true ORDER BY fechaPago DESC", cajero).list();
    }

    /**
     * Busca pagos pendientes de aplicar por estudiante
     */
    public List<Pago> findPendientesAplicarByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and UPPER(estado) = 'PENDIENTE_APLICAR' and montoPendienteAplicar > 0 and active = true ORDER BY fechaPago", 
                   estudianteId).list();
    }

    /**
     * Busca pagos por rango de fechas (sin universidad)
     */
    public List<Pago> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();
        return find("fechaPago >= ?1 and fechaPago < ?2 and active = true ORDER BY fechaPago", inicio, fin).list();
    }

    /**
     * Calcula total de pagos por método y fecha
     */
    public BigDecimal calcularTotalPagosByMetodo(String metodoPago, LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        Object result = find("SELECT COALESCE(SUM(montoPagado), 0) FROM Pago " +
                            "WHERE UPPER(metodoPago) = UPPER(?1) and fechaPago >= ?2 and fechaPago < ?3 and UPPER(estado) != 'ANULADO' and active = true", 
                            metodoPago, inicio, fin).project(BigDecimal.class).firstResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    /**
     * Cuenta pagos por estado y universidad
     */
    public long countByEstadoAndUniversidad(String estado, Long universidadId) {
        return count("UPPER(estado) = UPPER(?1) and universidad.id = ?2 and active = true", estado, universidadId);
    }
}
