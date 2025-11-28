package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.repositories.PersonaRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonaPanacheRepository implements PanacheRepositoryBase<Persona, Long>, PersonaRepository {

    @Override
    public void persist(Persona persona) {
        PanacheRepositoryBase.super.persist(persona);
    }

    @Override
    public Optional<Persona> findByIdOptional(Long id) {
        return PanacheRepositoryBase.super.findByIdOptional(id);
    }

    @Override
    public Optional<Persona> findByNumeroDocumento(String numeroDocumento) {
        return find("numeroDocumento = ?1 and active = true", numeroDocumento).firstResultOptional();
    }

    @Override
    public Optional<Persona> findByEmail(String email) {
        return find("email.value = ?1 and active = true", email).firstResultOptional();
    }

    @Override
    public List<Persona> findAllActive() {
        return find("active = true").list();
    }

    @Override
    public List<Persona> searchByNombres(String searchTerm) {
        return find(
                "active = true and (LOWER(nombres) like LOWER(?1) or LOWER(apellidoPaterno) like LOWER(?1) or LOWER(apellidoMaterno) like LOWER(?1))",
                "%" + searchTerm + "%").list();
    }

    @Override
    public boolean existsByNumeroDocumento(String numeroDocumento) {
        return count("numeroDocumento = ?1 and active = true", numeroDocumento) > 0;
    }

    @Override
    public boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id) {
        return count("numeroDocumento = :doc and id != :id and active = true",
                Parameters.with("doc", numeroDocumento).and("id", id)) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return count("email.value = ?1 and active = true", email) > 0;
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, Long id) {
        return count("email.value = :email and id != :id and active = true",
                Parameters.with("email", email).and("id", id)) > 0;
    }
}
