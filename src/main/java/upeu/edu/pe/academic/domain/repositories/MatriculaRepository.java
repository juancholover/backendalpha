package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Matricula;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MatriculaRepository implements PanacheRepositoryBase<Matricula, Long> {

    /**
     * Listar matrículas por estudiante
     */
    public List<Matricula> findByEstudiante(Long estudianteId) {
        return find("estudiante.id = ?1 and active = true ORDER BY fechaMatricula DESC", 
                   estudianteId).list();
    }

    /**
     * Listar matrículas por sección
     */
    public List<Matricula> findBySeccion(Long seccionId) {
        return find("cursoOfertado.id = ?1 and active = true ORDER BY estudiante.persona.apellidoPaterno", 
                   seccionId).list();
    }

    /**
     * Listar matrículas por período académico
     */
    public List<Matricula> findByPeriodoAcademico(Long periodoId) {
        return find("cursoOfertado.periodoAcademico.id = ?1 and active = true ORDER BY fechaMatricula DESC", 
                   periodoId).list();
    }

    /**
     * Listar matrículas de un estudiante en un período
     */
    public List<Matricula> findByEstudianteAndPeriodo(Long estudianteId, Long periodoId) {
        return find("estudiante.id = ?1 and cursoOfertado.periodoAcademico.id = ?2 and active = true", 
                   estudianteId, periodoId).list();
    }

    /**
     * Buscar matrícula específica (estudiante + sección)
     */
    public Optional<Matricula> findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return find("estudiante.id = ?1 and cursoOfertado.id = ?2 and active = true", 
                   estudianteId, seccionId).firstResultOptional();
    }

    /**
     * Verificar si estudiante ya está matriculado en una sección
     */
    public boolean existsByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return count("estudiante.id = ?1 and cursoOfertado.id = ?2 and active = true", 
                    estudianteId, seccionId) > 0;
    }

    /**
     * Listar matrículas por estado
     */
    public List<Matricula> findByEstadoMatricula(String estadoMatricula) {
        return find("UPPER(estadoMatricula) = UPPER(?1) and active = true", 
                   estadoMatricula).list();
    }

    /**
     * Listar matrículas activas (estado = MATRICULADO)
     */
    public List<Matricula> findMatriculasActivas(Long periodoId) {
        return find("seccion.periodoAcademico.id = ?1 and UPPER(estadoMatricula) = 'MATRICULADO' and active = true", 
                   periodoId).list();
    }

    /**
     * Listar matrículas aprobadas de un estudiante
     */
    public List<Matricula> findMatriculasAprobadas(Long estudianteId) {
        return find("estudiante.id = ?1 and UPPER(estadoAprobacion) = 'APROBADO' and active = true", 
                   estudianteId).list();
    }

    /**
     * Listar matrículas de un estudiante por estado de aprobación
     */
    public List<Matricula> findByEstudianteAndEstadoAprobacion(Long estudianteId, String estadoAprobacion) {
        return find("estudiante.id = ?1 and UPPER(estadoAprobacion) = UPPER(?2) and active = true", 
                   estudianteId, estadoAprobacion).list();
    }

    /**
     * Contar matrículas en una sección
     */
    public long countBySeccion(Long seccionId) {
        return count("seccion.id = ?1 and active = true", seccionId);
    }

    /**
     * Contar matrículas activas en una sección
     */
    public long countMatriculasActivasBySeccion(Long seccionId) {
        return count("seccion.id = ?1 and UPPER(estadoMatricula) = 'MATRICULADO' and active = true", 
                    seccionId);
    }

    /**
     * Listar todas las matrículas activas
     */
    public List<Matricula> findAllActive() {
        return find("active = true ORDER BY fechaMatricula DESC").list();
    }

    /**
     * Buscar matrículas por tipo
     */
    public List<Matricula> findByTipoMatricula(String tipoMatricula, Long periodoId) {
        return find("UPPER(tipoMatricula) = UPPER(?1) and seccion.periodoAcademico.id = ?2 and active = true", 
                   tipoMatricula, periodoId).list();
    }
}
