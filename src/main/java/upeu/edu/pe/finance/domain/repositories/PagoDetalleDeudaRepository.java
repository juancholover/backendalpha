package upeu.edu.pe.finance.domain.repositories;

import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import java.util.List;
import java.util.Optional;

public interface PagoDetalleDeudaRepository {
    PagoDetalleDeuda save(PagoDetalleDeuda pagoDetalleDeuda);

    Optional<PagoDetalleDeuda> findByIdOptional(Long id);

    List<PagoDetalleDeuda> listAll();

    void delete(Long id);

    List<PagoDetalleDeuda> findByPagoId(Long pagoId);

    List<PagoDetalleDeuda> findByDeudaId(Long deudaId);
}
