package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.repositories.HorarioRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class EliminarHorarioUseCase {

    private final HorarioRepository horarioRepository;

    @Inject
    public EliminarHorarioUseCase(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public void execute(Long id) {
        Horario horario = horarioRepository.findByIdOptional(id)
                .filter(Horario::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Horario", "id", id));

        horario.setActive(false);
        horarioRepository.persist(horario);
    }
}
