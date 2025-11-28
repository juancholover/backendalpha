package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UniversidadPanacheRepository implements PanacheRepositoryBase<Universidad, Long>, UniversidadRepository {

    @Override
    public void persist(Universidad universidad) {
        PanacheRepositoryBase.super.persist(universidad);
    }

    @Override
    public Optional<Universidad> findByIdOptional(Long id) {
        return PanacheRepositoryBase.super.findByIdOptional(id);
    }

    @Override
    public List<Universidad> listAll() {
        return PanacheRepositoryBase.super.listAll();
    }

    @Override
    public Optional<Universidad> findByCodigo(String codigo) {
        return find("codigo = ?1 and active = true", codigo).firstResultOptional();
    }

    @Override
    public Optional<Universidad> findByDominio(String dominio) {
        return find("dominio = ?1 and active = true", dominio).firstResultOptional();
    }

    @Override
    public Optional<Universidad> findByRuc(String ruc) {
        return find("ruc = ?1 and active = true", ruc).firstResultOptional();
    }

    @Override
    public List<Universidad> findAllActive() {
        return find("active = true").list();
    }

    @Override
    public List<Universidad> search(String query) {
        return list("LOWER(nombre) LIKE LOWER(?1)", "%" + query + "%");
    }

    @Override
    public boolean existsByCodigo(String codigo) {
        return count("codigo = ?1 and active = true", codigo) > 0;
    }

    @Override
    public boolean existsByDominio(String dominio) {
        return count("dominio = ?1 and active = true", dominio) > 0;
    }

    @Override
    public boolean existsByRuc(String ruc) {
        return count("ruc = ?1 and active = true", ruc) > 0;
    }
}
