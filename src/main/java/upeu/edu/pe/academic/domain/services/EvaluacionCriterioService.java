package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.EvaluacionCriterioRequestDTO;
import upeu.edu.pe.academic.application.dto.EvaluacionCriterioResponseDTO;
import upeu.edu.pe.academic.application.mapper.EvaluacionCriterioMapper;
import upeu.edu.pe.academic.domain.entities.EvaluacionCriterio;
import upeu.edu.pe.academic.domain.repositories.EvaluacionCriterioRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class EvaluacionCriterioService {

    @Inject
    EvaluacionCriterioRepository criterioRepository;

    @Inject
    EvaluacionCriterioMapper criterioMapper;

    public List<EvaluacionCriterioResponseDTO> findBySeccion(Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findBySeccion(seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    public List<EvaluacionCriterioResponseDTO> findActivosBySeccion(Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findActivosBySeccion(seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    public List<EvaluacionCriterioResponseDTO> findByTipoAndSeccion(String tipoEvaluacion, Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findByTipoAndSeccion(tipoEvaluacion, seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    public List<EvaluacionCriterioResponseDTO> findRecuperablesBySeccion(Long seccionId) {
        List<EvaluacionCriterio> criterios = criterioRepository.findRecuperablesBySeccion(seccionId);
        return criterioMapper.toResponseDTOList(criterios);
    }

    public EvaluacionCriterioResponseDTO findById(Long id) {
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Criterio de evaluación no encontrado con ID: " + id));
        return criterioMapper.toResponseDTO(criterio);
    }

    @Transactional
    public EvaluacionCriterioResponseDTO create(EvaluacionCriterioRequestDTO requestDTO) {
        // Validar que nota máxima >= nota mínima de aprobación
        if (requestDTO.getNotaMinimaAprobatoria() != null && requestDTO.getNotaMaxima() != null) {
            if (requestDTO.getNotaMinimaAprobatoria().compareTo(requestDTO.getNotaMaxima()) > 0) {
                throw new BusinessException("La nota mínima de aprobación no puede ser mayor a la nota máxima");
            }
        }

        // Validar que el nombre no esté duplicado en la sección
        criterioRepository.findByNombreAndSeccion(requestDTO.getNombre(), requestDTO.getSeccionId())
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe un criterio con el nombre: " + requestDTO.getNombre());
                });

        // Calcular peso actual
        Integer pesoActualInt = criterioRepository.sumPesoBySeccion(requestDTO.getSeccionId());
        BigDecimal pesoActual = pesoActualInt != null ? new BigDecimal(pesoActualInt) : BigDecimal.ZERO;
        BigDecimal pesoTotal = pesoActual.add(new BigDecimal(requestDTO.getPeso()));

        // Validar que el peso total no exceda 100%
        if (pesoTotal.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BusinessException("El peso total de los criterios no puede exceder 100%. " +
                    "Peso actual: " + pesoActual + "%, intentando agregar: " + requestDTO.getPeso() + "%");
        }

        EvaluacionCriterio criterio = criterioMapper.toEntity(requestDTO);
        upeu.edu.pe.academic.domain.entities.CursoOfertado cursoOfertado = new upeu.edu.pe.academic.domain.entities.CursoOfertado();
        cursoOfertado.setId(requestDTO.getSeccionId());
        criterio.setCursoOfertado(cursoOfertado);

        // Si no se especifica orden, obtener el siguiente
        if (requestDTO.getOrden() == null) {
            criterio.setOrden(criterioRepository.getNextOrden(requestDTO.getSeccionId()));
        }

        criterioRepository.persist(criterio);
        return criterioMapper.toResponseDTO(criterio);
    }

    @Transactional
    public EvaluacionCriterioResponseDTO update(Long id, EvaluacionCriterioRequestDTO requestDTO) {
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Criterio de evaluación no encontrado con ID: " + id));

        // Validar nota máxima >= nota mínima de aprobación
        if (requestDTO.getNotaMinimaAprobatoria() != null && requestDTO.getNotaMaxima() != null) {
            if (requestDTO.getNotaMinimaAprobatoria().compareTo(requestDTO.getNotaMaxima()) > 0) {
                throw new BusinessException("La nota mínima de aprobación no puede ser mayor a la nota máxima");
            }
        }

        // Validar nombre duplicado
        criterioRepository.findByNombreAndSeccion(requestDTO.getNombre(), requestDTO.getSeccionId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Ya existe un criterio con el nombre: " + requestDTO.getNombre());
                    }
                });

        // Si cambia el peso, validar que el total no exceda 100%
        if (!criterio.getPeso().equals(requestDTO.getPeso())) {
            Integer pesoActualInt = criterioRepository.sumPesoBySeccion(requestDTO.getSeccionId());
            BigDecimal pesoActual = pesoActualInt != null ? new BigDecimal(pesoActualInt) : BigDecimal.ZERO;
            BigDecimal pesoSinEste = pesoActual.subtract(new BigDecimal(criterio.getPeso()));
            BigDecimal pesoTotal = pesoSinEste.add(new BigDecimal(requestDTO.getPeso()));

            if (pesoTotal.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new BusinessException("El peso total de los criterios no puede exceder 100%. " +
                        "Peso actual sin este criterio: " + pesoSinEste + "%, intentando actualizar a: " + requestDTO.getPeso() + "%");
            }
        }

        criterioMapper.updateEntityFromDTO(requestDTO, criterio);
        criterioRepository.persist(criterio);

        return criterioMapper.toResponseDTO(criterio);
    }

    @Transactional
    public void delete(Long id) {
        EvaluacionCriterio criterio = criterioRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Criterio de evaluación no encontrado con ID: " + id));

        // Validar que no tenga notas registradas
        if (criterio.getEvaluacionNotas() != null && !criterio.getEvaluacionNotas().isEmpty()) {
            throw new BusinessException("No se puede eliminar el criterio porque tiene " + 
                    criterio.getEvaluacionNotas().size() + " nota(s) registrada(s)");
        }

        // Soft delete
        criterio.setActive(false);
        criterioRepository.persist(criterio);
    }

    public boolean isPesoTotalValido(Long seccionId) {
        return criterioRepository.isPesoTotalValido(seccionId);
    }

    public BigDecimal sumPesoBySeccion(Long seccionId) {
        Integer peso = criterioRepository.sumPesoBySeccion(seccionId);
        return peso != null ? new BigDecimal(peso) : BigDecimal.ZERO;
    }

    public BigDecimal getPesoTotalBySeccion(Long seccionId) {
        Integer peso = criterioRepository.sumPesoBySeccion(seccionId);
        return peso != null ? new BigDecimal(peso) : BigDecimal.ZERO;
    }

    public long countBySeccion(Long seccionId) {
        return criterioRepository.countBySeccion(seccionId);
    }
}
