package upeu.edu.pe.finance.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoDetalleDeudaMapper;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;
import upeu.edu.pe.finance.domain.usecases.BuscarPagoDetalleDeudaUseCase;
import upeu.edu.pe.finance.domain.usecases.CrearPagoDetalleDeudaUseCase;
import upeu.edu.pe.finance.domain.usecases.RevertirPagoDetalleDeudaUseCase;

import java.util.List;

@ApplicationScoped
public class PagoDetalleDeudaApplicationService {

    @Inject
    CrearPagoDetalleDeudaUseCase crearUseCase;

    @Inject
    RevertirPagoDetalleDeudaUseCase revertirUseCase;

    @Inject
    BuscarPagoDetalleDeudaUseCase buscarUseCase;

    @Inject
    PagoDetalleDeudaMapper mapper;

    public PagoDetalleDeudaResponseDTO aplicarPagoADeuda(PagoDetalleDeudaRequestDTO requestDTO) {
        PagoDetalleDeuda detalle = crearUseCase.execute(
                requestDTO.getPagoId(),
                requestDTO.getDeudaId(),
                requestDTO.getMontoAplicado(),
                requestDTO.getAplicadoPor(),
                requestDTO.getObservaciones());
        return mapper.toResponseDTO(detalle);
    }

    public PagoDetalleDeudaResponseDTO revertirAplicacion(Long id, String motivo) {
        revertirUseCase.execute(id, motivo);
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public PagoDetalleDeudaResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public List<PagoDetalleDeudaResponseDTO> findByPago(Long pagoId) {
        return mapper.toResponseDTOList(buscarUseCase.findByPagoId(pagoId));
    }

    public List<PagoDetalleDeudaResponseDTO> findByDeuda(Long deudaId) {
        return mapper.toResponseDTOList(buscarUseCase.findByDeudaId(deudaId));
    }
}
