package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;

@ApplicationScoped
public class ActualizarPagoUseCase {

    private final PagoRepository pagoRepository;

    @Inject
    public ActualizarPagoUseCase(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Transactional
    public Pago execute(Long id, String numeroRecibo, BigDecimal montoPagado, String metodoPago,
            String referenciaPago, String banco, String cajero, String observaciones) {

        Pago pago = pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago not found with id " + id));

        if (!pago.getNumeroRecibo().equals(numeroRecibo) && pagoRepository.existsByNumeroRecibo(numeroRecibo)) {
            throw new IllegalArgumentException("Ya existe un pago con el número de recibo " + numeroRecibo);
        }

        // Si el pago ya tiene aplicaciones, no se puede cambiar el monto a menos que
        // sea mayor o igual al aplicado
        if (pago.getMontoAplicado().compareTo(BigDecimal.ZERO) > 0 &&
                montoPagado.compareTo(pago.getMontoAplicado()) < 0) {
            throw new IllegalArgumentException("El nuevo monto no puede ser menor al monto ya aplicado");
        }

        pago.setNumeroRecibo(numeroRecibo);
        pago.setMontoPagado(montoPagado);
        pago.setMetodoPago(metodoPago);
        pago.setReferenciaPago(referenciaPago);
        pago.setBanco(banco);
        pago.setCajero(cajero);
        pago.setObservaciones(observaciones);

        pago.calcularMontoPendienteAplicar();
        pago.actualizarEstado();

        return pagoRepository.save(pago);
    }
}
