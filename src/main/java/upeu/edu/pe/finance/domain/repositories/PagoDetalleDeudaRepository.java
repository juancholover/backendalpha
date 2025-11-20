package upeu.edu.pe.finance.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PagoDetalleDeudaRepository implements PanacheRepository<PagoDetalleDeuda> {

    /**
     * Busca detalles por pago
     */
    public List<PagoDetalleDeuda> findByPago(Long pagoId) {
        return find("pago.id = ?1 and active = true ORDER BY fechaAplicacion", pagoId).list();
    }

    /**
     * Busca detalles por deuda
     */
    public List<PagoDetalleDeuda> findByDeuda(Long deudaId) {
        return find("deuda.id = ?1 and active = true ORDER BY fechaAplicacion", deudaId).list();
    }

    /**
     * Busca detalles activos por pago
     */
    public List<PagoDetalleDeuda> findActivosByPago(Long pagoId) {
        return find("pago.id = ?1 and UPPER(estado) = 'APLICADO' and active = true ORDER BY fechaAplicacion", 
                   pagoId).list();
    }

    /**
     * Busca detalles activos por deuda
     */
    public List<PagoDetalleDeuda> findActivosByDeuda(Long deudaId) {
        return find("deuda.id = ?1 and UPPER(estado) = 'APLICADO' and active = true ORDER BY fechaAplicacion", 
                   deudaId).list();
    }

    /**
     * Busca un detalle específico
     */
    public Optional<PagoDetalleDeuda> findByPagoAndDeuda(Long pagoId, Long deudaId) {
        return find("pago.id = ?1 and deuda.id = ?2 and UPPER(estado) = 'APLICADO' and active = true", 
                   pagoId, deudaId).firstResultOptional();
    }

    /**
     * Calcula total aplicado a una deuda
     */
    public BigDecimal calcularTotalAplicadoByDeuda(Long deudaId) {
        Object result = find("SELECT COALESCE(SUM(montoAplicado), 0) FROM PagoDetalleDeuda " +
                            "WHERE deuda.id = ?1 and UPPER(estado) = 'APLICADO' and active = true", 
                            deudaId).project(BigDecimal.class).firstResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    /**
     * Calcula total aplicado desde un pago
     */
    public BigDecimal calcularTotalAplicadoByPago(Long pagoId) {
        Object result = find("SELECT COALESCE(SUM(montoAplicado), 0) FROM PagoDetalleDeuda " +
                            "WHERE pago.id = ?1 and UPPER(estado) = 'APLICADO' and active = true", 
                            pagoId).project(BigDecimal.class).firstResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    /**
     * Busca detalles revertidos por pago
     */
    public List<PagoDetalleDeuda> findRevertidosByPago(Long pagoId) {
        return find("pago.id = ?1 and UPPER(estado) = 'REVERTIDO' and active = true ORDER BY fechaReversion DESC", 
                   pagoId).list();
    }

    /**
     * Busca detalles revertidos por deuda
     */
    public List<PagoDetalleDeuda> findRevertidosByDeuda(Long deudaId) {
        return find("deuda.id = ?1 and UPPER(estado) = 'REVERTIDO' and active = true ORDER BY fechaReversion DESC", 
                   deudaId).list();
    }

    /**
     * Cuenta detalles aplicados a una deuda
     */
    public long countActivosByDeuda(Long deudaId) {
        return count("deuda.id = ?1 and UPPER(estado) = 'APLICADO'", deudaId);
    }

    /**
     * Verifica si existe aplicación entre pago y deuda
     */
    public boolean existsAplicacion(Long pagoId, Long deudaId) {
        return count("pago.id = ?1 and deuda.id = ?2 and UPPER(estado) = 'APLICADO'", pagoId, deudaId) > 0;
    }

    /**
     * Busca detalles por usuario que aplicó
     */
    public List<PagoDetalleDeuda> findByAplicadoPor(String aplicadoPor) {
        return find("UPPER(aplicadoPor) = UPPER(?1) and active = true ORDER BY fechaAplicacion DESC", 
                   aplicadoPor).list();
    }

    /**
     * Verifica si existe relación entre pago y deuda (alias de existsAplicacion)
     */
    public boolean existsByPagoAndDeuda(Long pagoId, Long deudaId) {
        return existsAplicacion(pagoId, deudaId);
    }

    /**
     * Cuenta aplicaciones activas por pago
     */
    public long countByPago(Long pagoId) {
        return count("pago.id = ?1 and UPPER(estado) = 'APLICADO' and active = true", pagoId);
    }

    /**
     * Cuenta aplicaciones activas por deuda
     */
    public long countByDeuda(Long deudaId) {
        return count("deuda.id = ?1 and UPPER(estado) = 'APLICADO' and active = true", deudaId);
    }
}
