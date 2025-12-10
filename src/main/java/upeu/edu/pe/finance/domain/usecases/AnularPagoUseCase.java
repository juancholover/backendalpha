package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;

/**
 * Caso de Uso: Anular un pago.
 * 
 * Reglas de negocio:
 * - No se puede anular si tiene aplicaciones a deudas
 * - Requiere motivo de anulación
 */
@ApplicationScoped
public class AnularPagoUseCase {

    @Inject
    PagoRepository pagoRepository;

    @Transactional
    public Pago execute(Long pagoId, String motivo) {

        // 1. Validar motivo
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de anulación es obligatorio");
        }

        // 2. Obtener pago
        Pago pago = pagoRepository.findByIdOptional(pagoId)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + pagoId));

        // 3. Validar que no tenga aplicaciones
        if (pago.getMontoAplicado().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException(
                    "No se puede anular el pago porque tiene " + pago.getMontoAplicado() +
                            " aplicado a deudas. Primero revierta las aplicaciones.");
        }

        // 4. Anular
        pago.setEstado("ANULADO");
        pago.setObservaciones(
                (pago.getObservaciones() != null ? pago.getObservaciones() + ". " : "") +
                        "ANULADO: " + motivo);

        pagoRepository.persist(pago);

        return pago;
    }
}
