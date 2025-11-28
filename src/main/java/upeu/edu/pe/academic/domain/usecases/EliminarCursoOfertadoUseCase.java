package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;
import upeu.edu.pe.academic.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarCursoOfertadoUseCase {

    private final CursoOfertadoRepository cursoOfertadoRepository;

    @Inject
    public EliminarCursoOfertadoUseCase(CursoOfertadoRepository cursoOfertadoRepository) {
        this.cursoOfertadoRepository = cursoOfertadoRepository;
    }

    public void execute(Long id) {
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("CursoOfertado", "id", id));

        // Verificar si tiene matrículas
        long matriculados = cursoOfertadoRepository.countMatriculados(id);
        if (matriculados > 0) {
            throw new BusinessException("No se puede eliminar el curso ofertado porque tiene " + matriculados
                    + " estudiantes matriculados");
        }

        // Soft delete
        cursoOfertado.setActive(false);
        cursoOfertadoRepository.persist(cursoOfertado);
    }
}
