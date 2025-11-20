package upeu.edu.pe.finance.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoDetalleDeudaResponseDTO;
import upeu.edu.pe.finance.domain.entities.PagoDetalleDeuda;

import java.util.List;

@Mapper(
    componentModel = "cdi",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PagoDetalleDeudaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pago", ignore = true)
    @Mapping(target = "deuda", ignore = true)
    @Mapping(target = "fechaAplicacion", ignore = true)
    @Mapping(target = "estado", constant = "APLICADO")
    @Mapping(target = "fechaReversion", ignore = true)
    @Mapping(target = "motivoReversion", ignore = true)
    @Mapping(target = "aplicadoPor", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    PagoDetalleDeuda toEntity(PagoDetalleDeudaRequestDTO dto);

    @Mapping(target = "pagoId", source = "pago.id")
    @Mapping(target = "pagoNumeroRecibo", source = "pago.numeroRecibo")
    @Mapping(target = "pagoMontoPagado", source = "pago.montoPagado")
    @Mapping(target = "deudaId", source = "deuda.id")
    @Mapping(target = "deudaNumeroDocumento", source = "deuda.numeroCuota")
    @Mapping(target = "deudaConcepto", source = "deuda.concepto")
    @Mapping(target = "deudaMonto", source = "deuda.monto")
    PagoDetalleDeudaResponseDTO toResponseDTO(PagoDetalleDeuda entity);

    List<PagoDetalleDeudaResponseDTO> toResponseDTOList(List<PagoDetalleDeuda> entities);
}
