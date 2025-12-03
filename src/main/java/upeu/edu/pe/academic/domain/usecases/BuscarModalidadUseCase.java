package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Modalidad;
import upeu.edu.pe.academic.domain.repositories.ModalidadRepository;

import java.util.List;

/**
 * Caso de uso: Buscar modalidades con diferentes criterios
 * 
 * Responsabilidades:
 * - Buscar por ID
 * - Buscar por código
 * - Listar por universidad
 * - Filtrar por tipo de recursos
 */
@ApplicationScoped
public class BuscarModalidadUseCase {
    
    @Inject
    ModalidadRepository modalidadRepository;
    
    /**
     * Busca una modalidad por ID
     */
    public Modalidad findById(Long id) {
        return modalidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Modalidad no encontrada con ID: " + id
                ));
    }
    
    /**
     * Busca una modalidad por código en una universidad
     */
    public Modalidad findByCodigo(String codigo, Long universidadId) {
        return modalidadRepository.findByCodigo(codigo, universidadId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Modalidad no encontrada con código: " + codigo
                ));
    }
    
    /**
     * Lista todas las modalidades de una universidad
     */
    public List<Modalidad> findByUniversidad(Long universidadId) {
        return modalidadRepository.findByUniversidad(universidadId);
    }
    
    /**
     * Lista modalidades que requieren aula física
     */
    public List<Modalidad> findRequiereAula(Long universidadId) {
        return modalidadRepository.findRequiereAula(universidadId);
    }
    
    /**
     * Lista modalidades que requieren plataforma digital
     */
    public List<Modalidad> findRequierePlataforma(Long universidadId) {
        return modalidadRepository.findRequierePlataforma(universidadId);
    }
    
    /**
     * Busca modalidades por nombre (búsqueda parcial)
     */
    public List<Modalidad> findByNombre(String nombre, Long universidadId) {
        return modalidadRepository.findByNombreLike(nombre, universidadId);
    }
}
