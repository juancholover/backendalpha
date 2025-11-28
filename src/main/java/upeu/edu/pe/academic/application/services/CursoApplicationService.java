package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.CursoRequestDTO;
import upeu.edu.pe.academic.application.dto.CursoResponseDTO;
import upeu.edu.pe.academic.application.mapper.CursoMapper;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.usecases.ActualizarCursoUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarCursoUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearCursoUseCase;
import upeu.edu.pe.academic.domain.usecases.EliminarCursoUseCase;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CursoApplicationService {

    @Inject
    CrearCursoUseCase crearUseCase;

    @Inject
    ActualizarCursoUseCase actualizarUseCase;

    @Inject
    BuscarCursoUseCase buscarUseCase;

    @Inject
    EliminarCursoUseCase eliminarUseCase;

    @Inject
    CursoMapper mapper;

    @Transactional
    public CursoResponseDTO create(CursoRequestDTO dto) {
        Curso curso = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getCodigoCurso(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getHorasTeoricas(),
                dto.getHorasPracticas(),
                dto.getHorasSemanales(),
                dto.getTipoCurso(),
                dto.getAreaCurricular(),
                dto.getSilaboUrl());
        return mapper.toResponseDTO(curso);
    }

    @Transactional
    public CursoResponseDTO update(Long id, CursoRequestDTO dto) {
        Curso curso = actualizarUseCase.execute(
                id,
                dto.getUniversidadId(),
                dto.getCodigoCurso(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getHorasTeoricas(),
                dto.getHorasPracticas(),
                dto.getHorasSemanales(),
                dto.getTipoCurso(),
                dto.getAreaCurricular(),
                dto.getSilaboUrl());
        return mapper.toResponseDTO(curso);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public CursoResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public CursoResponseDTO findByCodigoCurso(String codigoCurso) {
        return mapper.toResponseDTO(buscarUseCase.findByCodigoCurso(codigoCurso));
    }

    public List<CursoResponseDTO> findAll() {
        return buscarUseCase.findAllActive().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CursoResponseDTO> findByUniversidad(Long universidadId) {
        return buscarUseCase.findByUniversidad(universidadId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
