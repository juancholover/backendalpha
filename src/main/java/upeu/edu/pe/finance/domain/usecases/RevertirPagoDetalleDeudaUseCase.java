package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class RevertirPagoDetalleDeudaUseCase {

    private final PagoDetalleDeudaRepository pagoDetalleDeudaRepository;
    private final PagoRepository pagoRepository;
    private final CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;

    @Inject
    public RevertirPagoDetalleDeudaUseCase(PagoDetalleDeudaRepository pagoDetalleDeudaRepository,
            PagoRepository pagoRepository,
            CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository) {
        this.pagoDetalleDeudaRepository = pagoDetalleDeudaRepository;
        this.pagoRepository = pagoRepository;
        this.cuentaCorrienteAlumnoRepository = cuentaCorrienteAlumnoRepository;
    }

    @Transactional
    public void execute(Long id, String motivo) {
        PagoDetalleDeuda detalle = pagoDetalleDeudaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("PagoDetalleDeuda not found with id " + id));

        detalle.revertir(motivo);

        // Guardar cambios en entidades relacionadas
        if (detalle.getPago() != null) {
            pagoRepository.save(detalle.getPago());
        }
        if (detalle.getDeuda() != null) {
            cuentaCorrienteAlumnoRepository.save(detalle.getDeuda());
        }

        pagoDetalleDeudaRepository.save(detalle);
    }
}
