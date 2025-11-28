package upeu.edu.pe.finance.domain.repositories;

import upeu.edu.pe.finance.domain.entities.Pago;
import java.util.List;
import java.util.Optional;

public interface PagoRepository {
    Pago save(Pago pago);

    Optional<Pago> findByIdOptional(Long id);

    List<Pago> listAll();

    void delete(Long id);

    List<Pago> search(String query);

    List<Pago> findByEstudianteId(Long estudianteId);

    boolean existsByNumeroRecibo(String numeroRecibo);
}
