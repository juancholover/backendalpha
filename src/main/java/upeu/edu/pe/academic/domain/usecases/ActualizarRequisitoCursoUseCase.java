package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class ActualizarRequisitoCursoUseCase {

    private final RequisitoCursoRepository requisitoRepository;
    private final CursoRepository cursoRepository;

    @Inject
    public ActualizarRequisitoCursoUseCase(RequisitoCursoRepository requisitoRepository,
            CursoRepository cursoRepository) {
        this.requisitoRepository = requisitoRepository;
        this.cursoRepository = cursoRepository;
    }

    public RequisitoCurso execute(Long id, Long cursoId, Long cursoRequisitoId,
            String tipoRequisito, Boolean esObligatorio,
            Integer notaMinimaRequerida, String observacion) {

        RequisitoCurso requisito = requisitoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("RequisitoCurso", "id", id));

        // Validar que el curso y el requisito no sean el mismo
        if (cursoId.equals(cursoRequisitoId)) {
            throw new BusinessException("Un curso no puede ser requisito de sí mismo");
        }

        // Si cambia el curso o el requisito, validar duplicados y circulares
        boolean cambioCurso = !requisito.getCurso().getId().equals(cursoId);
        boolean cambioRequisito = !requisito.getCursoRequisito().getId().equals(cursoRequisitoId);

        if (cambioCurso || cambioRequisito) {
            if (requisitoRepository.existsRequisito(cursoId, cursoRequisitoId)) {
                throw new BusinessException("El requisito ya existe para este curso");
            }
            if (requisitoRepository.existsRequisito(cursoRequisitoId, cursoId)) {
                throw new BusinessException("No se puede actualizar el requisito: existe una dependencia circular");
            }
        }

        if (cambioCurso) {
            Curso curso = cursoRepository.findByIdOptional(cursoId)
                    .filter(Curso::getActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", cursoId));
            requisito.setCurso(curso);
        }

        if (cambioRequisito) {
            Curso cursoRequisito = cursoRepository.findByIdOptional(cursoRequisitoId)
                    .filter(Curso::getActive)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso (requisito)", "id", cursoRequisitoId));
            requisito.setCursoRequisito(cursoRequisito);
        }

        requisito.setTipoRequisito(tipoRequisito);
        requisito.setEsObligatorio(esObligatorio);
        requisito.setNotaMinimaRequerida(notaMinimaRequerida);
        requisito.setObservacion(observacion);

        requisitoRepository.persist(requisito);
        return requisito;
    }
}
