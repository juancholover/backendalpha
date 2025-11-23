package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.CursoOfertado;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CursoOfertadoRepository implements PanacheRepository<CursoOfertado> {

    /**
     * Busca cursos ofertados por universidad
     */
    public List<CursoOfertado> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true ORDER BY codigoSeccion", 
                   universidadId).list();
    }

    /**
     * Busca cursos ofertados por período académico
     */
    public List<CursoOfertado> findByPeriodoAcademico(Long periodoId) {
        return find("periodoAcademico.id = ?1 and active = true ORDER BY codigoSeccion", 
                   periodoId).list();
    }

    /**
     * Busca cursos ofertados por plan académico (a través de PlanCurso)
     */
    public List<CursoOfertado> findByPlanAcademico(Long planId) {
        return find("planCurso.planAcademico.id = ?1 and active = true ORDER BY codigoSeccion", 
                   planId).list();
    }

    /**
     * Busca cursos ofertados por PlanCurso específico
     */
    public List<CursoOfertado> findByPlanCurso(Long planCursoId) {
        return find("planCurso.id = ?1 and active = true ORDER BY codigoSeccion", 
                   planCursoId).list();
    }

    /**
     * Busca cursos ofertados por curso (a través de PlanCurso)
     */
    public List<CursoOfertado> findByCurso(Long cursoId) {
        return find("planCurso.curso.id = ?1 and active = true ORDER BY periodoAcademico.fechaInicio DESC", 
                   cursoId).list();
    }

    /**
     * Busca cursos ofertados por profesor
     */
    public List<CursoOfertado> findByProfesor(Long profesorId) {
        return find("profesor.id = ?1 and active = true ORDER BY periodoAcademico.fechaInicio DESC", 
                   profesorId).list();
    }

    /**
     * Busca un curso ofertado específico
     */
    public Optional<CursoOfertado> findByCodigoAndPeriodoAndUniversidad(String codigoSeccion, Long periodoId, Long universidadId) {
        return find("UPPER(codigoSeccion) = UPPER(?1) and periodoAcademico.id = ?2 and universidad.id = ?3 and active = true", 
                   codigoSeccion, periodoId, universidadId).firstResultOptional();
    }

    /**
     * Busca cursos ofertados abiertos (disponibles para matrícula)
     */
    public List<CursoOfertado> findAbiertasByPeriodoAndUniversidad(Long periodoId, Long universidadId) {
        return find("periodoAcademico.id = ?1 and universidad.id = ?2 and UPPER(estado) = 'ABIERTA' and vacantesDisponibles > 0 and active = true", 
                   periodoId, universidadId).list();
    }

    /**
     * Busca cursos ofertados por modalidad
     */
    public List<CursoOfertado> findByModalidadAndPeriodo(String modalidad, Long periodoId) {
        return find("UPPER(modalidad) = UPPER(?1) and periodoAcademico.id = ?2 and active = true", 
                   modalidad, periodoId).list();
    }

    /**
     * Busca cursos ofertados con vacantes disponibles
     */
    public List<CursoOfertado> findConVacantesByPeriodo(Long periodoId) {
        return find("periodoAcademico.id = ?1 and vacantesDisponibles > 0 and active = true ORDER BY codigoSeccion", 
                   periodoId).list();
    }

    /**
     * Verifica si existe un curso ofertado
     */
    public boolean existsByCodigoAndPeriodoAndPlanCurso(String codigoSeccion, Long periodoId, Long planCursoId) {
        return count("UPPER(codigoSeccion) = UPPER(?1) and periodoAcademico.id = ?2 and planCurso.id = ?3", 
                    codigoSeccion, periodoId, planCursoId) > 0;
    }

    /**
     * Cuenta matriculados en un curso ofertado
     */
    public long countMatriculados(Long seccionId) {
        return count("SELECT COUNT(m) FROM Matricula m WHERE m.seccion.id = ?1", seccionId);
    }

    /**
     * Busca cursos ofertados por localización (aula)
     */
    public List<CursoOfertado> findByLocalizacion(Long localizacionId) {
        return find("localizacion.id = ?1 and active = true", localizacionId).list();
    }
}
