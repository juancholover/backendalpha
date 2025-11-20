package upeu.edu.pe.finance.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoDetalleDeudaMapper;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.finance.domain.repositories.CuentaCorrienteAlumnoRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class PagoDetalleDeudaService {

    @Inject
    PagoDetalleDeudaRepository detalleRepository;

    @Inject
    PagoDetalleDeudaMapper detalleMapper;

    @Inject
    PagoRepository pagoRepository;

    @Inject
    CuentaCorrienteAlumnoRepository cuentaRepository;

    public List<PagoDetalleDeudaResponseDTO> findByPago(Long pagoId) {
        List<PagoDetalleDeuda> detalles = detalleRepository.findByPago(pagoId);
        return detalleMapper.toResponseDTOList(detalles);
    }

    public List<PagoDetalleDeudaResponseDTO> findByDeuda(Long deudaId) {
        List<PagoDetalleDeuda> detalles = detalleRepository.findByDeuda(deudaId);
        return detalleMapper.toResponseDTOList(detalles);
    }

    public List<PagoDetalleDeudaResponseDTO> findActivosByPago(Long pagoId) {
        List<PagoDetalleDeuda> detalles = detalleRepository.findActivosByPago(pagoId);
        return detalleMapper.toResponseDTOList(detalles);
    }

    public List<PagoDetalleDeudaResponseDTO> findActivosByDeuda(Long deudaId) {
        List<PagoDetalleDeuda> detalles = detalleRepository.findActivosByDeuda(deudaId);
        return detalleMapper.toResponseDTOList(detalles);
    }

    public List<PagoDetalleDeudaResponseDTO> findRevertidosByDeuda(Long deudaId) {
        List<PagoDetalleDeuda> detalles = detalleRepository.findRevertidosByDeuda(deudaId);
        return detalleMapper.toResponseDTOList(detalles);
    }

    public PagoDetalleDeudaResponseDTO findById(Long id) {
        PagoDetalleDeuda detalle = detalleRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Detalle de pago no encontrado con ID: " + id));
        return detalleMapper.toResponseDTO(detalle);
    }

    @Transactional
    public PagoDetalleDeudaResponseDTO aplicarPagoADeuda(PagoDetalleDeudaRequestDTO requestDTO) {
        // Obtener pago y deuda
        Pago pago = pagoRepository.findByIdOptional(requestDTO.getPagoId())
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + requestDTO.getPagoId()));

        CuentaCorrienteAlumno deuda = cuentaRepository.findByIdOptional(requestDTO.getDeudaId())
                .orElseThrow(() -> new NotFoundException("Deuda no encontrada con ID: " + requestDTO.getDeudaId()));

        // Validar monto
        if (requestDTO.getMontoAplicado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto a aplicar debe ser mayor a cero");
        }

        // Validar que el pago tenga saldo disponible
        if (requestDTO.getMontoAplicado().compareTo(pago.getMontoPendienteAplicar()) > 0) {
            throw new BusinessException("El monto a aplicar excede el saldo disponible del pago: " + 
                    pago.getMontoPendienteAplicar());
        }

        // Validar que la deuda tenga monto pendiente
        if (requestDTO.getMontoAplicado().compareTo(deuda.getMontoPendiente()) > 0) {
            throw new BusinessException("El monto a aplicar excede el monto pendiente de la deuda: " + 
                    deuda.getMontoPendiente());
        }

        // Validar que no exista ya una aplicación activa entre este pago y deuda
        if (detalleRepository.existsByPagoAndDeuda(requestDTO.getPagoId(), requestDTO.getDeudaId())) {
            throw new BusinessException("Ya existe una aplicación activa de este pago a esta deuda");
        }

        // Crear detalle
        PagoDetalleDeuda detalle = detalleMapper.toEntity(requestDTO);
        detalle.setPago(pago);
        detalle.setDeuda(deuda);
        detalle.setEstado("APLICADO");
        detalle.setFechaAplicacion(LocalDateTime.now());

        // Actualizar pago
        pago.setMontoAplicado(pago.getMontoAplicado().add(requestDTO.getMontoAplicado()));
        pago.setMontoPendienteAplicar(pago.getMontoPendienteAplicar().subtract(requestDTO.getMontoAplicado()));
        
        if (pago.getMontoPendienteAplicar().compareTo(BigDecimal.ZERO) == 0) {
            pago.setEstado("APLICADO");
        } else {
            pago.setEstado("APLICADO_PARCIAL");
        }

        // Actualizar deuda
        deuda.setMontoPagado(deuda.getMontoPagado().add(requestDTO.getMontoAplicado()));
        deuda.setMontoPendiente(deuda.getMontoPendiente().subtract(requestDTO.getMontoAplicado()));
        
        if (deuda.getMontoPendiente().compareTo(BigDecimal.ZERO) == 0) {
            deuda.setEstado("PAGADA");
        } else {
            deuda.setEstado("PAGO_PARCIAL");
        }

        detalleRepository.persist(detalle);
        pagoRepository.persist(pago);
        cuentaRepository.persist(deuda);

        return detalleMapper.toResponseDTO(detalle);
    }

    @Transactional
    public PagoDetalleDeudaResponseDTO revertirAplicacion(Long id, String motivo) {
        PagoDetalleDeuda detalle = detalleRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Detalle de pago no encontrado con ID: " + id));

        // Validar que esté activo
        if ("REVERTIDO".equals(detalle.getEstado())) {
            throw new BusinessException("Esta aplicación ya ha sido revertida");
        }

        // Obtener pago y deuda
        Pago pago = detalle.getPago();
        CuentaCorrienteAlumno deuda = detalle.getDeuda();

        // Revertir en pago
        pago.setMontoAplicado(pago.getMontoAplicado().subtract(detalle.getMontoAplicado()));
        pago.setMontoPendienteAplicar(pago.getMontoPendienteAplicar().add(detalle.getMontoAplicado()));
        pago.setEstado("REGISTRADO");

        // Revertir en deuda
        deuda.setMontoPagado(deuda.getMontoPagado().subtract(detalle.getMontoAplicado()));
        deuda.setMontoPendiente(deuda.getMontoPendiente().add(detalle.getMontoAplicado()));
        deuda.setEstado("PENDIENTE");

        // Actualizar detalle
        detalle.setEstado("REVERTIDO");
        detalle.setFechaReversion(LocalDateTime.now());
        detalle.setMotivoReversion(motivo);

        detalleRepository.persist(detalle);
        pagoRepository.persist(pago);
        cuentaRepository.persist(deuda);

        return detalleMapper.toResponseDTO(detalle);
    }

    @Transactional
    public void delete(Long id) {
        PagoDetalleDeuda detalle = detalleRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Detalle de pago no encontrado con ID: " + id));

        // No se permite eliminación física, solo reversión
        throw new BusinessException("No se puede eliminar un detalle de pago. Use la operación de reversión en su lugar.");
    }

    public BigDecimal calcularTotalAplicadoByDeuda(Long deudaId) {
        return detalleRepository.calcularTotalAplicadoByDeuda(deudaId);
    }

    public BigDecimal calcularTotalAplicadoByPago(Long pagoId) {
        return detalleRepository.calcularTotalAplicadoByPago(pagoId);
    }

    public long countByPago(Long pagoId) {
        return detalleRepository.countByPago(pagoId);
    }

    public long countByDeuda(Long deudaId) {
        return detalleRepository.countByDeuda(deudaId);
    }
}
