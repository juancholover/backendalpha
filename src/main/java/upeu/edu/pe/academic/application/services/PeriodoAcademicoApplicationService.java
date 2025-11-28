package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.PeriodoAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.PeriodoAcademicoResponseDTO;
import upeu.edu.pe.academic.application.mapper.PeriodoAcademicoMapper;
import upeu.edu.pe.academic.domain.entities.PeriodoAcademico;
import upeu.edu.pe.academic.domain.usecases.ActualizarPeriodoAcademicoUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarPeriodoAcademicoUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearPeriodoAcademicoUseCase;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PeriodoAcademicoApplicationService {

    @Inject
    CrearPeriodoAcademicoUseCase crearUseCase;

    @Inject
    BuscarPeriodoAcademicoUseCase buscarUseCase;

    @Inject
    ActualizarPeriodoAcademicoUseCase actualizarUseCase;

    @Inject
    PeriodoAcademicoMapper mapper;

    @Transactional
    public PeriodoAcademicoResponseDTO create(PeriodoAcademicoRequestDTO dto) {
        PeriodoAcademico periodo = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getCodigoPeriodo(),
                dto.getNombre(),
                dto.getAnio(),
                dto.getTipoPeriodo(),
                dto.getNumeroPeriodo(),
                dto.getFechaInicio(),
                dto.getFechaFin(),
                dto.getFechaInicioMatricula(),
                dto.getFechaFinMatricula(),
                dto.getFechaInicioClases(),
                dto.getFechaFinClases(),
                dto.getDescripcion());
        return mapper.toResponseDTO(periodo);
    }

    @Transactional
    public PeriodoAcademicoResponseDTO update(Long id, PeriodoAcademicoRequestDTO dto) {
        PeriodoAcademico periodo = actualizarUseCase.execute(
                id,
                dto.getCodigoPeriodo(),
                dto.getNombre(),
                dto.getAnio(),
                dto.getTipoPeriodo(),
                dto.getNumeroPeriodo(),
                dto.getFechaInicio(),
                dto.getFechaFin(),
                dto.getFechaInicioMatricula(),
                dto.getFechaFinMatricula(),
                dto.getFechaInicioClases(),
                dto.getFechaFinClases(),
                dto.getEstado(),
                dto.getEsActual(),
                dto.getDescripcion());
        return mapper.toResponseDTO(periodo);
    }

    public PeriodoAcademicoResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public PeriodoAcademicoResponseDTO findByCodigoPeriodo(String codigoPeriodo) {
        return mapper.toResponseDTO(buscarUseCase.findByCodigoPeriodo(codigoPeriodo));
    }

    public List<PeriodoAcademicoResponseDTO> findAllActive() {
        return buscarUseCase.findAllActive().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PeriodoAcademicoResponseDTO> findByAnio(Integer anio) {
        return buscarUseCase.findByAnio(anio).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PeriodoAcademicoResponseDTO> findByUniversidad(Long universidadId) {
        return buscarUseCase.findByUniversidad(universidadId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PeriodoAcademicoResponseDTO findActualByUniversidad(Long universidadId) {
        return buscarUseCase.findPeriodoActual(universidadId)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "actual",
                        "universidadId: " + universidadId));
    }

    public PeriodoAcademicoResponseDTO findByCodigoAndUniversidad(String codigo, Long universidadId) {
        return buscarUseCase.findByCodigoAndUniversidad(codigo, universidadId)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("PeriodoAcademico", "codigo + universidadId",
                        codigo + " + " + universidadId));
    }

    public List<PeriodoAcademicoResponseDTO> findByAnioAndUniversidad(Integer anio, Long universidadId) {
        return buscarUseCase.findByAnioAndUniversidad(anio, universidadId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PeriodoAcademicoResponseDTO> findByEstadoAndUniversidad(String estado, Long universidadId) {
        return buscarUseCase.findByEstadoAndUniversidad(estado, universidadId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PeriodoAcademicoResponseDTO> findActivosAndUniversidad(Long universidadId) {
        return buscarUseCase.findActivosAndUniversidad(universidadId).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PeriodoAcademicoResponseDTO> findByEstado(String estado) {
        return buscarUseCase.findByEstado(estado).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PeriodoAcademicoResponseDTO marcarComoActual(Long id) {
        // Retrieve current values to keep them
        PeriodoAcademico periodo = buscarUseCase.findById(id);
        return update(id, createRequestFromEntity(periodo, true));
    }

    private PeriodoAcademicoRequestDTO createRequestFromEntity(PeriodoAcademico entity, Boolean esActual) {
        PeriodoAcademicoRequestDTO dto = new PeriodoAcademicoRequestDTO();
        dto.setUniversidadId(entity.getUniversidad().getId());
        dto.setCodigoPeriodo(entity.getCodigoPeriodo());
        dto.setNombre(entity.getNombre());
        dto.setAnio(entity.getAnio());
        dto.setTipoPeriodo(entity.getTipoPeriodo());
        dto.setNumeroPeriodo(entity.getNumeroPeriodo());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setFechaInicioMatricula(entity.getFechaInicioMatricula());
        dto.setFechaFinMatricula(entity.getFechaFinMatricula());
        dto.setFechaInicioClases(entity.getFechaInicioClases());
        dto.setFechaFinClases(entity.getFechaFinClases());
        dto.setEstado(entity.getEstado());
        dto.setEsActual(esActual != null ? esActual : entity.getEsActual());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        PeriodoAcademico periodo = buscarUseCase.findById(id);
        periodo.setActive(false);
    }
}
