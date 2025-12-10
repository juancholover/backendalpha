package upeu.edu.pe.people.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.people.application.dto.ProfesorResponseDTO;
import upeu.edu.pe.people.application.mapper.ProfesorMapper;
import upeu.edu.pe.people.domain.entities.Profesor;
import upeu.edu.pe.people.domain.repositories.ProfesorRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de profesores.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA (crear, actualizar, eliminar) se manejan en Use
 * Cases.
 */
@ApplicationScoped
public class ProfesorService {

    @Inject
    ProfesorRepository profesorRepository;

    @Inject
    ProfesorMapper profesorMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Lista todos los profesores activos
     */
    public List<ProfesorResponseDTO> listarTodos() {
        return profesorRepository.findAllActive()
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca profesor por ID
     */
    public ProfesorResponseDTO buscarPorId(Long id) {
        Profesor profesor = profesorRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));

        if (!profesor.getActive()) {
            throw new ResourceNotFoundException("Profesor no está activo");
        }

        return profesorMapper.toResponseDTO(profesor);
    }

    /**
     * Busca profesor por persona
     */
    public ProfesorResponseDTO buscarPorPersona(Long personaId) {
        Profesor profesor = profesorRepository.findByPersona(personaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró profesor para la persona ID: " + personaId));

        return profesorMapper.toResponseDTO(profesor);
    }

    /**
     * Lista profesores por grado académico
     */
    public List<ProfesorResponseDTO> listarPorGradoAcademico(String gradoAcademico) {
        return profesorRepository.findByGradoAcademico(gradoAcademico)
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores por categoría docente
     */
    public List<ProfesorResponseDTO> listarPorCategoriaDocente(String categoriaDocente) {
        return profesorRepository.findByCategoriaDocente(categoriaDocente)
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores por dedicación
     */
    public List<ProfesorResponseDTO> listarPorDedicacion(String dedicacion) {
        return profesorRepository.findByDedicacion(dedicacion)
                .stream()
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista profesores con código RENACYT
     */
    public List<ProfesorResponseDTO> listarConRenacyt() {
        return profesorRepository.findAllActive()
                .stream()
                .filter(p -> p.getCodigoRenacyt() != null && !p.getCodigoRenacyt().isEmpty())
                .map(profesorMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public Profesor getEntityById(Long id) {
        return profesorRepository.findByIdOptional(id)
                .filter(Profesor::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado con ID: " + id));
    }
}
