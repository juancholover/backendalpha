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
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;

@ApplicationScoped
public class CrearPagoDetalleDeudaUseCase {

    private final PagoDetalleDeudaRepository pagoDetalleDeudaRepository;
    private final PagoRepository pagoRepository;
    private final CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;

    @Inject
    public CrearPagoDetalleDeudaUseCase(PagoDetalleDeudaRepository pagoDetalleDeudaRepository,
            PagoRepository pagoRepository,
            CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository) {
        this.pagoDetalleDeudaRepository = pagoDetalleDeudaRepository;
        this.pagoRepository = pagoRepository;
        this.cuentaCorrienteAlumnoRepository = cuentaCorrienteAlumnoRepository;
    }

    @Transactional
    public PagoDetalleDeuda execute(Long pagoId, Long deudaId, BigDecimal montoAplicado, String aplicadoPor,
            String observaciones) {

        Pago pago = pagoRepository.findByIdOptional(pagoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pago not found with id " + pagoId));

        CuentaCorrienteAlumno deuda = cuentaCorrienteAlumnoRepository.findByIdOptional(deudaId)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda not found with id " + deudaId));

        PagoDetalleDeuda detalle = PagoDetalleDeuda.crear(pago, deuda, montoAplicado, aplicadoPor, observaciones);

        // Validar reglas de negocio
        detalle.validarAplicacion();

        // Aplicar cambios a Pago y Deuda
        detalle.aplicar();

        // Guardar cambios
        pagoRepository.save(pago);
        cuentaCorrienteAlumnoRepository.save(deuda);
        return pagoDetalleDeudaRepository.save(detalle);
    }
}
