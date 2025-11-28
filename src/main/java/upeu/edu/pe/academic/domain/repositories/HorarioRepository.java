package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.Horario;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioRepository {
    void persist(Horario horario);

    Optional<Horario> findByIdOptional(Long id);

    List<Horario> findByUniversidad(Long universidadId);

    List<Horario> findByCursoOfertado(Long cursoOfertadoId);

    List<Horario> findByEstudiante(Long estudianteId);

    List<Horario> findByProfesor(Long profesorId);

    List<Horario> findByDiaSemanaAndUniversidad(Integer diaSemana, Long universidadId);

    List<Horario> findByLocalizacion(Long localizacionId);

    List<Horario> findCrucesEstudiante(Long estudianteId, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin);

    List<Horario> findCrucesProfesor(Long profesorId, Integer diaSemana, LocalTime horaInicio, LocalTime horaFin);

    List<Horario> findCrucesLocalizacion(Long localizacionId, Integer diaSemana, LocalTime horaInicio,
            LocalTime horaFin, Long horarioIdExcluir);

    List<Horario> findByTipoSesionAndUniversidad(String tipoSesion, Long universidadId);

    long countByCursoOfertado(Long cursoOfertadoId);

    boolean existeHorario(Long cursoOfertadoId, Integer diaSemana, LocalTime horaInicio);

    Optional<Horario> findByIdWithRelations(Long id);
}
