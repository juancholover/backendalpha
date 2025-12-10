package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.commands.CrearCuentaCorrienteCommand;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.people.domain.entities.Estudiante;
import upeu.edu.pe.people.domain.repositories.EstudianteRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Caso de Uso: Crear cuenta corriente (deuda) de alumno.
 * 
 * Reglas de negocio:
 * - No duplicar concepto para el mismo estudiante
 * - Monto debe ser mayor a cero
 * - Fecha vencimiento debe ser posterior a fecha emisión
 */
@ApplicationScoped
public class CrearCuentaCorrienteUseCase {

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    @Inject
    EstudianteRepository estudianteRepository;

    @Transactional
    public CuentaCorrienteAlumno execute(CrearCuentaCorrienteCommand command) {

        // 1. Validar estudiante
        Estudiante estudiante = estudianteRepository.findByIdOptional(command.estudianteId())
                .orElseThrow(() -> new NotFoundException("Estudiante no encontrado con ID: " + command.estudianteId()));

        // 2. Validar duplicado
        if (cuentaRepository.existsByEstudianteAndConcepto(command.estudianteId(), command.concepto())) {
            throw new BusinessException("Ya existe una cuenta con el mismo concepto para este estudiante");
        }

        // 3. Validar fechas
        LocalDate fechaEmision = command.fechaEmision() != null ? command.fechaEmision() : LocalDate.now();
        if (command.fechaVencimiento() != null && command.fechaVencimiento().isBefore(fechaEmision)) {
            throw new BusinessException("La fecha de vencimiento debe ser posterior a la fecha de emisión");
        }

        // 4. Crear cuenta
        CuentaCorrienteAlumno cuenta = new CuentaCorrienteAlumno();
        cuenta.setEstudiante(estudiante);
        cuenta.setConcepto(command.concepto());
        cuenta.setMonto(command.monto());
        cuenta.setFechaEmision(fechaEmision);
        cuenta.setFechaVencimiento(command.fechaVencimiento());
        cuenta.setTipoCargo(command.tipoCargo());
        cuenta.setObservaciones(command.observaciones());

        // 5. Inicializar montos
        cuenta.setMontoPagado(BigDecimal.ZERO);
        cuenta.setMontoPendiente(command.monto());
        cuenta.setEstado("PENDIENTE");

        // 6. Persistir
        cuentaRepository.persist(cuenta);

        return cuenta;
    }
}
