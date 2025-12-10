package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.CursoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.CursoMapper;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para consultas de cursos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class CursoService {

    @Inject
    CursoRepository cursoRepository;

    @Inject
    CursoMapper cursoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<CursoResponseDTO> findAll() {
        return cursoRepository.findAllActive()
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public CursoResponseDTO findById(Long id) {
        Curso curso = cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));

        return cursoMapper.toResponseDTO(curso);
    }

    public CursoResponseDTO findByCodigoCurso(String codigoCurso) {
        Curso curso = cursoRepository.findByCodigoCurso(codigoCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "codigoCurso", codigoCurso));

        return cursoMapper.toResponseDTO(curso);
    }

    public List<CursoResponseDTO> findByUniversidad(Long universidadId) {
        return cursoRepository.findAllActiveCursos()
                .stream()
                .map(cursoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener entidad Curso por ID (para uso interno de otros services/usecases)
     */
    public Curso getEntityById(Long id) {
        return cursoRepository.findByIdOptional(id)
                .filter(Curso::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
    }
}
