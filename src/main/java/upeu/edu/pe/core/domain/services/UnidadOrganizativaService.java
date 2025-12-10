package upeu.edu.pe.core.domain.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.core.application.dto.UnidadOrganizativaResponseDTO;
import upeu.edu.pe.core.application.mapper.UnidadOrganizativaMapper;
import upeu.edu.pe.core.domain.entities.UnidadOrganizativa;
import upeu.edu.pe.core.domain.repositories.UnidadOrganizativaRepository;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.util.List;

/**
 * Servicio de dominio para consultas de unidades organizativas.
 * 
 * Este servicio solo contiene operaciones de LECTURA.
 * Las operaciones de ESCRITURA (crear, actualizar, eliminar) se manejan en Use
 * Cases.
 */
@ApplicationScoped
public class UnidadOrganizativaService {

    @Inject
    UnidadOrganizativaRepository unidadRepository;

    @Inject
    UnidadOrganizativaMapper unidadMapper;

    // =====================================================
    // OPERACIONES DE CONSULTA (solo lectura)
    // =====================================================

    /**
     * Listar todas las unidades organizativas activas.
     */
    public List<UnidadOrganizativaResponseDTO> findAll() {
        return unidadMapper.toResponseDTOList(unidadRepository.findAllActive());
    }

    /**
     * Buscar unidad organizativa por ID.
     */
    public UnidadOrganizativaResponseDTO findById(Long id) {
        UnidadOrganizativa unidad = unidadRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));
        return unidadMapper.toResponseDTO(unidad);
    }

    /**
     * Listar unidades por universidad.
     */
    public List<UnidadOrganizativaResponseDTO> findByUniversidad(Long universidadId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findAllActiveUnits());
    }

    /**
     * Listar unidades por tipo.
     */
    public List<UnidadOrganizativaResponseDTO> findByTipoUnidad(Long tipoUnidadId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findByTipoUnidad(tipoUnidadId));
    }

    /**
     * Listar unidades raíz (sin padre).
     */
    public List<UnidadOrganizativaResponseDTO> findRootUnidades(Long universidadId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findRootUnidades());
    }

    /**
     * Listar unidades hijas de una unidad padre.
     */
    public List<UnidadOrganizativaResponseDTO> findByUnidadPadre(Long unidadPadreId) {
        return unidadMapper.toResponseDTOList(unidadRepository.findByUnidadPadre(unidadPadreId));
    }

    /**
     * Verificar si una unidad tiene unidades hijas.
     */
    public boolean hasUnidadesHijas(Long unidadId) {
        return unidadRepository.hasUnidadesHijas(unidadId);
    }

    /**
     * Obtener entidad por ID (para uso interno).
     */
    public UnidadOrganizativa getEntityById(Long id) {
        return unidadRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Unidad organizativa no encontrada con ID: " + id));
    }
}
