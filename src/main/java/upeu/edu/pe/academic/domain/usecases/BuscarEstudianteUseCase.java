package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Estudiante;
import upeu.edu.pe.academic.domain.exceptions.EstudianteNoEncontradoException;
import upeu.edu.pe.academic.domain.repositories.EstudianteRepository;

import java.util.List;

/**
 * Caso de uso: Buscar estudiantes.
 * 
 * Encapsula la lógica de búsqueda y validaciones.
 */
@ApplicationScoped
public class BuscarEstudianteUseCase {
    
    @Inject
    EstudianteRepository estudianteRepository;
    
    /**
     * Busca un estudiante por su ID.
     */
    public Estudiante ejecutarPorId(Long id) {
        return estudianteRepository.findByIdOptional(id)
            .filter(e -> e.getActive())
            .orElseThrow(() -> new EstudianteNoEncontradoException(id));
    }
    
    /**
     * Busca un estudiante por su código.
     */
    public Estudiante ejecutarPorCodigo(String codigoEstudiante) {
        return estudianteRepository.findByCodigoEstudiante(codigoEstudiante)
            .orElseThrow(() -> new EstudianteNoEncontradoException(null));
    }
    
    /**
     * Lista todos los estudiantes activos.
     */
    public List<Estudiante> ejecutarListarActivos() {
        return estudianteRepository.findAllActive();
    }
    
    /**
     * Lista estudiantes por programa académico.
     */
    public List<Estudiante> ejecutarPorPrograma(Long programaId) {
        return estudianteRepository.findByProgramaAcademico(programaId);
    }
    
    /**
     * Lista estudiantes por estado académico.
     */
    public List<Estudiante> ejecutarPorEstadoAcademico(String estadoAcademico) {
        return estudianteRepository.findByEstadoAcademico(estadoAcademico);
    }
    
    /**
     * Lista estudiantes activos de un programa específico.
     */
    public List<Estudiante> ejecutarActivosPorPrograma(Long programaId) {
        return estudianteRepository.findEstudiantesActivos(programaId);
    }
}
