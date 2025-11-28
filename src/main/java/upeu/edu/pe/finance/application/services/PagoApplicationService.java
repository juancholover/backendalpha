package upeu.edu.pe.finance.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.finance.application.dto.PagoRequestDTO;
import upeu.edu.pe.finance.application.dto.PagoResponseDTO;
import upeu.edu.pe.finance.application.mapper.PagoMapper;
import upeu.edu.pe.finance.domain.entities.Pago;
import upeu.edu.pe.finance.domain.usecases.ActualizarPagoUseCase;
import upeu.edu.pe.finance.domain.usecases.AnularPagoUseCase;
import upeu.edu.pe.finance.domain.usecases.BuscarPagoUseCase;
import upeu.edu.pe.finance.domain.usecases.CrearPagoUseCase;
import upeu.edu.pe.finance.domain.repositories.PagoRepository;

import java.util.List;

@ApplicationScoped
public class PagoApplicationService {

    @Inject
    CrearPagoUseCase crearPagoUseCase;

    @Inject
    ActualizarPagoUseCase actualizarPagoUseCase;

    @Inject
    BuscarPagoUseCase buscarPagoUseCase;

    @Inject
    AnularPagoUseCase anularPagoUseCase;

    @Inject
    PagoRepository pagoRepository;

    @Inject
    PagoMapper pagoMapper;

    @Transactional
    public PagoResponseDTO create(PagoRequestDTO requestDTO) {
        Pago pago = crearPagoUseCase.execute(
                requestDTO.getUniversidadId(),
                requestDTO.getEstudianteId(),
                requestDTO.getNumeroRecibo(),
                requestDTO.getMontoPagado(),
                requestDTO.getMetodoPago(),
                requestDTO.getReferenciaPago(),
                requestDTO.getBanco(),
                requestDTO.getCajero(),
                requestDTO.getObservaciones());
        return pagoMapper.toResponseDTO(pago);
    }

    @Transactional
    public PagoResponseDTO update(Long id, PagoRequestDTO requestDTO) {
        Pago pago = actualizarPagoUseCase.execute(
                id,
                requestDTO.getNumeroRecibo(),
                requestDTO.getMontoPagado(),
                requestDTO.getMetodoPago(),
                requestDTO.getReferenciaPago(),
                requestDTO.getBanco(),
                requestDTO.getCajero(),
                requestDTO.getObservaciones());
        return pagoMapper.toResponseDTO(pago);
    }

    public void anular(Long id, String motivo) {
        anularPagoUseCase.execute(id, motivo);
    }

    public void delete(Long id) {
        pagoRepository.delete(id);
    }

    public PagoResponseDTO findById(Long id) {
        return pagoMapper.toResponseDTO(buscarPagoUseCase.findById(id));
    }

    public List<PagoResponseDTO> findAll() {
        return pagoMapper.toResponseDTOList(buscarPagoUseCase.findAll());
    }

    public List<PagoResponseDTO> search(String query) {
        return pagoMapper.toResponseDTOList(buscarPagoUseCase.search(query));
    }

    public List<PagoResponseDTO> findByEstudianteId(Long estudianteId) {
        return pagoMapper.toResponseDTOList(buscarPagoUseCase.findByEstudianteId(estudianteId));
    }
}
