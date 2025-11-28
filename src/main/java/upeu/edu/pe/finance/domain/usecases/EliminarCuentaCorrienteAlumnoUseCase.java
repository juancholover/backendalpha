package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarCuentaCorrienteAlumnoUseCase {

    private final CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;

    @Inject
    public EliminarCuentaCorrienteAlumnoUseCase(CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository) {
        this.cuentaCorrienteAlumnoRepository = cuentaCorrienteAlumnoRepository;
    }

    @Transactional
    public void execute(Long id) {
        if (cuentaCorrienteAlumnoRepository.findByIdOptional(id).isEmpty()) {
            throw new ResourceNotFoundException("CuentaCorrienteAlumno not found with id " + id);
        }
        cuentaCorrienteAlumnoRepository.delete(id);
    }
}
