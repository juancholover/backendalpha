package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Horario;
import upeu.edu.pe.academic.domain.repositories.HorarioRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HorarioPanacheRepository implements HorarioRepository, PanacheRepository<Horario> {

    @Override
    public void persist(Horario horario) {
        PanacheRepository.super.persist(horario);
    }

    @Override
    public Optional<Horario> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public List<Horario> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true ORDER BY diaSemana, horaInicio",
                universidadId).list();
    }

    @Override
    public List<Horario> findByCursoOfertado(Long cursoOfertadoId) {
        return find("cursoOfertado.id = ?1 and active = true ORDER BY diaSemana, horaInicio",
                cursoOfertadoId).list();
    }

    @Override
    public List<Horario> findByEstudiante(Long estudianteId) {
        return find("cursoOfertado.id IN " +
                "(SELECT m.cursoOfertado.id FROM Matricula m " +
                "WHERE m.estudiante.id = ?1 and UPPER(m.estado) IN ('MATRICULADO', 'REGULAR')) " +
                "and active = true ORDER BY diaSemana, horaInicio",
                estudianteId).list();
    }

    @Override
    public List<Horario> findByProfesor(Long profesorId) {
        return find("cursoOfertado.profesor.id = ?1 and active = true ORDER BY diaSemana, horaInicio",
                profesorId).list();
    }

    @Override
    public List<Horario> findByDiaSemanaAndUniversidad(Integer diaSemana, Long universidadId) {
        return find("diaSemana = ?1 and universidad.id = ?2 and active = true ORDER BY horaInicio",
                diaSemana, universidadId).list();
    }

    @Override
    public List<Horario> findByLocalizacion(Long localizacionId) {
        return find("localizacion.id = ?1 and active = true ORDER BY diaSemana, horaInicio",
                localizacionId).list();
    }

    @Override
    public List<Horario> findCrucesEstudiante(Long estudianteId, Integer diaSemana,
            LocalTime horaInicio, LocalTime horaFin) {
        return find("cursoOfertado.id IN " +
                "(SELECT m.cursoOfertado.id FROM Matricula m " +
                "WHERE m.estudiante.id = ?1 and UPPER(m.estado) IN ('MATRICULADO', 'REGULAR')) " +
                "and diaSemana = ?2 " +
                "and ((horaInicio < ?4 and horaFin > ?3) or " +
                "     (horaInicio >= ?3 and horaInicio < ?4)) " +
                "and active = true",
                estudianteId, diaSemana, horaInicio, horaFin).list();
    }

    @Override
    public List<Horario> findCrucesProfesor(Long profesorId, Integer diaSemana,
            LocalTime horaInicio, LocalTime horaFin) {
        return find("cursoOfertado.profesor.id = ?1 " +
                "and diaSemana = ?2 " +
                "and ((horaInicio < ?4 and horaFin > ?3) or " +
                "     (horaInicio >= ?3 and horaInicio < ?4)) " +
                "and active = true",
                profesorId, diaSemana, horaInicio, horaFin).list();
    }

    @Override
    public List<Horario> findCrucesLocalizacion(Long localizacionId, Integer diaSemana,
            LocalTime horaInicio, LocalTime horaFin,
            Long horarioIdExcluir) {
        String query = "localizacion.id = ?1 " +
                "and diaSemana = ?2 " +
                "and ((horaInicio < ?4 and horaFin > ?3) or " +
                "     (horaInicio >= ?3 and horaInicio < ?4)) " +
                "and active = true";

        if (horarioIdExcluir != null) {
            query += " and id != " + horarioIdExcluir;
        }

        return find(query, localizacionId, diaSemana, horaInicio, horaFin).list();
    }

    @Override
    public List<Horario> findByTipoSesionAndUniversidad(String tipoSesion, Long universidadId) {
        return find(
                "UPPER(tipoSesion) = UPPER(?1) and universidad.id = ?2 and active = true ORDER BY diaSemana, horaInicio",
                tipoSesion, universidadId).list();
    }

    @Override
    public long countByCursoOfertado(Long cursoOfertadoId) {
        return count("cursoOfertado.id = ?1 and active = true", cursoOfertadoId);
    }

    @Override
    public boolean existeHorario(Long cursoOfertadoId, Integer diaSemana, LocalTime horaInicio) {
        return count("cursoOfertado.id = ?1 and diaSemana = ?2 and horaInicio = ?3 and active = true",
                cursoOfertadoId, diaSemana, horaInicio) > 0;
    }

    @Override
    public Optional<Horario> findByIdWithRelations(Long id) {
        return find("SELECT h FROM Horario h " +
                "LEFT JOIN FETCH h.universidad " +
                "LEFT JOIN FETCH h.cursoOfertado co " +
                "LEFT JOIN FETCH co.planCurso pc " +
                "LEFT JOIN FETCH pc.curso " +
                "LEFT JOIN FETCH co.profesor p " +
                "LEFT JOIN FETCH p.persona " +
                "LEFT JOIN FETCH h.localizacion " +
                "WHERE h.id = ?1 and h.active = true", id)
                .firstResultOptional();
    }
}
