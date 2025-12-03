package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.exceptions.UniversidadNoEncontradaException;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

import java.util.List;

/**
 * Caso de uso: Buscar universidades.
 * 
 * Encapsula la lógica de búsqueda y validaciones.
 */
@ApplicationScoped
public class BuscarUniversidadUseCase {
    
    @Inject
    UniversidadRepository repository;
    
   
    public Universidad ejecutarPorId(Long id) {
        return repository.findByIdOptional(id)
            .orElseThrow(() -> new UniversidadNoEncontradaException(id));
    }
    
   
    public Universidad ejecutarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo)
            .orElseThrow(() -> new UniversidadNoEncontradaException(null));
    }
    
    /**
     * Lista todas las universidades activas.
     * 
     * @return Lista de universidades activas
     */
    public List<Universidad> ejecutarListarActivas() {
        return repository.findAllActive();
    }
    
    /**
     * Lista todas las universidades (incluyendo inactivas).
     * 
     * @return Lista de todas las universidades
     */
    public List<Universidad> ejecutarListarTodas() {
        return repository.listAll();
    }
}
