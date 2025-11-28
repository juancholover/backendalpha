package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;

@ApplicationScoped
public class CrearCuentaCorrienteAlumnoUseCase {

    private final CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;
    private final UniversidadRepository universidadRepository;
    private final EstudianteRepository estudianteRepository;

    @Inject
    public CrearCuentaCorrienteAlumnoUseCase(CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository,
            UniversidadRepository universidadRepository,
            EstudianteRepository estudianteRepository) {
        this.cuentaCorrienteAlumnoRepository = cuentaCorrienteAlumnoRepository;
        this.universidadRepository = universidadRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional
    public CuentaCorrienteAlumno execute(Long universidadId, Long estudianteId, BigDecimal monto, String concepto,
            LocalDate fechaVencimiento, String tipoCargo, String periodoAcademico,
            Integer numeroCuota, String observaciones) {

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad not found with id " + universidadId));

        Estudiante estudiante = estudianteRepository.findByIdOptional(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante not found with id " + estudianteId));

        CuentaCorrienteAlumno cta = CuentaCorrienteAlumno.crear(
                universidad, estudiante, monto, concepto, fechaVencimiento,
                tipoCargo, periodoAcademico, numeroCuota, observaciones);

        return cuentaCorrienteAlumnoRepository.save(cta);
    }
}
