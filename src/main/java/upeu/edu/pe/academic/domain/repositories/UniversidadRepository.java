package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.Universidad;
import java.util.List;
import java.util.Optional;

public interface UniversidadRepository {
    void persist(Universidad universidad);

    Optional<Universidad> findByIdOptional(Long id);

    Optional<Universidad> findByCodigo(String codigo);

    Optional<Universidad> findByDominio(String dominio);

    Optional<Universidad> findByRuc(String ruc);

    List<Universidad> findAllActive();

    List<Universidad> listAll();

    List<Universidad> search(String query);

    boolean existsByCodigo(String codigo);

    boolean existsByDominio(String dominio);

    boolean existsByRuc(String ruc);
}
