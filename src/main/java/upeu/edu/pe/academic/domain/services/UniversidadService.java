package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.application.mapper.UniversidadMapper;
import upeu.edu.pe.academic.domain.commands.ActualizarUniversidadCommand;
import upeu.edu.pe.academic.domain.commands.CrearUniversidadCommand;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.academic.domain.usecases.ActualizarUniversidadUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarUniversidadUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearUniversidadUseCase;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UniversidadService {

    @Inject
    UniversidadRepository universidadRepository;

    @Inject
    UniversidadMapper universidadMapper;

    @Inject
    CrearUniversidadUseCase crearUseCase;
    
    @Inject
    ActualizarUniversidadUseCase actualizarUseCase;
    
    @Inject
    BuscarUniversidadUseCase buscarUseCase;

    
    @Transactional
    public UniversidadResponseDTO create(UniversidadRequestDTO dto) {
        // 1. Convertir DTO → Command
        CrearUniversidadCommand command = new CrearUniversidadCommand(
            dto.getCodigo(),
            dto.getNombre(),
            dto.getRuc(),
            dto.getTipo(),
            dto.getPlan(),
            dto.getDominio(),
            dto.getWebsite(),
            dto.getMaxEstudiantes(),
            dto.getMaxDocentes()
        );
        
        // 2. Ejecutar caso de uso (validaciones + persistencia)
        Universidad universidad = crearUseCase.execute(command);
        
        // 3. Convertir Entity → ResponseDTO
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Lista todas las universidades.
     */
    public List<UniversidadResponseDTO> findAll() {
        return buscarUseCase.ejecutarListarTodas().stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista solo universidades activas.
     */
    public List<UniversidadResponseDTO> findAllActive() {
        return buscarUseCase.ejecutarListarActivas().stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca universidad por ID.
     */
    public UniversidadResponseDTO findById(Long id) {
        Universidad universidad = buscarUseCase.ejecutarPorId(id);
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Busca universidad por código.
     */
    public UniversidadResponseDTO findByCodigo(String codigo) {
        Universidad universidad = buscarUseCase.ejecutarPorCodigo(codigo);
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Busca universidad por dominio.
     */
    public UniversidadResponseDTO findByDominio(String dominio) {
        Universidad universidad = universidadRepository.findByDominio(dominio)
                .orElseThrow(() -> new RuntimeException("Universidad no encontrada con dominio: " + dominio));
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Búsqueda general por nombre.
     */
    public List<UniversidadResponseDTO> search(String query) {
        return universidadRepository.list("LOWER(nombre) LIKE LOWER(?1)", "%" + query + "%").stream()
                .map(universidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza una universidad.
     * 
     * Flujo:
     * 1. DTO → Command
     * 2. Use Case (validaciones + actualización)
     * 3. Entity → ResponseDTO
     */
    @Transactional
    public UniversidadResponseDTO update(Long id, UniversidadRequestDTO dto) {
        // 1. Convertir DTO → Command
        ActualizarUniversidadCommand command = new ActualizarUniversidadCommand(
            id,
            dto.getNombre(),
            dto.getTipo(),
            dto.getPlan(),
            dto.getDominio(),
            dto.getWebsite(),
            dto.getEstado(),
            dto.getMaxEstudiantes(),
            dto.getMaxDocentes()
        );
        
        // 2. Ejecutar caso de uso
        Universidad universidad = actualizarUseCase.execute(command);
        
        // 3. Convertir Entity → ResponseDTO
        return universidadMapper.toResponseDTO(universidad);
    }

    /**
     * Elimina (lógicamente) una universidad.
     */
    @Transactional
    public void delete(Long id) {
        Universidad universidad = buscarUseCase.ejecutarPorId(id);
        universidad.setActive(false);
    }
}

