package upeu.edu.pe.finance.infrastructure.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PagoPanacheRepository implements PagoRepository, PanacheRepository<Pago> {

    @Override
    public Pago save(Pago pago) {
        persist(pago);
        return pago;
    }

    @Override
    public Optional<Pago> findByIdOptional(Long id) {
        return find("id = ?1 and active = true", id).firstResultOptional();
    }

    @Override
    public List<Pago> listAll() {
        return list("active = true ORDER BY fechaPago DESC");
    }

    @Override
    public void delete(Long id) {
        findByIdOptional(id).ifPresent(pago -> {
            pago.setActive(false);
            persist(pago);
        });
    }

    @Override
    public List<Pago> search(String query) {
        return list("active = true and (UPPER(numeroRecibo) like UPPER(?1) or UPPER(estudiante.nombre) like UPPER(?1))",
                "%" + query + "%");
    }

    @Override
    public List<Pago> findByEstudianteId(Long estudianteId) {
        return list("estudiante.id = ?1 and active = true", estudianteId);
    }

    @Override
    public boolean existsByNumeroRecibo(String numeroRecibo) {
        return count("numeroRecibo = ?1 and active = true", numeroRecibo) > 0;
    }
}
