package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import upeu.edu.pe.academic.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BuscarPeriodoAcademicoUseCase {

    private final PeriodoAcademicoRepository repository;

    @Inject
    public BuscarPeriodoAcademicoUseCase(PeriodoAcademicoRepository repository) {
        this.repository = repository;
    }

    public PeriodoAcademico findById(Long id) {
        return repository.findByIdOptional(id)
                .filter(PeriodoAcademico::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "id", id));
    }

    public PeriodoAcademico findByCodigoPeriodo(String codigoPeriodo) {
        return repository.findByCodigoPeriodo(codigoPeriodo)
                .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "codigoPeriodo", codigoPeriodo));
    }

    public Optional<PeriodoAcademico> findPeriodoActual(Long universidadId) {
        return repository.findPeriodoActual(universidadId);
    }

    public List<PeriodoAcademico> findAllActive() {
        return repository.findAllActive();
    }

    public List<PeriodoAcademico> findByAnio(Integer anio) {
        return repository.findByAnio(anio);
    }

    public List<PeriodoAcademico> findByEstado(String estado) {
        return repository.findByEstado(estado);
    }

    public List<PeriodoAcademico> findByUniversidad(Long universidadId) {
        return repository.findByUniversidad(universidadId);
    }

    public Optional<PeriodoAcademico> findByCodigoAndUniversidad(String codigo, Long universidadId) {
        return repository.findByCodigoAndUniversidad(codigo, universidadId);
    }

    public List<PeriodoAcademico> findByAnioAndUniversidad(Integer anio, Long universidadId) {
        return repository.findByAnioAndUniversidad(anio, universidadId);
    }

    public List<PeriodoAcademico> findByEstadoAndUniversidad(String estado, Long universidadId) {
        return repository.findByEstadoAndUniversidad(estado, universidadId);
    }

    public List<PeriodoAcademico> findActivosAndUniversidad(Long universidadId) {
        return repository.findActivosAndUniversidad(universidadId);
    }
}
