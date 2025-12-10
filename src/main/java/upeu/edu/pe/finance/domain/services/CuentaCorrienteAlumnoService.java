package upeu.edu.pe.finance.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.application.mapper.CuentaCorrienteAlumnoMapper;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de dominio para consultas de cuentas corrientes.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class CuentaCorrienteAlumnoService {

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    @Inject
    CuentaCorrienteAlumnoMapper cuentaMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<CuentaCorrienteAlumnoResponseDTO> findByUniversidad(Long universidadId) {
        List<CuentaCorrienteAlumno> cuentas = cuentaRepository.findAllActive();
        return cuentaMapper.toResponseDTOList(cuentas);
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findByEstudiante(Long estudianteId) {
        List<CuentaCorrienteAlumno> cuentas = cuentaRepository.findByEstudiante(estudianteId);
        return cuentaMapper.toResponseDTOList(cuentas);
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findVencidasByEstudiante(Long estudianteId) {
        List<CuentaCorrienteAlumno> cuentas = cuentaRepository.findVencidasByEstudiante(estudianteId);
        return cuentaMapper.toResponseDTOList(cuentas);
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findPendientesByEstudiante(Long estudianteId) {
        List<CuentaCorrienteAlumno> cuentas = cuentaRepository.findPendientesByEstudiante(estudianteId);
        return cuentaMapper.toResponseDTOList(cuentas);
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findProximasVencerByEstudiante(Long estudianteId, Integer dias) {
        List<CuentaCorrienteAlumno> cuentas = cuentaRepository.findProximasVencerByEstudiante(estudianteId, dias);
        return cuentaMapper.toResponseDTOList(cuentas);
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findByTipoCargo(String tipoCargo) {
        return cuentaRepository.findByTipoCargo(tipoCargo)
                .stream()
                .map(cuentaMapper::toResponseDTO)
                .toList();
    }

    public List<CuentaCorrienteAlumnoResponseDTO> findByEstado(String estado) {
        return cuentaRepository.findByEstado(estado)
                .stream()
                .map(cuentaMapper::toResponseDTO)
                .toList();
    }

    public CuentaCorrienteAlumnoResponseDTO findById(Long id) {
        CuentaCorrienteAlumno cuenta = cuentaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cuenta corriente no encontrada con ID: " + id));
        return cuentaMapper.toResponseDTO(cuenta);
    }

    public BigDecimal calcularDeudaTotalByEstudiante(Long estudianteId) {
        return cuentaRepository.calcularDeudaTotalByEstudiante(estudianteId);
    }

    public long countByEstadoAndUniversidad(String estado, Long universidadId) {
        return cuentaRepository.countByEstadoActive(estado);
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public CuentaCorrienteAlumno getEntityById(Long id) {
        return cuentaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cuenta corriente no encontrada con ID: " + id));
    }
}
