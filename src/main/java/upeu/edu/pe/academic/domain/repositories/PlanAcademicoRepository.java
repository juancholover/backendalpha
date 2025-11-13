package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.PlanAcademico;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PlanAcademicoRepository implements PanacheRepositoryBase<PlanAcademico, Long> {

    /**
     * Buscar plan académico por código
     */
    public Optional<PlanAcademico> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo).firstResultOptional();
    }

    /**
     * Listar planes por programa académico
     */
    public List<PlanAcademico> findByProgramaAcademico(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and active = true", programaAcademicoId).list();
    }

    /**
     * Listar planes vigentes por programa
     */
    public List<PlanAcademico> findPlanesVigentes(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and estado = 'VIGENTE' and active = true", programaAcademicoId).list();
    }

    /**
     * Buscar plan vigente actual por programa
     */
    public Optional<PlanAcademico> findPlanVigenteActual(Long programaAcademicoId) {
        return find("programaAcademico.id = ?1 and estado = 'VIGENTE' and active = true order by fechaVigenciaInicio desc", 
                    programaAcademicoId).firstResultOptional();
    }

    /**
     * Listar todos los planes activos
     */
    public List<PlanAcademico> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe código
     */
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", codigo) > 0;
    }

    /**
     * Verificar si existe código excluyendo un ID
     */
    public boolean existsByCodigoAndIdNot(String codigo, Long id) {
        return count("codigo = ?1 and id != ?2 and active = true", codigo, id) > 0;
    }
}
