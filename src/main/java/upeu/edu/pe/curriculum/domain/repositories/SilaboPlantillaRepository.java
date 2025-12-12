package upeu.edu.pe.curriculum.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.curriculum.domain.entities.SilaboPlantilla;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilaboPlantillaRepository implements PanacheRepositoryBase<SilaboPlantilla, Long> {

    /**
     * Busca una plantilla por el ID del sílabo base.
     */
    public Optional<SilaboPlantilla> findBySilabo(Long silaboId) {
        return find("silabo.id = ?1 and active = true", silaboId)
                .firstResultOptional();
    }

    /**
     * Lista todas las plantillas activas.
     */
    public List<SilaboPlantilla> findAllActivas() {
        return find("estado = 'ACTIVA' and active = true order by createdAt desc")
                .list();
    }

    /**
     * Lista plantillas vigentes en una fecha específica.
     */
    public List<SilaboPlantilla> findVigentesEnFecha(LocalDate fecha) {
        return find("""
                estado = 'ACTIVA' 
                and active = true
                and (fechaInicioVigencia is null or fechaInicioVigencia <= ?1)
                and (fechaFinVigencia is null or fechaFinVigencia >= ?1)
                order by createdAt desc
                """, fecha)
                .list();
    }

    /**
     * Lista plantillas por estado.
     */
    public List<SilaboPlantilla> findByEstado(String estado) {
        return find("estado = ?1 and active = true order by createdAt desc", estado)
                .list();
    }

    /**
     * Busca plantillas por curso (a través del sílabo).
     */
    public List<SilaboPlantilla> findByCurso(Long cursoId) {
        return find("silabo.curso.id = ?1 and active = true order by createdAt desc", cursoId)
                .list();
    }

    /**
     * Busca plantillas por año académico (a través del sílabo).
     */
    public List<SilaboPlantilla> findByAnioAcademico(String anioAcademico) {
        return find("silabo.anioAcademico = ?1 and active = true order by createdAt desc", anioAcademico)
                .list();
    }

    /**
     * Busca plantillas autorizadas por un usuario.
     */
    public List<SilaboPlantilla> findByAutorizadoPor(String usuario) {
        return find("autorizadoPor = ?1 and active = true order by fechaAutorizacion desc", usuario)
                .list();
    }

    /**
     * Cuenta plantillas por estado.
     */
    public long countByEstado(String estado) {
        return count("estado = ?1 and active = true", estado);
    }
}
