package upeu.edu.pe.finance.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarCuentaCorrienteAlumnoUseCase {

    private final CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository;

    @Inject
    public BuscarCuentaCorrienteAlumnoUseCase(CuentaCorrienteAlumnoRepository cuentaCorrienteAlumnoRepository) {
        this.cuentaCorrienteAlumnoRepository = cuentaCorrienteAlumnoRepository;
    }

    public CuentaCorrienteAlumno findById(Long id) {
        return cuentaCorrienteAlumnoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CuentaCorrienteAlumno not found with id " + id));
    }

    public List<CuentaCorrienteAlumno> findAll() {
        return cuentaCorrienteAlumnoRepository.listAll();
    }

    public List<CuentaCorrienteAlumno> search(String query) {
        return cuentaCorrienteAlumnoRepository.search(query);
    }

    public List<CuentaCorrienteAlumno> findByEstudianteId(Long estudianteId) {
        return cuentaCorrienteAlumnoRepository.findByEstudianteId(estudianteId);
    }
}
