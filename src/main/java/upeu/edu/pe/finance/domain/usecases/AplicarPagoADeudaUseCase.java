package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.commands.AplicarPagoADeudaCommand;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Aplicar un pago a una deuda específica.
 * 
 * Reglas de negocio:
 * - Monto no puede exceder saldo disponible del pago
 * - Monto no puede exceder monto pendiente de la deuda
 * - No se puede duplicar aplicación activa
 */
@ApplicationScoped
public class AplicarPagoADeudaUseCase {

    @Inject
    PagoDetalleDeudaRepository detalleRepository;

    @Inject
    PagoRepository pagoRepository;

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    @Transactional
    public PagoDetalleDeuda execute(AplicarPagoADeudaCommand command) {

        // 1. Obtener pago
        Pago pago = pagoRepository.findByIdOptional(command.pagoId())
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + command.pagoId()));

        // 2. Obtener deuda
        CuentaCorrienteAlumno deuda = cuentaRepository.findByIdOptional(command.deudaId())
                .orElseThrow(() -> new NotFoundException("Deuda no encontrada con ID: " + command.deudaId()));

        // 3. Validar saldo del pago
        if (command.montoAplicado().compareTo(pago.getMontoPendienteAplicar()) > 0) {
            throw new BusinessException(
                    "El monto a aplicar (" + command.montoAplicado() + ") excede el saldo disponible del pago (" +
                            pago.getMontoPendienteAplicar() + ")");
        }

        // 4. Validar monto pendiente de la deuda
        if (command.montoAplicado().compareTo(deuda.getMontoPendiente()) > 0) {
            throw new BusinessException(
                    "El monto a aplicar (" + command.montoAplicado() + ") excede el monto pendiente de la deuda (" +
                            deuda.getMontoPendiente() + ")");
        }

        // 5. Validar que no exista aplicación activa duplicada
        if (detalleRepository.existsByPagoAndDeuda(command.pagoId(), command.deudaId())) {
            throw new BusinessException("Ya existe una aplicación activa de este pago a esta deuda");
        }

        // 6. Crear detalle de aplicación
        PagoDetalleDeuda detalle = new PagoDetalleDeuda();
        detalle.setPago(pago);
        detalle.setDeuda(deuda);
        detalle.setMontoAplicado(command.montoAplicado());
        detalle.setEstado("APLICADO");
        detalle.setFechaAplicacion(LocalDateTime.now());

        // 7. Actualizar pago
        pago.setMontoAplicado(pago.getMontoAplicado().add(command.montoAplicado()));
        pago.setMontoPendienteAplicar(pago.getMontoPendienteAplicar().subtract(command.montoAplicado()));
        if (pago.getMontoPendienteAplicar().compareTo(BigDecimal.ZERO) == 0) {
            pago.setEstado("APLICADO");
        } else {
            pago.setEstado("APLICADO_PARCIAL");
        }

        // 8. Actualizar deuda
        deuda.setMontoPagado(deuda.getMontoPagado().add(command.montoAplicado()));
        deuda.setMontoPendiente(deuda.getMontoPendiente().subtract(command.montoAplicado()));
        if (deuda.getMontoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            deuda.setEstado("PAGADA");
        } else {
            deuda.setEstado("PAGO_PARCIAL");
        }

        // 9. Persistir cambios
        detalleRepository.persist(detalle);
        pagoRepository.persist(pago);
        cuentaRepository.persist(deuda);

        return detalle;
    }
}
