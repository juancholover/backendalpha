package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.commands.CrearRequisitoCursoCommand;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.entities.RequisitoCurso;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.curriculum.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class CrearRequisitoCursoUseCase {

    @Inject
    RequisitoCursoRepository requisitoRepository;

    @Inject
    CursoRepository cursoRepository;

    @Transactional
    public RequisitoCurso execute(CrearRequisitoCursoCommand command) {
        // 1. Obtener curso
        Curso curso = cursoRepository.findByIdOptional(command.cursoId())
                .orElseThrow(() -> new NotFoundException("Curso no encontrado con ID: " + command.cursoId()));

        // 2. Obtener curso requisito
        Curso cursoRequisito = cursoRepository.findByIdOptional(command.cursoRequisitoId())
                .orElseThrow(() -> new NotFoundException(
                        "Curso requisito no encontrado con ID: " + command.cursoRequisitoId()));

        // 3. Validar duplicado
        if (requisitoRepository.existsRequisito(command.cursoId(), command.cursoRequisitoId())) {
            throw new BusinessException("Ya existe este requisito para el curso");
        }

        // 4. Crear requisito
        RequisitoCurso requisito = new RequisitoCurso();
        requisito.setCurso(curso);
        requisito.setCursoRequisito(cursoRequisito);
        requisito.setTipoRequisito(command.tipoRequisito());

        requisitoRepository.persist(requisito);
        return requisito;
    }
}
