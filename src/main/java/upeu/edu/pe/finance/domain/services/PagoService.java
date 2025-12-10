package upeu.edu.pe.finance.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoMapper;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de dominio para consultas de pagos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class PagoService {

    @Inject
    PagoRepository pagoRepository;

    @Inject
    PagoMapper pagoMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    public List<PagoResponseDTO> findByUniversidad(Long universidadId) {
        List<Pago> pagos = pagoRepository.findAllActivePagos();
        return pagoMapper.toResponseDTOList(pagos);
    }

    public List<PagoResponseDTO> findByEstudiante(Long estudianteId) {
        List<Pago> pagos = pagoRepository.findByEstudiante(estudianteId);
        return pagoMapper.toResponseDTOList(pagos);
    }

    public PagoResponseDTO findByNumeroRecibo(String numeroRecibo, Long universidadId) {
        Pago pago = pagoRepository.findByNumeroRecibo(numeroRecibo)
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

    public BigDecimal calcularTotalPagosByMetodo(String metodoPago, LocalDate fecha) {
        return pagoRepository.calcularTotalPagosByMetodo(metodoPago, fecha);
    }

    public long countByEstadoAndUniversidad(String estado, Long universidadId) {
        return pagoRepository.countByEstadoActive(estado);
    }

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public Pago getEntityById(Long id) {
        return pagoRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Pago no encontrado con ID: " + id));
    }
}
