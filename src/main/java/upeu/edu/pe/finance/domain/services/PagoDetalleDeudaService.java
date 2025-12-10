package upeu.edu.pe.finance.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoDetalleDeudaMapper;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.repositories.PagoDetalleDeudaRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de dominio para consultas de detalle de pagos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class PagoDetalleDeudaService {

    @Inject
    PagoDetalleDeudaRepository detalleRepository;

    @Inject
    PagoDetalleDeudaMapper detalleMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

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

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public PagoDetalleDeuda getEntityById(Long id) {
        return detalleRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Detalle de pago no encontrado con ID: " + id));
    }
}
