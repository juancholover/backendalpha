package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarPagoUseCase {

    private final PagoRepository pagoRepository;

    @Inject
    public BuscarPagoUseCase(PagoRepository pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    public Pago findById(Long id) {
        return pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago not found with id " + id));
    }

    public List<Pago> findAll() {
        return pagoRepository.listAll();
    }

    public List<Pago> search(String query) {
        return pagoRepository.search(query);
    }

    public List<Pago> findByEstudianteId(Long estudianteId) {
        return pagoRepository.findByEstudianteId(estudianteId);
    }
}
