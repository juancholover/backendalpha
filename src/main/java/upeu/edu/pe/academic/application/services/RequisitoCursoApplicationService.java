package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.RequisitoCursoRequestDTO;
import upeu.edu.pe.academic.application.dto.RequisitoCursoResponseDTO;
import upeu.edu.pe.academic.application.mapper.RequisitoCursoMapper;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.usecases.*;

import java.util.List;

@ApplicationScoped
public class RequisitoCursoApplicationService {

    @Inject
    CrearRequisitoCursoUseCase crearUseCase;

    @Inject
    ActualizarRequisitoCursoUseCase actualizarUseCase;

    @Inject
    BuscarRequisitoCursoUseCase buscarUseCase;

    @Inject
    EliminarRequisitoCursoUseCase eliminarUseCase;

    @Inject
    RequisitoCursoMapper mapper;

    @Transactional
    public RequisitoCursoResponseDTO create(RequisitoCursoRequestDTO dto) {
        RequisitoCurso requisito = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getCursoId(),
                dto.getCursoRequisitoId(),
                dto.getTipoRequisito(),
                dto.getEsObligatorio(),
                dto.getNotaMinimaRequerida(),
                dto.getObservacion());
        return mapper.toResponseDTO(requisito);
    }

    @Transactional
    public RequisitoCursoResponseDTO update(Long id, RequisitoCursoRequestDTO dto) {
        RequisitoCurso requisito = actualizarUseCase.execute(
                id,
                dto.getCursoId(),
                dto.getCursoRequisitoId(),
                dto.getTipoRequisito(),
                dto.getEsObligatorio(),
                dto.getNotaMinimaRequerida(),
                dto.getObservacion());
        return mapper.toResponseDTO(requisito);
    }

    @Transactional
    public void delete(Long id) {
        eliminarUseCase.execute(id);
    }

    public List<RequisitoCursoResponseDTO> findByCurso(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByCurso(cursoId));
    }

    public List<RequisitoCursoResponseDTO> findPrerequisitosByCurso(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findPrerequisitosByCurso(cursoId));
    }

    public List<RequisitoCursoResponseDTO> findCorrequisitosByCurso(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findCorrequisitosByCurso(cursoId));
    }

    public List<RequisitoCursoResponseDTO> findCursosQueTienenComoRequisito(Long cursoRequisitoId) {
        return mapper.toResponseDTOList(buscarUseCase.findCursosQueTienenComoRequisito(cursoRequisitoId));
    }

    public List<RequisitoCursoResponseDTO> findObligatoriosByCurso(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findObligatoriosByCurso(cursoId));
    }

    public List<RequisitoCursoResponseDTO> findAllRequisitosCascada(Long cursoId) {
        return mapper.toResponseDTOList(buscarUseCase.findAllRequisitosCascada(cursoId));
    }

    public long countByCurso(Long cursoId) {
        return buscarUseCase.countByCurso(cursoId);
    }
}
