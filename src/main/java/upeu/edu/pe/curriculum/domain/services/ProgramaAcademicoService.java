package upeu.edu.pe.curriculum.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.curriculum.application.dto.ProgramaAcademicoResponseDTO;
import upeu.edu.pe.curriculum.application.mapper.ProgramaAcademicoMapper;
import upeu.edu.pe.curriculum.domain.entities.ProgramaAcademico;
import upeu.edu.pe.curriculum.domain.repositories.ProgramaAcademicoRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de programas académicos.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA se manejan en Use Cases.
 */
@ApplicationScoped
public class ProgramaAcademicoService {

    @Inject
    ProgramaAcademicoRepository programaRepository;

    @Inject
    ProgramaAcademicoMapper programaMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

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

    /**
     * Obtener entidad por ID (para uso interno)
     */
    public ProgramaAcademico getEntityById(Long id) {
        return programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Programa académico no encontrado con ID: " + id));
    }
}
