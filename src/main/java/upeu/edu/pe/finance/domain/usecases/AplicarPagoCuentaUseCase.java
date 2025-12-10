package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;

/**
 * Caso de Uso: Aplicar pago a cuenta corriente.
 * 
 * Reglas de negocio:
 * - Monto debe ser mayor a cero
 * - Monto no puede exceder el monto pendiente
 * - Actualiza estado según saldo
 */
@ApplicationScoped
public class AplicarPagoCuentaUseCase {

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    @Transactional
    public CuentaCorrienteAlumno execute(Long cuentaId, BigDecimal montoPago) {

        // 1. Validar monto
        if (montoPago == null || montoPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto del pago debe ser mayor a cero");
        }

        // 2. Obtener cuenta
        CuentaCorrienteAlumno cuenta = cuentaRepository.findByIdOptional(cuentaId)
                .orElseThrow(() -> new NotFoundException("Cuenta corriente no encontrada con ID: " + cuentaId));

        // 3. Validar monto no excede pendiente
        if (montoPago.compareTo(cuenta.getMontoPendiente()) > 0) {
            throw new BusinessException(
                    "El monto del pago (" + montoPago + ") excede el monto pendiente (" + cuenta.getMontoPendiente()
                            + ")");
        }

        // 4. Aplicar pago
        cuenta.setMontoPagado(cuenta.getMontoPagado().add(montoPago));
        cuenta.setMontoPendiente(cuenta.getMontoPendiente().subtract(montoPago));

        // 5. Actualizar estado
        if (cuenta.getMontoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            cuenta.setEstado("PAGADA");
        } else {
            cuenta.setEstado("PAGO_PARCIAL");
        }

        cuentaRepository.persist(cuenta);

        return cuenta;
    }
}
