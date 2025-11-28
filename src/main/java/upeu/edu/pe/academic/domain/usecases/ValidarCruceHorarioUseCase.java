package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.repositories.HorarioRepository;

import java.util.List;

@ApplicationScoped
public class ValidarCruceHorarioUseCase {

    private final HorarioRepository horarioRepository;

    @Inject
    public ValidarCruceHorarioUseCase(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public boolean execute(Long estudianteId, Long cursoOfertadoId) {
        List<Horario> horariosNuevoCurso = horarioRepository.findByCursoOfertado(cursoOfertadoId);

        for (Horario nuevoHorario : horariosNuevoCurso) {
            List<Horario> cruces = horarioRepository.findCrucesEstudiante(
                    estudianteId,
                    nuevoHorario.getDiaSemana(),
                    nuevoHorario.getHoraInicio(),
                    nuevoHorario.getHoraFin());

            if (!cruces.isEmpty()) {
                return true;
            }
        }

        return false;
    }
}
