package upeu.edu.pe.finance.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoRequestDTO;
import upeu.edu.pe.finance.application.dto.CuentaCorrienteAlumnoResponseDTO;
import upeu.edu.pe.finance.application.mapper.CuentaCorrienteAlumnoMapper;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class CuentaCorrienteAlumnoService {

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    @Inject
    CuentaCorrienteAlumnoMapper cuentaMapper;

    public List<CuentaCorrienteAlumnoResponseDTO> findByUniversidad(Long universidadId) {
        List<CuentaCorrienteAlumno> cuentas = cuentaRepository.findByUniversidad(universidadId);
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

    @Transactional
    public CuentaCorrienteAlumnoResponseDTO create(CuentaCorrienteAlumnoRequestDTO requestDTO) {
        // Validar monto
        if (requestDTO.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto debe ser mayor a cero");
        }

        // Validar fechas
        if (requestDTO.getFechaVencimiento() != null && requestDTO.getFechaEmision() != null) {
            if (requestDTO.getFechaVencimiento().isBefore(requestDTO.getFechaEmision())) {
                throw new BusinessException("La fecha de vencimiento debe ser posterior a la fecha de emisión");
            }
        }

        // Validar duplicado por estudiante y concepto
        if (cuentaRepository.existsByEstudianteAndConcepto(requestDTO.getEstudianteId(), requestDTO.getConcepto())) {
            throw new BusinessException("Ya existe una cuenta con el mismo concepto para este estudiante");
        }

        CuentaCorrienteAlumno cuenta = cuentaMapper.toEntity(requestDTO);
        upeu.edu.pe.academic.domain.entities.Universidad universidad = new upeu.edu.pe.academic.domain.entities.Universidad();
        universidad.setId(requestDTO.getUniversidadId());
        cuenta.setUniversidad(universidad);
        
        upeu.edu.pe.academic.domain.entities.Estudiante estudiante = new upeu.edu.pe.academic.domain.entities.Estudiante();
        estudiante.setId(requestDTO.getEstudianteId());
        cuenta.setEstudiante(estudiante);

        // Inicializar montos
        cuenta.setMontoPagado(BigDecimal.ZERO);
        cuenta.setMontoPendiente(requestDTO.getMonto());

        cuentaRepository.persist(cuenta);
        return cuentaMapper.toResponseDTO(cuenta);
    }

    @Transactional
    public CuentaCorrienteAlumnoResponseDTO update(Long id, CuentaCorrienteAlumnoRequestDTO requestDTO) {
        CuentaCorrienteAlumno cuenta = cuentaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cuenta corriente no encontrada con ID: " + id));

        // Validar que si tiene pagos, no se puede cambiar el monto original
        if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0 && 
            !cuenta.getMonto().equals(requestDTO.getMonto())) {
            throw new BusinessException("No se puede modificar el monto porque ya tiene pagos aplicados");
        }

        // Validar fechas
        if (requestDTO.getFechaVencimiento() != null && requestDTO.getFechaEmision() != null) {
            if (requestDTO.getFechaVencimiento().isBefore(requestDTO.getFechaEmision())) {
                throw new BusinessException("La fecha de vencimiento debe ser posterior a la fecha de emisión");
            }
        }

        cuentaMapper.updateEntityFromDTO(requestDTO, cuenta);

        // Recalcular monto pendiente si cambió el monto total
        if (!cuenta.getMonto().equals(requestDTO.getMonto())) {
            cuenta.setMontoPendiente(requestDTO.getMonto().subtract(cuenta.getMontoPagado()));
        }

        cuentaRepository.persist(cuenta);
        return cuentaMapper.toResponseDTO(cuenta);
    }

    @Transactional
    public CuentaCorrienteAlumnoResponseDTO aplicarPago(Long id, BigDecimal montoPago) {
        CuentaCorrienteAlumno cuenta = cuentaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cuenta corriente no encontrada con ID: " + id));

        // Validar monto
        if (montoPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto del pago debe ser mayor a cero");
        }

        if (montoPago.compareTo(cuenta.getMontoPendiente()) > 0) {
            throw new BusinessException("El monto del pago excede el monto pendiente: " + cuenta.getMontoPendiente());
        }

        // Aplicar pago
        cuenta.setMontoPagado(cuenta.getMontoPagado().add(montoPago));
        cuenta.setMontoPendiente(cuenta.getMontoPendiente().subtract(montoPago));

        // Actualizar estado
        if (cuenta.getMontoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            cuenta.setEstado("PAGADA");
        } else {
            cuenta.setEstado("PAGO_PARCIAL");
        }

        cuentaRepository.persist(cuenta);
        return cuentaMapper.toResponseDTO(cuenta);
    }

    @Transactional
    public void delete(Long id) {
        CuentaCorrienteAlumno cuenta = cuentaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Cuenta corriente no encontrada con ID: " + id));

        // Validar que no tenga pagos aplicados
        if (cuenta.getMontoPagado().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("No se puede eliminar la cuenta porque tiene pagos aplicados");
        }

        // Soft delete
        cuenta.setActive(false);
        cuentaRepository.persist(cuenta);
    }

    public BigDecimal calcularDeudaTotalByEstudiante(Long estudianteId) {
        return cuentaRepository.calcularDeudaTotalByEstudiante(estudianteId);
    }

    public long countByEstadoAndUniversidad(String estado, Long universidadId) {
        return cuentaRepository.countByEstadoAndUniversidad(estado, universidadId);
    }
}
