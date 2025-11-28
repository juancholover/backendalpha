package upeu.edu.pe.finance.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoRequestDTO;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.application.mapper.CuentaCorrienteAlumnoMapper;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.usecases.ActualizarCuentaCorrienteAlumnoUseCase;
import upeu.edu.pe.finance.domain.usecases.BuscarCuentaCorrienteAlumnoUseCase;
import upeu.edu.pe.finance.domain.usecases.CrearCuentaCorrienteAlumnoUseCase;
import upeu.edu.pe.finance.domain.usecases.EliminarCuentaCorrienteAlumnoUseCase;

import java.util.List;

@ApplicationScoped
public class CuentaCorrienteAlumnoApplicationService {

    @Inject
    CrearCuentaCorrienteAlumnoUseCase crearCuentaCorrienteAlumnoUseCase;

    @Inject
    ActualizarCuentaCorrienteAlumnoUseCase actualizarCuentaCorrienteAlumnoUseCase;

    @Inject
    BuscarCuentaCorrienteAlumnoUseCase buscarCuentaCorrienteAlumnoUseCase;

    @Inject
    EliminarCuentaCorrienteAlumnoUseCase eliminarCuentaCorrienteAlumnoUseCase;

    @Inject
    CuentaCorrienteAlumnoMapper cuentaCorrienteAlumnoMapper;

    @Transactional
    public CuentaCorrienteAlumnoResponseDTO create(CuentaCorrienteAlumnoRequestDTO requestDTO) {
        CuentaCorrienteAlumno cta = crearCuentaCorrienteAlumnoUseCase.execute(
                requestDTO.getUniversidadId(),
                requestDTO.getEstudianteId(),
                requestDTO.getMonto(),
                requestDTO.getConcepto(),
                requestDTO.getFechaVencimiento(),
                requestDTO.getTipoCargo(),
                requestDTO.getPeriodoAcademico(),
                requestDTO.getNumeroCuota(),
                requestDTO.getObservaciones());
        return cuentaCorrienteAlumnoMapper.toResponseDTO(cta);
    }

    @Transactional
    public CuentaCorrienteAlumnoResponseDTO update(Long id, CuentaCorrienteAlumnoRequestDTO requestDTO) {
        CuentaCorrienteAlumno cta = actualizarCuentaCorrienteAlumnoUseCase.execute(
                id,
                requestDTO.getMonto(),
                requestDTO.getConcepto(),
                requestDTO.getFechaVencimiento(),
                requestDTO.getTipoCargo(),
                requestDTO.getPeriodoAcademico(),
                requestDTO.getNumeroCuota(),
                requestDTO.getObservaciones());
        return cuentaCorrienteAlumnoMapper.toResponseDTO(cta);
    }

    public void delete(Long id) {
        eliminarCuentaCorrienteAlumnoUseCase.execute(id);
    }

    public CuentaCorrienteAlumnoResponseDTO findById(Long id) {
        return cuentaCorrienteAlumnoMapper.toResponseDTO(buscarCuentaCorrienteAlumnoUseCase.findById(id));
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findAll() {
        return cuentaCorrienteAlumnoMapper.toResponseDTOList(buscarCuentaCorrienteAlumnoUseCase.findAll());
    }

    public List<CuentaCorrienteAlumnoResponseDTO> search(String query) {
        return cuentaCorrienteAlumnoMapper.toResponseDTOList(buscarCuentaCorrienteAlumnoUseCase.search(query));
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findByEstudianteId(Long estudianteId) {
        return cuentaCorrienteAlumnoMapper
                .toResponseDTOList(buscarCuentaCorrienteAlumnoUseCase.findByEstudianteId(estudianteId));
    }
}
