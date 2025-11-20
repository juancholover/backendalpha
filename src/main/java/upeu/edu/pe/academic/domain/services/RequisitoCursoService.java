package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.RequisitoCursoRequestDTO;
import upeu.edu.pe.academic.application.dto.RequisitoCursoResponseDTO;
import upeu.edu.pe.academic.application.mapper.RequisitoCursoMapper;
import upeu.edu.pe.academic.domain.entities.RequisitoCurso;
import upeu.edu.pe.academic.domain.repositories.RequisitoCursoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class RequisitoCursoService {

    @Inject
    RequisitoCursoRepository requisitoRepository;

    @Inject
    RequisitoCursoMapper requisitoMapper;

    public List<RequisitoCursoResponseDTO> findByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findPrerequisitosByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findPrerequisitosByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findCorrequisitosByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findCorrequisitosByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findCursosQueTienenComoRequisito(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findCursosQueTienenComoRequisito(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findObligatoriosByCurso(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findObligatoriosByCurso(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    public List<RequisitoCursoResponseDTO> findAllRequisitosCascada(Long cursoId) {
        List<RequisitoCurso> requisitos = requisitoRepository.findAllRequisitosCascada(cursoId);
        return requisitoMapper.toResponseDTOList(requisitos);
    }

    @Transactional
    public RequisitoCursoResponseDTO create(RequisitoCursoRequestDTO requestDTO) {
        // Validar que el curso y el requisito no sean el mismo
        if (requestDTO.getCursoId().equals(requestDTO.getCursoRequisitoId())) {
            throw new BusinessException("Un curso no puede ser requisito de sí mismo");
        }

        // Validar que no exista el requisito
        if (requisitoRepository.existsRequisito(requestDTO.getCursoId(), requestDTO.getCursoRequisitoId())) {
            throw new BusinessException("El requisito ya existe para este curso");
        }

        // Validar dependencias circulares
        if (requisitoRepository.existsRequisito(requestDTO.getCursoRequisitoId(), requestDTO.getCursoId())) {
            throw new BusinessException("No se puede crear el requisito: existe una dependencia circular");
        }

        RequisitoCurso requisito = requisitoMapper.toEntity(requestDTO);
        upeu.edu.pe.academic.domain.entities.Curso curso = new upeu.edu.pe.academic.domain.entities.Curso();
        curso.setId(requestDTO.getCursoId());
        requisito.setCurso(curso);
        
        upeu.edu.pe.academic.domain.entities.Curso cursoRequisito = new upeu.edu.pe.academic.domain.entities.Curso();
        cursoRequisito.setId(requestDTO.getCursoRequisitoId());
        requisito.setCursoRequisito(cursoRequisito);

        requisitoRepository.persist(requisito);
        return requisitoMapper.toResponseDTO(requisito);
    }

    @Transactional
    public RequisitoCursoResponseDTO update(Long id, RequisitoCursoRequestDTO requestDTO) {
        RequisitoCurso requisito = requisitoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Requisito no encontrado con ID: " + id));

        // Validar que el curso y el requisito no sean el mismo
        if (requestDTO.getCursoId().equals(requestDTO.getCursoRequisitoId())) {
            throw new BusinessException("Un curso no puede ser requisito de sí mismo");
        }

        requisitoMapper.updateEntityFromDTO(requestDTO, requisito);
        requisitoRepository.persist(requisito);

        return requisitoMapper.toResponseDTO(requisito);
    }

    @Transactional
    public void delete(Long id) {
        RequisitoCurso requisito = requisitoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Requisito no encontrado con ID: " + id));

        // Soft delete
        requisito.setActive(false);
        requisitoRepository.persist(requisito);
    }

    public long countByCurso(Long cursoId) {
        return requisitoRepository.countByCurso(cursoId);
    }
}
