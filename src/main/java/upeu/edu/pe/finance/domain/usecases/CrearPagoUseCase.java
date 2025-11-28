package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;

@ApplicationScoped
public class CrearPagoUseCase {

    private final PagoRepository pagoRepository;
    private final UniversidadRepository universidadRepository;
    private final EstudianteRepository estudianteRepository;

    @Inject
    public CrearPagoUseCase(PagoRepository pagoRepository,
            UniversidadRepository universidadRepository,
            EstudianteRepository estudianteRepository) {
        this.pagoRepository = pagoRepository;
        this.universidadRepository = universidadRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional
    public Pago execute(Long universidadId, Long estudianteId, String numeroRecibo,
            BigDecimal montoPagado, String metodoPago, String referenciaPago,
            String banco, String cajero, String observaciones) {

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad not found with id " + universidadId));

        Estudiante estudiante = estudianteRepository.findByIdOptional(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante not found with id " + estudianteId));

        if (pagoRepository.existsByNumeroRecibo(numeroRecibo)) {
            throw new IllegalArgumentException("Ya existe un pago con el número de recibo " + numeroRecibo);
        }

        Pago pago = Pago.crear(
                universidad, estudiante, numeroRecibo, montoPagado,
                metodoPago, referenciaPago, banco, cajero, observaciones);

        return pagoRepository.save(pago);
    }
}
