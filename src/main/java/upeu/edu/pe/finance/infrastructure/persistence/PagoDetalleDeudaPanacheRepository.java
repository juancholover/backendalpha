package upeu.edu.pe.finance.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PagoDetalleDeudaPanacheRepository
        implements PagoDetalleDeudaRepository, PanacheRepository<PagoDetalleDeuda> {

    @Override
    public PagoDetalleDeuda save(PagoDetalleDeuda pagoDetalleDeuda) {
        persist(pagoDetalleDeuda);
        return pagoDetalleDeuda;
    }

    @Override
    public Optional<PagoDetalleDeuda> findByIdOptional(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    @Override
    public List<PagoDetalleDeuda> listAll() {
        return list("active = true ORDER BY fechaAplicacion DESC");
    }

    @Override
    public void delete(Long id) {
        findByIdOptional(id).ifPresent(detalle -> {
            detalle.setActive(false);
            persist(detalle);
        });
    }

    @Override
    public List<PagoDetalleDeuda> findByPagoId(Long pagoId) {
        return list("pago.id = ?1 and active = true", pagoId);
    }

    @Override
    public List<PagoDetalleDeuda> findByDeudaId(Long deudaId) {
        return list("deuda.id = ?1 and active = true", deudaId);
    }
}
