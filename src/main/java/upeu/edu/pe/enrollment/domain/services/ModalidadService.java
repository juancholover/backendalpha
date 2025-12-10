package upeu.edu.pe.enrollment.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.enrollment.application.dto.ModalidadRequestDTO;
import upeu.edu.pe.enrollment.application.dto.ModalidadResponseDTO;
import upeu.edu.pe.enrollment.application.mapper.ModalidadMapper;
import upeu.edu.pe.enrollment.domain.commands.ActualizarModalidadCommand;
import upeu.edu.pe.enrollment.domain.commands.CrearModalidadCommand;
import upeu.edu.pe.enrollment.domain.entities.Modalidad;
import upeu.edu.pe.enrollment.domain.usecases.ActualizarModalidadUseCase;
import upeu.edu.pe.enrollment.domain.usecases.BuscarModalidadUseCase;
import upeu.edu.pe.enrollment.domain.usecases.CrearModalidadUseCase;

import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped
public class ModalidadService {

    @Inject
    CrearModalidadUseCase crearUseCase;
    
    @Inject
    ActualizarModalidadUseCase actualizarUseCase;
    
    @Inject
    BuscarModalidadUseCase buscarUseCase;
    
    @Inject
    ModalidadMapper modalidadMapper;

    
    @Transactional
    public ModalidadResponseDTO crear(ModalidadRequestDTO dto) {
        CrearModalidadCommand command = new CrearModalidadCommand(
            dto.getCodigo(),
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getRequiereAula(),
            dto.getRequierePlataforma(),
            dto.getPorcentajePresencialidad(),
            dto.getColorHex()
        );
        
      
        Modalidad modalidad = crearUseCase.execute(command);
        
      
        return modalidadMapper.toResponseDTO(modalidad);
    }

    @Transactional
    public ModalidadResponseDTO actualizar(Long id, ModalidadRequestDTO dto) {
      
        ActualizarModalidadCommand command = new ActualizarModalidadCommand(
            id,
            dto.getCodigo(),
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getRequiereAula(),
            dto.getRequierePlataforma(),
            dto.getPorcentajePresencialidad(),
            dto.getColorHex()
        );
        
        // Ejecutar caso de uso
        Modalidad modalidad = actualizarUseCase.execute(command);
        
 
        return modalidadMapper.toResponseDTO(modalidad);
    }

    /**
     * Buscar modalidad por ID
     */
    public ModalidadResponseDTO buscarPorId(Long id) {
        Modalidad modalidad = buscarUseCase.findById(id);
        return modalidadMapper.toResponseDTO(modalidad);
    }

 
    public ModalidadResponseDTO buscarPorCodigo(String codigo, Long universidadId) {
        Modalidad modalidad = buscarUseCase.findByCodigo(codigo, universidadId);
        return modalidadMapper.toResponseDTO(modalidad);
    }


    public List<ModalidadResponseDTO> listarPorUniversidad(Long universidadId) {
        return buscarUseCase.findByUniversidad(universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar modalidades que requieren aula física
     */
    public List<ModalidadResponseDTO> listarRequierenAula(Long universidadId) {
        return buscarUseCase.findRequiereAula(universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Listar modalidades que requieren plataforma virtual
     */
    public List<ModalidadResponseDTO> listarRequierenPlataforma(Long universidadId) {
        return buscarUseCase.findRequierePlataforma(universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar modalidades por nombre (búsqueda parcial)
     */
    public List<ModalidadResponseDTO> buscarPorNombre(String nombre, Long universidadId) {
        return buscarUseCase.findByNombre(nombre, universidadId).stream()
                .map(modalidadMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Eliminar (lógicamente) una modalidad
     */
    @Transactional
    public void eliminar(Long id) {
        Modalidad modalidad = buscarUseCase.findById(id);
        modalidad.setActive(false);
    }
}

