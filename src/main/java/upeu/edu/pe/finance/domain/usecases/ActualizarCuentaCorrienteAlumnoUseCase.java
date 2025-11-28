package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

@ApplicationScoped
public class ActualizarCuentaCorrienteAlumnoUseCase {

    private final CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;

    @Inject
    public ActualizarCuentaCorrienteAlumnoUseCase(CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository) {
        this.cuentaCorrienteAlumnoRepository = cuentaCorrienteAlumnoRepository;
    }

    @Transactional
    public CuentaCorrienteAlumno execute(Long id, BigDecimal monto, String concepto, LocalDate fechaVencimiento,
            String tipoCargo, String periodoAcademico, Integer numeroCuota, String observaciones) {

        CuentaCorrienteAlumno cta = cuentaCorrienteAlumnoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CuentaCorrienteAlumno not found with id " + id));

        if (cta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0 && monto.compareTo(cta.getMontoPagado()) < 0) {
            throw new IllegalArgumentException("El nuevo monto no puede ser menor al monto ya pagado");
        }

        cta.setMonto(monto);
        cta.setConcepto(concepto);
        cta.setFechaVencimiento(fechaVencimiento);
        cta.setTipoCargo(tipoCargo);
        cta.setPeriodoAcademico(periodoAcademico);
        cta.setNumeroCuota(numeroCuota);
        cta.setObservaciones(observaciones);

        cta.calcularMontoPendiente();
        cta.actualizarEstado();

        return cuentaCorrienteAlumnoRepository.save(cta);
    }
}
