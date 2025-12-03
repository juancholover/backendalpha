package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso: Buscar sílabos con diferentes criterios
 * 
 * Responsabilidades:
 * - Buscar sílabo por ID
 * - Buscar sílabo vigente de un curso
 * - Buscar todos los sílabos de un curso
 * - Buscar sílabos por año académico
 * - Buscar sílabos por estado
 * - Buscar sílabos pendientes de aprobación
 * - Buscar sílabos aprobados por año
 */
@ApplicationScoped
public class BuscarSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    /**
     * Buscar sílabo por ID
     */
    public Optional<Silabo> findById(Long id) {
        return silaboRepository.findByIdOptional(id);
    }
    
    /**
     * Buscar sílabo vigente de un curso
     */
    public List<Silabo> findVigenteByCurso(Long cursoId, Long universidadId) {
        return silaboRepository.findVigenteByCurso(cursoId, universidadId);
    }
    
    /**
     * Buscar todos los sílabos de un curso (todas las versiones)
     */
    public List<Silabo> findByCurso(Long cursoId, Long universidadId) {
        return silaboRepository.findByCurso(cursoId, universidadId);
    }
    
    /**
     * Buscar sílabos de un curso en un año específico
     */
    public Optional<Silabo> findByCursoAndAnio(Long cursoId, String anioAcademico, Long universidadId) {
        return silaboRepository.findByCursoAndAnio(cursoId, anioAcademico, universidadId);
    }
    
    /**
     * Buscar sílabos por año académico
     */
    public List<Silabo> findByAnioAcademico(String anioAcademico, Long universidadId) {
        return silaboRepository.findByAnioAcademico(anioAcademico, universidadId);
    }
    
    /**
     * Buscar sílabos por estado
     */
    public List<Silabo> findByEstado(String estado, Long universidadId) {
        return silaboRepository.findByEstado(estado, universidadId);
    }
    
    /**
     * Buscar sílabos pendientes de aprobación (EN_REVISION)
     */
    public List<Silabo> findPendientesAprobacion(Long universidadId) {
        return silaboRepository.findPendientesAprobacion(universidadId);
    }
    
    /**
     * Buscar sílabos aprobados en un año
     */
    public List<Silabo> findAprobadosByAnio(String anioAcademico, Long universidadId) {
        return silaboRepository.findAprobadosByAnio(anioAcademico, universidadId);
    }
    
    /**
     * Buscar última versión de sílabo de un curso
     */
    public Optional<Silabo> findUltimaVersion(Long cursoId, Long universidadId) {
        return silaboRepository.findUltimaVersion(cursoId, universidadId);
    }
    
    /**
     * Contar sílabos por estado
     */
    public long countByEstado(String estado, Long universidadId) {
        return silaboRepository.countByEstado(estado, universidadId);
    }
}
