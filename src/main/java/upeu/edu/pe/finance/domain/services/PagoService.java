package upeu.edu.pe.finance.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.application.dto.PagoRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoMapper;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PagoService {

    @Inject
    PagoRepository pagoRepository;

    @Inject
    PagoMapper pagoMapper;

    public List<PagoResponseDTO> findByUniversidad(Long universidadId) {
        List<Pago> pagos = pagoRepository.findByUniversidad(universidadId);
        return pagoMapper.toResponseDTOList(pagos);
    }

    public List<PagoResponseDTO> findByEstudiante(Long estudianteId) {
        List<Pago> pagos = pagoRepository.findByEstudiante(estudianteId);
        return pagoMapper.toResponseDTOList(pagos);
    }

    public PagoResponseDTO findByNumeroRecibo(String numeroRecibo, Long universidadId) {
        Pago pago = pagoRepository.findByNumeroRecibo(numeroRecibo, universidadId)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con número de recibo: " + numeroRecibo));
        return pagoMapper.toResponseDTO(pago);
    }

    public List<PagoResponseDTO> findByEstado(String estado) {
        return pagoMapper.toResponseDTOList(pagoRepository.findByEstado(estado));
    }

    public List<PagoResponseDTO> findByMetodoPago(String metodoPago) {
        return pagoMapper.toResponseDTOList(pagoRepository.findByMetodoPago(metodoPago));
    }

    public List<PagoResponseDTO> findByFecha(LocalDate fecha) {
        return pagoMapper.toResponseDTOList(pagoRepository.findByFecha(fecha));
    }

    public List<PagoResponseDTO> findByCajero(String cajero) {
        return pagoMapper.toResponseDTOList(pagoRepository.findByCajero(cajero));
    }

    public List<PagoResponseDTO> findPendientesAplicarByEstudiante(Long estudianteId) {
        return pagoMapper.toResponseDTOList(pagoRepository.findPendientesAplicarByEstudiante(estudianteId));
    }

    public List<PagoResponseDTO> findByRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return pagoMapper.toResponseDTOList(pagoRepository.findByRangoFechas(fechaInicio, fechaFin));
    }

    public List<PagoResponseDTO> findByCajeroAndFecha(String cajero, LocalDate fecha) {
        List<Pago> pagos = pagoRepository.findByCajeroAndFecha(cajero, fecha);
        return pagoMapper.toResponseDTOList(pagos);
    }

    public PagoResponseDTO findById(Long id) {
        Pago pago = pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));
        return pagoMapper.toResponseDTO(pago);
    }

    @Transactional
    public PagoResponseDTO create(PagoRequestDTO requestDTO) {
        // Validar número de recibo único
        if (pagoRepository.existsByNumeroRecibo(requestDTO.getNumeroRecibo(), requestDTO.getUniversidadId())) {
            throw new BusinessException("Ya existe un pago con el número de recibo: " + requestDTO.getNumeroRecibo());
        }

        // Validar monto
        if (requestDTO.getMontoPagado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto pagado debe ser mayor a cero");
        }

        Pago pago = pagoMapper.toEntity(requestDTO);
        upeu.edu.pe.academic.domain.entities.Universidad universidad = new upeu.edu.pe.academic.domain.entities.Universidad();
        universidad.setId(requestDTO.getUniversidadId());
        pago.setUniversidad(universidad);
        
        upeu.edu.pe.academic.domain.entities.Estudiante estudiante = new upeu.edu.pe.academic.domain.entities.Estudiante();
        estudiante.setId(requestDTO.getEstudianteId());
        pago.setEstudiante(estudiante);

        // Inicializar montos
        pago.setMontoAplicado(BigDecimal.ZERO);
        pago.setMontoPendienteAplicar(requestDTO.getMontoPagado());
        
        // Establecer estado inicial
        pago.setEstado("REGISTRADO");

        pagoRepository.persist(pago);
        return pagoMapper.toResponseDTO(pago);
    }

    @Transactional
    public PagoResponseDTO update(Long id, PagoRequestDTO requestDTO) {
        Pago pago = pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));

        // Validar que si tiene aplicaciones, no se puede cambiar el monto
        if (pago.getMontoAplicado().compareTo(BigDecimal.ZERO) > 0 && 
            !pago.getMontoPagado().equals(requestDTO.getMontoPagado())) {
            throw new BusinessException("No se puede modificar el monto porque ya tiene aplicaciones a deudas");
        }

        // Validar número de recibo único
        pagoRepository.findByNumeroRecibo(requestDTO.getNumeroRecibo(), requestDTO.getUniversidadId())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new BusinessException("Ya existe un pago con el número de recibo: " + requestDTO.getNumeroRecibo());
                    }
                });

        pagoMapper.updateEntityFromDTO(requestDTO, pago);

        // Recalcular monto pendiente si cambió el monto total
        if (!pago.getMontoPagado().equals(requestDTO.getMontoPagado())) {
            pago.setMontoPendienteAplicar(requestDTO.getMontoPagado().subtract(pago.getMontoAplicado()));
        }

        pagoRepository.persist(pago);
        return pagoMapper.toResponseDTO(pago);
    }

    @Transactional
    public PagoResponseDTO anular(Long id, String motivo) {
        Pago pago = pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));

        // Validar que no tenga aplicaciones
        if (pago.getMontoAplicado().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("No se puede anular el pago porque tiene " + 
                    pago.getMontoAplicado() + " aplicado a deudas. Primero revierta las aplicaciones.");
        }

        pago.setEstado("ANULADO");
        pago.setObservaciones((pago.getObservaciones() != null ? pago.getObservaciones() + ". " : "") + 
                              "ANULADO: " + motivo);

        pagoRepository.persist(pago);
        return pagoMapper.toResponseDTO(pago);
    }

    @Transactional
    public void delete(Long id) {
        Pago pago = pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));

        // Validar que no tenga aplicaciones a deudas
        if (pago.getMontoAplicado().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("No se puede eliminar el pago porque tiene aplicaciones a deudas");
        }

        // Soft delete
        pago.setActive(false);
        pagoRepository.persist(pago);
    }

    public BigDecimal calcularTotalPagosByFecha(LocalDate fecha) {
        // Nota: Este método requiere universidadId para funcionar correctamente
        throw new UnsupportedOperationException("Use calcularTotalPagosByFecha(LocalDate fecha, Long universidadId) en su lugar");
    }

    public BigDecimal calcularTotalPagosByMetodo(String metodoPago, LocalDate fecha) {
        return pagoRepository.calcularTotalPagosByMetodo(metodoPago, fecha);
    }

    public long countByEstadoAndUniversidad(String estado, Long universidadId) {
        return pagoRepository.countByEstadoAndUniversidad(estado, universidadId);
    }
}
