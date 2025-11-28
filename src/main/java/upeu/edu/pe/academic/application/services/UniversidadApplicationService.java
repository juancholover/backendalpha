package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.UniversidadRequestDTO;
import upeu.edu.pe.academic.application.dto.UniversidadResponseDTO;
import upeu.edu.pe.academic.application.mapper.UniversidadMapper;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.usecases.ActualizarUniversidadUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarUniversidadUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearUniversidadUseCase;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UniversidadApplicationService {

    @Inject
    CrearUniversidadUseCase crearUseCase;

    @Inject
    BuscarUniversidadUseCase buscarUseCase;

    @Inject
    ActualizarUniversidadUseCase actualizarUseCase;

    @Inject
    UniversidadMapper mapper;

    @Transactional
    public UniversidadResponseDTO create(UniversidadRequestDTO dto) {
        Universidad universidad = crearUseCase.execute(
                dto.getCodigo(),
                dto.getNombre(),
                dto.getRuc(),
                dto.getTipo(),
                dto.getDominio());
        return mapper.toResponseDTO(universidad);
    }

    @Transactional
    public UniversidadResponseDTO update(Long id, UniversidadRequestDTO dto) {
        Universidad universidad = actualizarUseCase.execute(
                id,
                dto.getCodigo(),
                dto.getNombre(),
                dto.getRuc(),
                dto.getTipo(),
                dto.getDominio());
        return mapper.toResponseDTO(universidad);
    }

    public UniversidadResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public UniversidadResponseDTO findByCodigo(String codigo) {
        return mapper.toResponseDTO(buscarUseCase.findByCodigo(codigo));
    }

    public UniversidadResponseDTO findByDominio(String dominio) {
        return mapper.toResponseDTO(buscarUseCase.findByDominio(dominio));
    }

    public List<UniversidadResponseDTO> findAllActive() {
        return buscarUseCase.findAllActive().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UniversidadResponseDTO> findAll() {
        return buscarUseCase.findAll().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UniversidadResponseDTO> search(String query) {
        return buscarUseCase.search(query).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Universidad universidad = buscarUseCase.findById(id);
        universidad.setActive(false);
    }
}
