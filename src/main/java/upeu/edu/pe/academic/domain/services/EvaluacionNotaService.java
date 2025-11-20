package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.EvaluacionNotaRequestDTO;
import upeu.edu.pe.academic.application.dto.EvaluacionNotaResponseDTO;
import upeu.edu.pe.academic.application.mapper.EvaluacionNotaMapper;
import upeu.edu.pe.academic.domain.entities.EvaluacionNota;
import upeu.edu.pe.academic.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.academic.domain.repositories.EvaluacionNotaRepository;
import upeu.edu.pe.academic.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class EvaluacionNotaService {

    @Inject
    EvaluacionNotaRepository notaRepository;

    @Inject
    EvaluacionNotaMapper notaMapper;

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    public List<EvaluacionNotaResponseDTO> findByMatricula(Long matriculaId) {
        List<EvaluacionNota> notas = notaRepository.findByMatricula(matriculaId);
        return notaMapper.toResponseDTOList(notas);
    }

    public List<EvaluacionNotaResponseDTO> findByCriterio(Long criterioId) {
        List<EvaluacionNota> notas = notaRepository.findByCriterio(criterioId);
        return notaMapper.toResponseDTOList(notas);
    }

    public List<EvaluacionNotaResponseDTO> findPendientesBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findPendientesBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    public List<EvaluacionNotaResponseDTO> findCalificadasBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findCalificadasBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    public List<EvaluacionNotaResponseDTO> findByEstudianteAndSeccion(Long estudianteId, Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findByEstudianteAndSeccion(estudianteId, seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    public List<EvaluacionNotaResponseDTO> findConRecuperacionBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findConRecuperacionBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    public List<EvaluacionNotaResponseDTO> findDesaprobadasBySeccion(Long seccionId) {
        List<EvaluacionNota> notas = notaRepository.findDesaprobadasBySeccion(seccionId);
        return notaMapper.toResponseDTOList(notas);
    }

    public EvaluacionNotaResponseDTO findById(Long id) {
        EvaluacionNota nota = notaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Nota no encontrada con ID: " + id));
        return notaMapper.toResponseDTO(nota);
    }

    @Transactional
    public EvaluacionNotaResponseDTO create(EvaluacionNotaRequestDTO requestDTO) {
        // Validar que no exista ya una nota para esta matrícula y criterio
        if (notaRepository.existsByMatriculaAndCriterio(requestDTO.getMatriculaId(), requestDTO.getCriterioId())) {
            throw new BusinessException("Ya existe una nota registrada para esta matrícula y criterio");
        }

        // Obtener criterio para validaciones
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(requestDTO.getCriterioId())
                .orElseThrow(() -> new NotFoundException("Criterio no encontrado con ID: " + requestDTO.getCriterioId()));

        // Validar que la nota no exceda la nota máxima del criterio
        if (requestDTO.getNota() != null && criterio.getNotaMaxima() != null) {
            if (requestDTO.getNota().compareTo(new BigDecimal(criterio.getNotaMaxima())) > 0) {
                throw new BusinessException("La nota no puede exceder la nota máxima del criterio: " + criterio.getNotaMaxima());
            }
        }

        EvaluacionNota nota = notaMapper.toEntity(requestDTO);
        upeu.edu.pe.academic.domain.entities.Matricula matricula = new upeu.edu.pe.academic.domain.entities.Matricula();
        matricula.setId(requestDTO.getMatriculaId());
        nota.setMatricula(matricula);
        nota.setCriterio(criterio);

        // Establecer fecha de calificación si hay nota
        if (requestDTO.getNota() != null) {
            nota.setFechaCalificacion(LocalDateTime.now());
        }

        notaRepository.persist(nota);
        return notaMapper.toResponseDTO(nota);
    }

    @Transactional
    public EvaluacionNotaResponseDTO registrarNota(Long id, BigDecimal nota) {
        EvaluacionNota evaluacionNota = notaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Nota no encontrada con ID: " + id));

        // Validar que la nota no exceda la nota máxima del criterio
        var criterio = evaluacionNota.getCriterio();
        if (criterio.getNotaMaxima() != null && nota.compareTo(new BigDecimal(criterio.getNotaMaxima())) > 0) {
            throw new BusinessException("La nota no puede exceder la nota máxima del criterio: " + criterio.getNotaMaxima());
        }

        evaluacionNota.setNota(nota);
        evaluacionNota.setFechaCalificacion(LocalDateTime.now());
        evaluacionNota.setEstado("CALIFICADA");

        notaRepository.persist(evaluacionNota);
        return notaMapper.toResponseDTO(evaluacionNota);
    }

    @Transactional
    public EvaluacionNotaResponseDTO registrarRecuperacion(Long id, BigDecimal notaRecuperacion) {
        EvaluacionNota evaluacionNota = notaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Nota no encontrada con ID: " + id));

        // Validar que el criterio permita recuperación
        if (!evaluacionNota.getCriterio().getEsRecuperable()) {
            throw new BusinessException("Este criterio no permite recuperación");
        }

        // Validar que tenga una nota previa
        if (evaluacionNota.getNota() == null) {
            throw new BusinessException("Debe registrar primero la nota regular antes de la recuperación");
        }

        // Validar que la nota de recuperación no exceda la nota máxima
        var criterio = evaluacionNota.getCriterio();
        if (criterio.getNotaMaxima() != null && notaRecuperacion.compareTo(new BigDecimal(criterio.getNotaMaxima())) > 0) {
            throw new BusinessException("La nota de recuperación no puede exceder la nota máxima del criterio: " + criterio.getNotaMaxima());
        }

        evaluacionNota.setNotaRecuperacion(notaRecuperacion);
        evaluacionNota.setEstado("RECUPERADA");

        notaRepository.persist(evaluacionNota);
        return notaMapper.toResponseDTO(evaluacionNota);
    }

    @Transactional
    public EvaluacionNotaResponseDTO update(Long id, EvaluacionNotaRequestDTO requestDTO) {
        EvaluacionNota nota = notaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Nota no encontrada con ID: " + id));

        // Validar que la nota no exceda la nota máxima del criterio
        EvaluacionCriterio criterio = nota.getCriterio();
        if (requestDTO.getNota() != null && criterio.getNotaMaxima() != null) {
            if (requestDTO.getNota().compareTo(new BigDecimal(criterio.getNotaMaxima())) > 0) {
                throw new BusinessException("La nota no puede exceder la nota máxima del criterio: " + criterio.getNotaMaxima());
            }
        }

        notaMapper.updateEntityFromDTO(requestDTO, nota);

        // Actualizar fecha de calificación si cambió la nota
        if (requestDTO.getNota() != null && nota.getFechaCalificacion() == null) {
            nota.setFechaCalificacion(LocalDateTime.now());
        }

        notaRepository.persist(nota);
        return notaMapper.toResponseDTO(nota);
    }

    @Transactional
    public void delete(Long id) {
        EvaluacionNota nota = notaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Nota no encontrada con ID: " + id));

        // Soft delete
        nota.setActive(false);
        notaRepository.persist(nota);
    }

    public long countCalificadasByMatricula(Long matriculaId) {
        return notaRepository.countCalificadasByMatricula(matriculaId);
    }

    public BigDecimal getPromedioNotasByCriterio(Long criterioId) {
        Double promedio = notaRepository.getPromedioNotasByCriterio(criterioId);
        return promedio != null ? BigDecimal.valueOf(promedio) : BigDecimal.ZERO;
    }
}
