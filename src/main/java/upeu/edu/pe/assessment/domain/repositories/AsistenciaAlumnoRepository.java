package upeu.edu.pe.assessment.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.assessment.domain.entities.AsistenciaAlumno;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AsistenciaAlumnoRepository implements PanacheRepositoryBase<AsistenciaAlumno, Long> {

    /**
     * Buscar asistencia por estudiante, horario y fecha
     */
    public Optional<AsistenciaAlumno> findByEstudianteHorarioFecha(Long estudianteId, Long horarioId,
            LocalDate fechaClase) {
        return find("estudiante.id = ?1 and horario.id = ?2 and fechaClase = ?3 and active = true",
                estudianteId, horarioId, fechaClase)
                .firstResultOptional();
    }

    /**
     * Listar asistencias de un estudiante
     */
    public List<AsistenciaAlumno> findByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and active = true", estudianteId).list();
    }

    /**
     * Listar asistencias de un estudiante en un rango de fechas
     */
    public List<AsistenciaAlumno> findByEstudianteAndFechaRange(Long estudianteId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        return find("estudiante.id = ?1 and fechaClase >= ?2 and fechaClase <= ?3 and active = true",
                estudianteId, fechaInicio, fechaFin).list();
    }

    /**
     * Listar asistencias de un horario en una fecha específica
     */
    public List<AsistenciaAlumno> findByHorarioAndFecha(Long horarioId, LocalDate fechaClase) {
        return find("horario.id = ?1 and fechaClase = ?2 and active = true", horarioId, fechaClase).list();
    }

    /**
     * Listar asistencias de un horario (todas las fechas)
     */
    public List<AsistenciaAlumno> findByHorario(Long horarioId) {
        return find("horario.id = ?1 and active = true", horarioId).list();
    }

    /**
     * Listar asistencias por estado
     */
    public List<AsistenciaAlumno> findByEstado(String estado) {
        return find("estado = ?1 and active = true", estado).list();
    }

    /**
     * Listar asistencias de un estudiante por estado
     */
    public List<AsistenciaAlumno> findByEstudianteAndEstado(Long estudianteId, String estado) {
        return find("estudiante.id = ?1 and estado = ?2 and active = true", estudianteId, estado).list();
    }

    /**
     * Listar asistencias de un estudiante en una fecha específica
     */
    public List<AsistenciaAlumno> findByEstudianteAndFecha(Long estudianteId, LocalDate fechaClase) {
        return find("estudiante.id = ?1 and fechaClase = ?2 and active = true", estudianteId, fechaClase).list();
    }

    /**
     * Buscar con todas las relaciones cargadas
     */
    public Optional<AsistenciaAlumno> findByIdWithRelations(Long id) {
        return find("SELECT a FROM AsistenciaAlumno a " +
                "LEFT JOIN FETCH a.estudiante e " +
                "LEFT JOIN FETCH e.persona " +
                "LEFT JOIN FETCH a.horario h " +
                "LEFT JOIN FETCH h.cursoOfertado co " +
                "LEFT JOIN FETCH co.curso " +
                "LEFT JOIN FETCH h.localizacion " +
                "WHERE a.id = ?1 and a.active = true", id)
                .firstResultOptional();
    }

    /**
     * Contar asistencias de un estudiante por estado
     */
    public long countByEstudianteAndEstado(Long estudianteId, String estado) {
        return count("estudiante.id = ?1 and estado = ?2 and active = true", estudianteId, estado);
    }

    /**
     * Contar asistencias de un estudiante en un horario
     */
    public long countByEstudianteAndHorario(Long estudianteId, Long horarioId) {
        return count("estudiante.id = ?1 and horario.id = ?2 and active = true", estudianteId, horarioId);
    }

    /**
     * Calcular porcentaje de asistencia de un estudiante en un horario
     */
    public double calcularPorcentajeAsistencia(Long estudianteId, Long horarioId) {
        long totalClases = countByEstudianteAndHorario(estudianteId, horarioId);
        if (totalClases == 0)
            return 0.0;

        long clasesAsistidas = count(
                "estudiante.id = ?1 and horario.id = ?2 and (estado = 'PRESENTE' or estado = 'TARDANZA') and active = true",
                estudianteId, horarioId);

        return (clasesAsistidas * 100.0) / totalClases;
    }

    /**
     * Verificar si existe asistencia registrada
     */
    public boolean existsByEstudianteHorarioFecha(Long estudianteId, Long horarioId, LocalDate fechaClase) {
        return count("estudiante.id = ?1 and horario.id = ?2 and fechaClase = ?3 and active = true",
                estudianteId, horarioId, fechaClase) > 0;
    }

    /**
     * Listar todas las asistencias activas
     */
    public List<AsistenciaAlumno> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Listar todas las asistencias por fecha
     */
    public List<AsistenciaAlumno> findByFecha(LocalDate fechaClase) {
        return find("fechaClase = ?1 and active = true", fechaClase).list();
    }

    // =====================================================
    // MÉTODOS ADICIONALES PARA CONSULTAS POR SECCIÓN
    // =====================================================

    /**
     * Listar asistencias de un estudiante en una sección (curso ofertado)
     */
    public List<AsistenciaAlumno> findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return find("estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and active = true ORDER BY fechaClase DESC",
                estudianteId, seccionId).list();
    }

    /**
     * Listar asistencias de una sección en un rango de fechas
     */
    public List<AsistenciaAlumno> findBySeccionAndFechaRange(Long seccionId, LocalDate fechaInicio,
            LocalDate fechaFin) {
        return find(
                "horario.cursoOfertado.id = ?1 and fechaClase >= ?2 and fechaClase <= ?3 and active = true ORDER BY fechaClase",
                seccionId, fechaInicio, fechaFin).list();
    }

    /**
     * Listar ausencias de un estudiante en una sección
     */
    public List<AsistenciaAlumno> findAusenciasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return find(
                "estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and estado = 'AUSENTE' and active = true ORDER BY fechaClase",
                estudianteId, seccionId).list();
    }

    /**
     * Contar registros de un estudiante en una sección
     */
    public long countByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return count("estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and active = true",
                estudianteId, seccionId);
    }

    /**
     * Contar asistencias (PRESENTE + TARDANZA) de un estudiante en una sección
     */
    public long countAsistenciasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return count(
                "estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and (estado = 'PRESENTE' or estado = 'TARDANZA') and active = true",
                estudianteId, seccionId);
    }

    /**
     * Contar ausencias de un estudiante en una sección
     */
    public long countAusenciasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return count("estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and estado = 'AUSENTE' and active = true",
                estudianteId, seccionId);
    }

    /**
     * Contar tardanzas de un estudiante en una sección
     */
    public long countTardanzasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return count("estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and estado = 'TARDANZA' and active = true",
                estudianteId, seccionId);
    }

    /**
     * Contar justificadas de un estudiante en una sección
     */
    public long countJustificadasByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return count(
                "estudiante.id = ?1 and horario.cursoOfertado.id = ?2 and estado = 'JUSTIFICADO' and active = true",
                estudianteId, seccionId);
    }

    /**
     * Contar total de clases de una sección (registros únicos de fecha por horario)
     */
    public long countClasesBySeccion(Long seccionId) {
        return count("horario.cursoOfertado.id = ?1 and active = true", seccionId);
    }
}
