package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.entities.PeriodoAcademico;
import upeu.edu.pe.enrollment.domain.repositories.PeriodoAcademicoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarPeriodoAcademicoUseCase {

    @Inject
    PeriodoAcademicoRepository periodoRepository;

    @Transactional
    public void execute(Long id) {
        PeriodoAcademico periodo = periodoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Período académico no encontrado con ID: " + id));

        // Validar que no tenga cursos ofertados
        if (periodo.getCursosOfertados() != null && !periodo.getCursosOfertados().isEmpty()) {
            throw new BusinessException("No se puede eliminar: tiene " +
                    periodo.getCursosOfertados().size() + " cursos ofertados asociados");
        }

        periodo.setActive(false);
        periodoRepository.persist(periodo);
    }
}
