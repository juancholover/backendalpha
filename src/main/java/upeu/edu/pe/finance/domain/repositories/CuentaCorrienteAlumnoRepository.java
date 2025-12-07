package upeu.edu.pe.finance.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CuentaCorrienteAlumnoRepository implements PanacheRepository<CuentaCorrienteAlumno> {

    /**
     * Busca cuentas por estudiante
     */
    public List<CuentaCorrienteAlumno> findByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and active = true ORDER BY fechaEmision DESC", 
                   estudianteId).list();
    }

    /**
     * Busca todas las cuentas activas
     */
    public List<CuentaCorrienteAlumno> findAllActive() {
        return find("active = true ORDER BY fechaEmision DESC").list();
    }

    /**
     * Busca cuentas pendientes de pago
     */
    public List<CuentaCorrienteAlumno> findPendientesByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and UPPER(estado) IN ('PENDIENTE', 'PAGADO_PARCIAL') and active = true ORDER BY fechaVencimiento", 
                   estudianteId).list();
    }

    /**
     * Busca cuentas vencidas
     */
    public List<CuentaCorrienteAlumno> findVencidasByEstudiante(Long estudianteId) {
        LocalDate hoy = LocalDate.now();
        return find("estudiante.id = ?1 and fechaVencimiento < ?2 and UPPER(estado) IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') and active = true ORDER BY fechaVencimiento", 
                   estudianteId, hoy).list();
    }

    /**
     * Busca cuentas por tipo de cargo
     */
    public List<CuentaCorrienteAlumno> findByTipoCargoAndEstudiante(String tipoCargo, Long estudianteId) {
        return find("UPPER(tipoCargo) = UPPER(?1) and estudiante.id = ?2 and active = true ORDER BY fechaEmision DESC", 
                   tipoCargo, estudianteId).list();
    }

    /**
     * Busca cuentas por estado activas
     */
    public List<CuentaCorrienteAlumno> findByEstadoActive(String estado) {
        return find("UPPER(estado) = UPPER(?1) and active = true ORDER BY fechaEmision DESC", 
                   estado).list();
    }

    /**
     * Calcula deuda total de un estudiante
     */
    public BigDecimal calcularDeudaTotalByEstudiante(Long estudianteId) {
        Object result = find("SELECT COALESCE(SUM(montoPendiente), 0) FROM CuentaCorrienteAlumno " +
                            "WHERE estudiante.id = ?1 and UPPER(estado) IN ('PENDIENTE', 'PAGADO_PARCIAL', 'VENCIDO') and active = true", 
                            estudianteId).project(BigDecimal.class).firstResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }

    /**
     * Busca cuenta por número de documento
     */
    public Optional<CuentaCorrienteAlumno> findByNumeroDocumento(String numeroDocumento) {
        return find("UPPER(numeroDocumento) = UPPER(?1) and active = true", 
                   numeroDocumento).firstResultOptional();
    }

    /**
     * Verifica si existe un documento
     */
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return count("UPPER(numeroDocumento) = UPPER(?1) and active = true", 
                    numeroDocumento) > 0;
    }

    /**
     * Busca cuentas por rango de fechas
     */
    public List<CuentaCorrienteAlumno> findByFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return find("fechaEmision BETWEEN ?1 and ?2 and active = true ORDER BY fechaEmision", 
                   fechaInicio, fechaFin).list();
    }

    /**
     * Cuenta cuentas pendientes de un estudiante
     */
    public long countPendientesByEstudiante(Long estudianteId) {
        return count("estudiante.id = ?1 and UPPER(estado) IN ('PENDIENTE', 'PAGADO_PARCIAL')", estudianteId);
    }

    /**
     * Busca cuentas próximas a vencer
     */
    public List<CuentaCorrienteAlumno> findProximasVencerByEstudiante(Long estudianteId, Integer dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        return find("estudiante.id = ?1 and fechaVencimiento <= ?2 and UPPER(estado) IN ('PENDIENTE', 'PAGADO_PARCIAL') and active = true ORDER BY fechaVencimiento", 
                   estudianteId, fechaLimite).list();
    }

    /**
     * Busca cuentas por tipo de cargo (sin filtro de estudiante)
     */
    public List<CuentaCorrienteAlumno> findByTipoCargo(String tipoCargo) {
        return find("UPPER(tipoCargo) = UPPER(?1) and active = true ORDER BY fechaEmision DESC", tipoCargo).list();
    }

    /**
     * Busca cuentas por estado (sin filtro de universidad)
     */
    public List<CuentaCorrienteAlumno> findByEstado(String estado) {
        return find("UPPER(estado) = UPPER(?1) and active = true ORDER BY fechaEmision DESC", estado).list();
    }

    /**
     * Cuenta cuentas por estado
     */
    public long countByEstadoActive(String estado) {
        return count("UPPER(estado) = UPPER(?1) and active = true", estado);
    }

    /**
     * Verifica si existe una cuenta para estudiante y concepto
     */
    public boolean existsByEstudianteAndConcepto(Long estudianteId, String concepto) {
        return count("estudiante.id = ?1 and UPPER(concepto) = UPPER(?2) and active = true", estudianteId, concepto) > 0;
    }
}
