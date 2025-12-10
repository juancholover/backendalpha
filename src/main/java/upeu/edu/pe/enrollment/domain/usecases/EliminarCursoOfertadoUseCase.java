package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.entities.CursoOfertado;
import upeu.edu.pe.enrollment.domain.repositories.CursoOfertadoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

/**
 * Caso de Uso: Eliminar un curso ofertado.
 * 
 * Reglas de negocio:
 * - No se puede eliminar si tiene estudiantes matriculados
 */
@ApplicationScoped
public class EliminarCursoOfertadoUseCase {

    @Inject
    CursoOfertadoRepository cursoOfertadoRepository;

    @Transactional
    public void execute(Long cursoOfertadoId) {

        // 1. Obtener curso ofertado
        CursoOfertado cursoOfertado = cursoOfertadoRepository.findByIdOptional(cursoOfertadoId)
                .orElseThrow(() -> new NotFoundException("Curso ofertado no encontrado con ID: " + cursoOfertadoId));

        // 2. Verificar matrículas
        long matriculados = cursoOfertadoRepository.countMatriculados(cursoOfertadoId);
        if (matriculados > 0) {
            throw new BusinessException(
                    "No se puede eliminar el curso porque tiene " + matriculados + " estudiantes matriculados");
        }

        // 3. Soft delete
        cursoOfertado.setActive(false);
        cursoOfertadoRepository.persist(cursoOfertado);
    }
}
