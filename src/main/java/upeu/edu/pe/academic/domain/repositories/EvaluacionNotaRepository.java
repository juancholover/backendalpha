package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.EvaluacionNota;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EvaluacionNotaRepository implements PanacheRepository<EvaluacionNota> {

    /**
     * Busca notas por matrícula
     */
    public List<EvaluacionNota> findByMatricula(Long matriculaId) {
        return find("matricula.id = ?1 and active = true", matriculaId).list();
    }

    /**
     * Busca notas por criterio
     */
    public List<EvaluacionNota> findByCriterio(Long criterioId) {
        return find("criterio.id = ?1 and active = true", criterioId).list();
    }

    /**
     * Busca una nota específica
     */
    public Optional<EvaluacionNota> findByMatriculaAndCriterio(Long matriculaId, Long criterioId) {
        return find("matricula.id = ?1 and criterio.id = ?2", matriculaId, criterioId).firstResultOptional();
    }

    /**
     * Busca notas pendientes de calificar por sección
     */
    public List<EvaluacionNota> findPendientesBySeccion(Long seccionId) {
        return find("matricula.seccion.id = ?1 and UPPER(estado) = 'PENDIENTE' and active = true", 
                   seccionId).list();
    }

    /**
     * Busca notas calificadas por sección
     */
    public List<EvaluacionNota> findCalificadasBySeccion(Long seccionId) {
        return find("matricula.cursoOfertado.id = ?1 and UPPER(estado) = 'CALIFICADO' and active = true", 
                   seccionId).list();
    }

    /**
     * Busca notas por estudiante y sección
     */
    public List<EvaluacionNota> findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        return find("matricula.estudiante.id = ?1 and matricula.cursoOfertado.id = ?2 and active = true", 
                   estudianteId, seccionId).list();
    }

    /**
     * Busca notas con recuperación
     */
    public List<EvaluacionNota> findConRecuperacionBySeccion(Long seccionId) {
        return find("matricula.cursoOfertado.id = ?1 and notaRecuperacion IS NOT NULL and active = true", 
                   seccionId).list();
    }

    /**
     * Verifica si existe una nota
     */
    public boolean existsByMatriculaAndCriterio(Long matriculaId, Long criterioId) {
        return count("matricula.id = ?1 and criterio.id = ?2", matriculaId, criterioId) > 0;
    }

    /**
     * Cuenta notas calificadas de una matrícula
     */
    public long countCalificadasByMatricula(Long matriculaId) {
        return count("matricula.id = ?1 and UPPER(estado) = 'CALIFICADO'", matriculaId);
    }

    /**
     * Busca notas desaprobadas de una sección
     */
    public List<EvaluacionNota> findDesaprobadasBySeccion(Long seccionId) {
        return find("SELECT en FROM EvaluacionNota en " +
                   "WHERE en.matricula.cursoOfertado.id = ?1 " +
                   "AND en.notaFinal < en.criterio.notaMinimaAprobatoria " +
                   "AND en.active = true", seccionId).list();
    }

    /**
     * Calcula promedio de notas de un criterio
     */
    public Double getPromedioNotasByCriterio(Long criterioId) {
        Object result = find("SELECT AVG(notaFinal) FROM EvaluacionNota WHERE criterio.id = ?1 and notaFinal IS NOT NULL", 
                            criterioId).project(Double.class).firstResult();
        return result != null ? (Double) result : 0.0;
    }
}
