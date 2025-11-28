package upeu.edu.pe.academic.domain.repositories;

import upeu.edu.pe.academic.domain.entities.Persona;
import java.util.List;
import java.util.Optional;

public interface PersonaRepository {
    void persist(Persona persona);

    Optional<Persona> findByIdOptional(Long id);

    Optional<Persona> findByNumeroDocumento(String numeroDocumento);

    Optional<Persona> findByEmail(String email);

    List<Persona> findAllActive();

    List<Persona> searchByNombres(String searchTerm);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByNumeroDocumentoAndIdNot(String numeroDocumento, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
