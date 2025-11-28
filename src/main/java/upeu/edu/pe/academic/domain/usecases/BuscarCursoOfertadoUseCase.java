package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BuscarCursoOfertadoUseCase {

    private final CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    public BuscarCursoOfertadoUseCase(CursoOfertadoRepository cursoOfertadoRepository) {
        this.cursoOfertadoRepository = cursoOfertadoRepository;
    }

    public CursoOfertado findById(Long id) {
        return cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CursoOfertado", "id", id));
    }

    public List<CursoOfertado> findByUniversidad(Long universidadId) {
        return cursoOfertadoRepository.findByUniversidad(universidadId);
    }

    public List<CursoOfertado> findByPeriodoAcademico(Long periodoId) {
        return cursoOfertadoRepository.findByPeriodoAcademico(periodoId);
    }

    public List<CursoOfertado> findByPlanAcademico(Long planId) {
        return cursoOfertadoRepository.findByPlanAcademico(planId);
    }

    public List<CursoOfertado> findByPlanCurso(Long planCursoId) {
        return cursoOfertadoRepository.findByPlanCurso(planCursoId);
    }

    public List<CursoOfertado> findByCurso(Long cursoId) {
        return cursoOfertadoRepository.findByCurso(cursoId);
    }

    public List<CursoOfertado> findByProfesor(Long profesorId) {
        return cursoOfertadoRepository.findByProfesor(profesorId);
    }

    public List<CursoOfertado> findAbiertasByPeriodoAndUniversidad(Long periodoId, Long universidadId) {
        return cursoOfertadoRepository.findAbiertasByPeriodoAndUniversidad(periodoId, universidadId);
    }

    public List<CursoOfertado> findByModalidadAndPeriodo(String modalidad, Long periodoId) {
        return cursoOfertadoRepository.findByModalidadAndPeriodo(modalidad, periodoId);
    }

    public List<CursoOfertado> findConVacantesByPeriodo(Long periodoId) {
        return cursoOfertadoRepository.findConVacantesByPeriodo(periodoId);
    }

    public Optional<CursoOfertado> findByCodigoAndPeriodoAndUniversidad(String codigoSeccion, Long periodoId,
            Long universidadId) {
        return cursoOfertadoRepository.findByCodigoAndPeriodoAndUniversidad(codigoSeccion, periodoId, universidadId);
    }
}
