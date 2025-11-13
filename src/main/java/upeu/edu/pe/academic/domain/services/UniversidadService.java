package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.application.mapper.UniversidadMapper;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UniversidadService {

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    UniversidadMapper universidadMapper;

    /**
     * Crea una nueva universidad
     */
    @Transactional
    public UniversidadResponseDTO create(UniversidadRequestDTO dto) {
        // Validar RUC único
        if (universidadRepository.existsByRuc(dto.getRuc())) {
            throw new DuplicateResourceException("Universidad", "ruc", dto.getRuc());
        }

        Universidad universidad = universidadMapper.toEntity(dto);
        universidadRepository.persist(universidad);
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Lista todas las universidades
     */
    public List<UniversidadResponseDTO> findAll() {
        return universidadRepository.listAll().stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista solo universidades activas
     */
    public List<UniversidadResponseDTO> findAllActive() {
        return universidadRepository.find("active", true).list().stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca universidad por ID
     */
    public UniversidadResponseDTO findById(Long id) {
        Universidad universidad = universidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + id));
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Busca universidad por RUC
     */
    public UniversidadResponseDTO findByRuc(String ruc) {
        Universidad universidad = universidadRepository.findByRuc(ruc)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con RUC: " + ruc));
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Busca universidades por tipo
     */
    public List<UniversidadResponseDTO> findByTipo(String tipo) {
        return universidadRepository.findByTipo(tipo).stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Búsqueda general por nombre
     */
    public List<UniversidadResponseDTO> search(String query) {
        return universidadRepository.list("LOWER(nombre) LIKE LOWER(?1)", "%" + query + "%").stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una universidad
     */
    @Transactional
    public UniversidadResponseDTO update(Long id, UniversidadRequestDTO dto) {
        Universidad universidad = universidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + id));

        // Validar RUC único (excluyendo la universidad actual)
        if (!universidad.getRuc().equals(dto.getRuc()) && 
            universidadRepository.existsByRuc(dto.getRuc())) {
            throw new DuplicateResourceException("Universidad", "ruc", dto.getRuc());
        }

        universidadMapper.updateEntityFromDto(dto, universidad);
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Elimina (lógicamente) una universidad
     */
    @Transactional
    public void delete(Long id) {
        Universidad universidad = universidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad no encontrada con ID: " + id));
        
        universidad.setActive(false);
    }
}

