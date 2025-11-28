package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class AnularPagoUseCase {

    private final PagoRepository pagoRepository;

    @Inject
    public AnularPagoUseCase(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Transactional
    public void execute(Long id, String motivo) {
        Pago pago = pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago not found with id " + id));

        pago.anular(motivo);
        pagoRepository.save(pago);
    }
}
