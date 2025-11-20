package upeu.edu.pe.academic.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.ProgramaAcademicoRequestDTO;
import upeu.edu.pe.academic.application.dto.ProgramaAcademicoResponseDTO;
import upeu.edu.pe.academic.application.mapper.ProgramaAcademicoMapper;
import upeu.edu.pe.academic.domain.entities.ProgramaAcademico;
import upeu.edu.pe.academic.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.academic.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.academic.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ProgramaAcademicoService {

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Inject
    ProgramaAcademicoMapper programaMapper;

    public List<ProgramaAcademicoResponseDTO> findAll() {
        List<ProgramaAcademico> programas = programaRepository.findAllActive();
        return programaMapper.toResponseDTOList(programas);
    }

    public ProgramaAcademicoResponseDTO findById(Long id) {
        ProgramaAcademico programa = programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + id));
        return programaMapper.toResponseDTO(programa);
    }

    public ProgramaAcademicoResponseDTO findByCodigo(String codigo) {
        ProgramaAcademico programa = programaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con código: " + codigo));
        return programaMapper.toResponseDTO(programa);
    }

    public List<ProgramaAcademicoResponseDTO> findByUnidadOrganizativa(Long unidadOrganizativaId) {
        List<ProgramaAcademico> programas = programaRepository.findByUnidadOrganizativa(unidadOrganizativaId);
        return programaMapper.toResponseDTOList(programas);
    }

    public List<ProgramaAcademicoResponseDTO> findByNivelAcademico(String nivelAcademico) {
        List<ProgramaAcademico> programas = programaRepository.findByNivelAcademico(nivelAcademico);
        return programaMapper.toResponseDTOList(programas);
    }

    public List<ProgramaAcademicoResponseDTO> findByModalidad(String modalidad) {
        List<ProgramaAcademico> programas = programaRepository.findByModalidad(modalidad);
        return programaMapper.toResponseDTOList(programas);
    }

    public List<ProgramaAcademicoResponseDTO> findProgramasActivos() {
        List<ProgramaAcademico> programas = programaRepository.findProgramasActivos();
        return programaMapper.toResponseDTOList(programas);
    }

    @Transactional
    public ProgramaAcademicoResponseDTO create(ProgramaAcademicoRequestDTO requestDTO) {
        // Validar que no exista el código
        if (programaRepository.existsByCodigo(requestDTO.getCodigo())) {
            throw new BusinessException("Ya existe un programa académico con el código: " + requestDTO.getCodigo());
        }

        // Validar que exista la unidad organizativa
        UnidadOrganizativa unidadOrganizativa = unidadRepository.findByIdOptional(requestDTO.getUnidadOrganizativaId())
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + requestDTO.getUnidadOrganizativaId()));

        // Crear el programa
        ProgramaAcademico programa = programaMapper.toEntity(requestDTO);
        programa.setUnidadOrganizativa(unidadOrganizativa);

        programaRepository.persist(programa);
        return programaMapper.toResponseDTO(programa);
    }

    @Transactional
    public ProgramaAcademicoResponseDTO update(Long id, ProgramaAcademicoRequestDTO requestDTO) {
        ProgramaAcademico programa = programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + id));

        // Validar código único si cambió
        if (!programa.getCodigo().equals(requestDTO.getCodigo()) && 
            programaRepository.existsByCodigoAndIdNot(requestDTO.getCodigo(), id)) {
            throw new BusinessException("Ya existe un programa académico con el código: " + requestDTO.getCodigo());
        }

        // Validar unidad organizativa si cambió
        if (!programa.getUnidadOrganizativa().getId().equals(requestDTO.getUnidadOrganizativaId())) {
            UnidadOrganizativa unidadOrganizativa = unidadRepository.findByIdOptional(requestDTO.getUnidadOrganizativaId())
                    .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + requestDTO.getUnidadOrganizativaId()));
            programa.setUnidadOrganizativa(unidadOrganizativa);
        }

        programaMapper.updateEntityFromDTO(requestDTO, programa);
        programaRepository.persist(programa);
        return programaMapper.toResponseDTO(programa);
    }

    @Transactional
    public void delete(Long id) {
        ProgramaAcademico programa = programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + id));

        // Soft delete
        programa.setActive(false);
        programaRepository.persist(programa);
    }
}
