package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDateTime;

/**
 * Caso de Uso: Revertir una aplicación de pago a deuda.
 * 
 * Reglas de negocio:
 * - Solo se puede revertir aplicaciones activas
 * - Requiere motivo de reversión
 * - Restaura saldos en pago y deuda
 */
@ApplicationScoped
public class RevertirAplicacionPagoUseCase {

    @Inject
    PagoDetalleDeudaRepository detalleRepository;

    @Inject
    PagoRepository pagoRepository;

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    @Transactional
    public PagoDetalleDeuda execute(Long detalleId, String motivo) {

        // 1. Validar motivo
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de reversión es obligatorio");
        }

        // 2. Obtener detalle
        PagoDetalleDeuda detalle = detalleRepository.findByIdOptional(detalleId)
                .orElseThrow(() -> new NotFoundException("Detalle de pago no encontrado con ID: " + detalleId));

        // 3. Validar que esté activo
        if ("REVERTIDO".equals(detalle.getEstado())) {
            throw new BusinessException("Esta aplicación ya ha sido revertida");
        }

        // 4. Obtener pago y deuda
        Pago pago = detalle.getPago();
        CuentaCorrienteAlumno deuda = detalle.getDeuda();

        // 5. Revertir en pago
        pago.setMontoAplicado(pago.getMontoAplicado().subtract(detalle.getMontoAplicado()));
        pago.setMontoPendienteAplicar(pago.getMontoPendienteAplicar().add(detalle.getMontoAplicado()));
        pago.setEstado("REGISTRADO");

        // 6. Revertir en deuda
        deuda.setMontoPagado(deuda.getMontoPagado().subtract(detalle.getMontoAplicado()));
        deuda.setMontoPendiente(deuda.getMontoPendiente().add(detalle.getMontoAplicado()));
        deuda.setEstado("PENDIENTE");

        // 7. Actualizar detalle
        detalle.setEstado("REVERTIDO");
        detalle.setFechaReversion(LocalDateTime.now());
        detalle.setMotivoReversion(motivo);

        // 8. Persistir
        detalleRepository.persist(detalle);
        pagoRepository.persist(pago);
        cuentaRepository.persist(deuda);

        return detalle;
    }
}
