package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

@ApplicationScoped
public class CrearUniversidadUseCase {

    private final UniversidadRepository repository;

    @Inject
    public CrearUniversidadUseCase(UniversidadRepository repository) {
        this.repository = repository;
    }

    public Universidad execute(String codigo, String nombre, String ruc, String tipo, String dominio) {
        validarCodigoUnico(codigo);
        validarDominioUnico(dominio);
        validarRucUnico(ruc);

        Universidad universidad = Universidad.crear(codigo, nombre, ruc, tipo, dominio);
        repository.persist(universidad);

        return universidad;
    }

    private void validarCodigoUnico(String codigo) {
        if (repository.existsByCodigo(codigo)) {
            throw new DuplicateResourceException("Universidad", "codigo", codigo);
        }
    }

    private void validarDominioUnico(String dominio) {
        if (dominio != null && repository.existsByDominio(dominio)) {
            throw new DuplicateResourceException("Universidad", "dominio", dominio);
        }
    }

    private void validarRucUnico(String ruc) {
        if (repository.existsByRuc(ruc)) {
            throw new DuplicateResourceException("Universidad", "ruc", ruc);
        }
    }
}
