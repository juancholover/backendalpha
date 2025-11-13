package upeu.edu.pe.academic.domain.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Persona;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonaRepository implements PanacheRepositoryBase<Persona, Long> {

    /**
     * Buscar persona por número de documento
     */
    public Optional<Persona> findByNumeroDocumento(String numeroDocumento) {
        return find("numeroDocumento = ?1 and active = true", numeroDocumento).firstResultOptional();
    }

    /**
     * Buscar persona por email
     */
    public Optional<Persona> findByEmail(String email) {
        return find("email = ?1 and active = true", email).firstResultOptional();
    }

    /**
     * Buscar personas por tipo de documento
     */
    public List<Persona> findByTipoDocumento(String tipoDocumento) {
        return find("tipoDocumento = ?1 and active = true", tipoDocumento).list();
    }

    /**
     * Buscar personas por género
     */
    public List<Persona> findByGenero(String genero) {
        return find("genero = ?1 and active = true", genero).list();
    }

    /**
     * Buscar personas por nombres y apellidos (búsqueda parcial)
     */
    public List<Persona> searchByNombres(String searchTerm) {
        String pattern = "%" + searchTerm.toLowerCase() + "%";
        return find("(lower(nombres) like ?1 or lower(apellidoPaterno) like ?1 or lower(apellidoMaterno) like ?1) and active = true", 
                    pattern).list();
    }

    /**
     * Listar todas las personas activas
     */
    public List<Persona> findAllActive() {
        return find("active = true").list();
    }

    /**
     * Verificar si existe número de documento
     */
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return count("numeroDocumento = ?1 and active = true", numeroDocumento) > 0;
    }

    /**
     * Verificar si existe número de documento excluyendo un ID
     */
    public boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id) {
        return count("numeroDocumento = ?1 and id != ?2 and active = true", numeroDocumento, id) > 0;
    }

    /**
     * Verificar si existe email
     */
    public boolean existsByEmail(String email) {
        return count("email = ?1 and active = true", email) > 0;
    }

    /**
     * Verificar si existe email excluyendo un ID
     */
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return count("email = ?1 and id != ?2 and active = true", email, id) > 0;
    }
}
