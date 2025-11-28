package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.repositories.HorarioRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;

@ApplicationScoped
public class BuscarHorarioUseCase {

    private final HorarioRepository horarioRepository;

    @Inject
    public BuscarHorarioUseCase(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    public Horario findById(Long id) {
        return horarioRepository.findByIdWithRelations(id)
                .filter(Horario::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Horario", "id", id));
    }

    public List<Horario> findByUniversidad(Long universidadId) {
        return horarioRepository.findByUniversidad(universidadId);
    }

    public List<Horario> findByCursoOfertado(Long cursoOfertadoId) {
        return horarioRepository.findByCursoOfertado(cursoOfertadoId);
    }

    public List<Horario> findByEstudiante(Long estudianteId) {
        return horarioRepository.findByEstudiante(estudianteId);
    }

    public List<Horario> findByProfesor(Long profesorId) {
        return horarioRepository.findByProfesor(profesorId);
    }

    public List<Horario> findByDiaSemana(Integer diaSemana, Long universidadId) {
        return horarioRepository.findByDiaSemanaAndUniversidad(diaSemana, universidadId);
    }

    public List<Horario> findByLocalizacion(Long localizacionId) {
        return horarioRepository.findByLocalizacion(localizacionId);
    }
}
