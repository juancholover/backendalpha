package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Semestre;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SemestreRepository implements PanacheRepositoryBase<Semestre, Long> {

    /**
     * Buscar semestre por código
     */
    public Optional<Semestre> findByCodigoSemestre(String codigoSemestre) {
        return find("codigoSemestre = ?1 and active = true", codigoSemestre).firstResultOptional();
    }

    /**
     * Buscar el semestre actual
     */
    public Optional<Semestre> findSemestreActual() {
        return find("esActual = true and active = true").firstResultOptional();
    }

    /**
     * Listar semestres por año
     */
    public List<Semestre> findByAnio(Integer anio) {
        return find("anio = ?1 and active = true order by periodo asc", anio).list();
    }

    /**
     * Listar semestres por estado
     */
    public List<Semestre> findByEstado(String estado) {
        return find("estado = ?1 and active = true order by anio desc, periodo desc", estado).list();
    }

    /**
     * Listar semestres en curso
     */
    public List<Semestre> findSemestresEnCurso() {
        return find("estado = 'EN_CURSO' and active = true").list();
    }

    /**
     * Listar todos los semestres activos ordenados
     */
    public List<Semestre> findAllActiveOrdered() {
        return find("active = true order by anio desc, periodo desc").list();
    }

    /**
     * Verificar si existe código de semestre
     */
    public boolean existsByCodigoSemestre(String codigoSemestre) {
        return count("codigoSemestre = ?1 and active = true", codigoSemestre) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoSemestreAndIdNot(String codigoSemestre, Long id) {
        return count("codigoSemestre = ?1 and id != ?2 and active = true", codigoSemestre, id) > 0;
    }

    /**
     * Verificar si ya existe otro semestre como actual
     */
    public boolean existsOtroSemestreActual(Long currentId) {
        if (currentId == null) {
            return count("esActual = true and active = true") > 0;
        }
        return count("esActual = true and id != ?1 and active = true", currentId) > 0;
    }
}
