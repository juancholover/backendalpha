package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearRequisitoCursoUseCase {

    private final RequisitoCursoRepository requisitoRepository;
    private final UniversidadRepository universidadRepository;
    private final CursoRepository cursoRepository;

    @Inject
    public CrearRequisitoCursoUseCase(RequisitoCursoRepository requisitoRepository,
            UniversidadRepository universidadRepository,
            CursoRepository cursoRepository) {
        this.requisitoRepository = requisitoRepository;
        this.universidadRepository = universidadRepository;
        this.cursoRepository = cursoRepository;
    }

    public RequisitoCurso execute(Long universidadId, Long cursoId, Long cursoRequisitoId,
            String tipoRequisito, Boolean esObligatorio,
            Integer notaMinimaRequerida, String observacion) {

        // Validar que el curso y el requisito no sean el mismo
        if (cursoId.equals(cursoRequisitoId)) {
            throw new BusinessException("Un curso no puede ser requisito de sí mismo");
        }

        // Validar que no exista el requisito
        if (requisitoRepository.existsRequisito(cursoId, cursoRequisitoId)) {
            throw new BusinessException("El requisito ya existe para este curso");
        }

        // Validar dependencias circulares
        if (requisitoRepository.existsRequisito(cursoRequisitoId, cursoId)) {
            throw new BusinessException("No se puede crear el requisito: existe una dependencia circular");
        }

        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        Curso curso = cursoRepository.findByIdOptional(cursoId)
                .filter(Curso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", cursoId));

        Curso cursoRequisito = cursoRepository.findByIdOptional(cursoRequisitoId)
                .filter(Curso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Curso (requisito)", "id", cursoRequisitoId));

        RequisitoCurso requisito = RequisitoCurso.crear(universidad, curso, cursoRequisito,
                tipoRequisito, esObligatorio, notaMinimaRequerida, observacion);

        requisitoRepository.persist(requisito);
        return requisito;
    }
}
