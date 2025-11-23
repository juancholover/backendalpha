package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Horario;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class HorarioRepository implements PanacheRepository<Horario> {

    /**
     * Busca todos los horarios de una universidad
     */
    public List<Horario> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true ORDER BY diaSemana, horaInicio", 
                   universidadId).list();
    }

    /**
     * Busca todos los horarios de un curso ofertado
     */
    public List<Horario> findByCursoOfertado(Long cursoOfertadoId) {
        return find("cursoOfertado.id = ?1 and active = true ORDER BY diaSemana, horaInicio", 
                   cursoOfertadoId).list();
    }

    /**
     * Busca horarios de un estudiante (a través de sus matrículas)
     */
    public List<Horario> findByEstudiante(Long estudianteId) {
        return find("cursoOfertado.id IN " +
                   "(SELECT m.cursoOfertado.id FROM Matricula m " +
                   "WHERE m.estudiante.id = ?1 and UPPER(m.estado) IN ('MATRICULADO', 'REGULAR')) " +
                   "and active = true ORDER BY diaSemana, horaInicio", 
                   estudianteId).list();
    }

    /**
     * Busca horarios de un profesor
     */
    public List<Horario> findByProfesor(Long profesorId) {
        return find("cursoOfertado.profesor.id = ?1 and active = true ORDER BY diaSemana, horaInicio", 
                   profesorId).list();
    }

    /**
     * Busca horarios por día de la semana
     */
    public List<Horario> findByDiaSemanaAndUniversidad(Integer diaSemana, Long universidadId) {
        return find("diaSemana = ?1 and universidad.id = ?2 and active = true ORDER BY horaInicio", 
                   diaSemana, universidadId).list();
    }

    /**
     * Busca horarios en una localización específica
     */
    public List<Horario> findByLocalizacion(Long localizacionId) {
        return find("localizacion.id = ?1 and active = true ORDER BY diaSemana, horaInicio", 
                   localizacionId).list();
    }

    /**
     * Verifica si existe cruce de horarios para un estudiante
     * al intentar matricularse en un nuevo curso
     */
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

    /**
     * Verifica si existe cruce de horarios para un profesor
     */
    public List<Horario> findCrucesProfesor(Long profesorId, Integer diaSemana, 
                                             LocalTime horaInicio, LocalTime horaFin) {
        return find("cursoOfertado.profesor.id = ?1 " +
                   "and diaSemana = ?2 " +
                   "and ((horaInicio < ?4 and horaFin > ?3) or " +
                   "     (horaInicio >= ?3 and horaInicio < ?4)) " +
                   "and active = true", 
                   profesorId, diaSemana, horaInicio, horaFin).list();
    }

    /**
     * Verifica si existe cruce de horarios en una localización (aula)
     */
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

    /**
     * Busca horarios por tipo de sesión
     */
    public List<Horario> findByTipoSesionAndUniversidad(String tipoSesion, Long universidadId) {
        return find("UPPER(tipoSesion) = UPPER(?1) and universidad.id = ?2 and active = true ORDER BY diaSemana, horaInicio", 
                   tipoSesion, universidadId).list();
    }

    /**
     * Cuenta horarios de un curso ofertado
     */
    public long countByCursoOfertado(Long cursoOfertadoId) {
        return count("cursoOfertado.id = ?1 and active = true", cursoOfertadoId);
    }

    /**
     * Verifica si existe un horario específico
     */
    public boolean existeHorario(Long cursoOfertadoId, Integer diaSemana, LocalTime horaInicio) {
        return count("cursoOfertado.id = ?1 and diaSemana = ?2 and horaInicio = ?3 and active = true", 
                    cursoOfertadoId, diaSemana, horaInicio) > 0;
    }

    /**
     * Busca por ID con todas las relaciones cargadas
     */
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
