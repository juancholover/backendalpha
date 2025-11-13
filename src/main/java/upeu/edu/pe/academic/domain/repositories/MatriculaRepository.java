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
        return find("estudiante.id = ?1 and active = true", estudianteId).list();
    }

    /**
     * Listar matrículas por semestre
     */
    public List<Matricula> findBySemestre(Long semestreId) {
        return find("semestre.id = ?1 and active = true", semestreId).list();
    }

    /**
     * Listar matrículas por curso
     */
    public List<Matricula> findByCurso(Long cursoId) {
        return find("curso.id = ?1 and active = true", cursoId).list();
    }

    /**
     * Listar matrículas por profesor
     */
    public List<Matricula> findByProfesor(Long profesorId) {
        return find("profesor.id = ?1 and active = true", profesorId).list();
    }

    /**
     * Buscar matrícula específica (estudiante + curso + semestre)
     */
    public Optional<Matricula> findMatricula(Long estudianteId, Long cursoId, Long semestreId) {
        return find("estudiante.id = ?1 and curso.id = ?2 and semestre.id = ?3 and active = true", 
                    estudianteId, cursoId, semestreId).firstResultOptional();
    }

    /**
     * Listar matrículas de un estudiante en un semestre
     */
    public List<Matricula> findByEstudianteAndSemestre(Long estudianteId, Long semestreId) {
        return find("estudiante.id = ?1 and semestre.id = ?2 and active = true", estudianteId, semestreId).list();
    }

    /**
     * Listar matrículas por curso y semestre (sección completa)
     */
    public List<Matricula> findByCursoAndSemestre(Long cursoId, Long semestreId, String seccion) {
        return find("curso.id = ?1 and semestre.id = ?2 and seccion = ?3 and active = true", 
                    cursoId, semestreId, seccion).list();
    }

    /**
     * Listar matrículas por estado
     */
    public List<Matricula> findByEstadoMatricula(String estadoMatricula) {
        return find("estadoMatricula = ?1 and active = true", estadoMatricula).list();
    }

    /**
     * Listar matrículas aprobadas
     */
    public List<Matricula> findMatriculasAprobadas(Long estudianteId) {
        return find("estudiante.id = ?1 and estadoAprobacion = 'APROBADO' and active = true", estudianteId).list();
    }

    /**
     * Verificar si estudiante ya está matriculado en curso/semestre
     */
    public boolean existsMatricula(Long estudianteId, Long cursoId, Long semestreId) {
        return count("estudiante.id = ?1 and curso.id = ?2 and semestre.id = ?3 and active = true", 
                     estudianteId, cursoId, semestreId) > 0;
    }

    /**
     * Contar matrículas activas en un curso/semestre/sección
     */
    public long countMatriculasActivas(Long cursoId, Long semestreId, String seccion) {
        return count("curso.id = ?1 and semestre.id = ?2 and seccion = ?3 and estadoMatricula = 'MATRICULADO' and active = true", 
                     cursoId, semestreId, seccion);
    }

    /**
     * Listar todas las matrículas activas
     */
    public List<Matricula> findAllActive() {
        return find("active = true").list();
    }
}
