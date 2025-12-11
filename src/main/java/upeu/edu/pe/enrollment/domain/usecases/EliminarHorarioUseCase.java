package upeu.edu.pe.enrollment.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.domain.entities.Horario;
import upeu.edu.pe.enrollment.domain.repositories.HorarioRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

@ApplicationScoped
public class EliminarHorarioUseCase {

    @Inject
    HorarioRepository horarioRepository;

    @Transactional
    public void execute(Long id) {
        Horario horario = horarioRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Horario no encontrado con ID: " + id));

        horario.setActive(false);
        horarioRepository.persist(horario);
    }
}
