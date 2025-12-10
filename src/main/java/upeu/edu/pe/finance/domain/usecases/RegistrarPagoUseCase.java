package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.commands.RegistrarPagoCommand;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Caso de Uso: Registrar un nuevo pago.
 * 
 * Reglas de negocio:
 * - Número de recibo debe ser único
 * - Monto debe ser mayor a cero
 * - Estudiante debe existir
 */
@ApplicationScoped
public class RegistrarPagoUseCase {

    @Inject
    PagoRepository pagoRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Transactional
    public Pago execute(RegistrarPagoCommand command) {

        // 1. Validar número de recibo único
        if (pagoRepository.existsByNumeroRecibo(command.numeroRecibo())) {
            throw new DuplicateResourceException("Pago", "numeroRecibo", command.numeroRecibo());
        }

        // 2. Obtener estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(command.estudianteId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + command.estudianteId()));

        // 3. Crear pago
        Pago pago = new Pago();
        pago.setEstudiante(estudiante);
        pago.setNumeroRecibo(command.numeroRecibo());
        pago.setMontoPagado(command.montoPagado());
        pago.setFechaPago(command.fechaPago() != null ? command.fechaPago() : LocalDateTime.now());
        pago.setMetodoPago(command.metodoPago());
        pago.setCajero(command.cajero());
        pago.setObservaciones(command.observaciones());

        // 4. Inicializar montos
        pago.setMontoAplicado(BigDecimal.ZERO);
        pago.setMontoPendienteAplicar(command.montoPagado());
        pago.setEstado("REGISTRADO");

        // 5. Persistir
        pagoRepository.persist(pago);

        return pago;
    }
}
