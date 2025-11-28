package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarPagoDetalleDeudaUseCase {

    private final PagoDetalleDeudaRepository pagoDetalleDeudaRepository;

    @Inject
    public BuscarPagoDetalleDeudaUseCase(PagoDetalleDeudaRepository pagoDetalleDeudaRepository) {
        this.pagoDetalleDeudaRepository = pagoDetalleDeudaRepository;
    }

    public PagoDetalleDeuda findById(Long id) {
        return pagoDetalleDeudaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("PagoDetalleDeuda not found with id " + id));
    }

    public List<PagoDetalleDeuda> findByPagoId(Long pagoId) {
        return pagoDetalleDeudaRepository.findByPagoId(pagoId);
    }

    public List<PagoDetalleDeuda> findByDeudaId(Long deudaId) {
        return pagoDetalleDeudaRepository.findByDeudaId(deudaId);
    }
}
