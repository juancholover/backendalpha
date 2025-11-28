package upeu.edu.pe.academic.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CursoPanacheRepository implements CursoRepository, PanacheRepository<Curso> {

    @Override
    public void persist(Curso curso) {
        PanacheRepository.super.persist(curso);
    }

    @Override
    public Optional<Curso> findByIdOptional(Long id) {
        return PanacheRepository.super.findByIdOptional(id);
    }

    @Override
    public List<Curso> findAllActive() {
        return find("active = true").list();
    }

    @Override
    public Optional<Curso> findByCodigoCurso(String codigoCurso) {
        return find("codigoCurso = ?1 and active = true", codigoCurso).firstResultOptional();
    }

    @Override
    public List<Curso> findByUniversidad(Long universidadId) {
        return find("universidad.id = ?1 and active = true", universidadId).list();
    }

    @Override
    public boolean existsByCodigoCurso(String codigoCurso) {
        return count("codigoCurso = ?1 and active = true", codigoCurso) > 0;
    }

    @Override
    public boolean existsByCodigoCursoAndIdNot(String codigoCurso, Long id) {
        return count("codigoCurso = ?1 and id != ?2 and active = true", codigoCurso, id) > 0;
    }
}
