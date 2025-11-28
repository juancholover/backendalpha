package upeu.edu.pe.academic.application.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.application.dto.PersonaRequestDTO;
import upeu.edu.pe.academic.application.dto.PersonaResponseDTO;
import upeu.edu.pe.academic.application.mapper.PersonaMapper;
import upeu.edu.pe.academic.domain.entities.Persona;
import upeu.edu.pe.academic.domain.usecases.ActualizarPersonaUseCase;
import upeu.edu.pe.academic.domain.usecases.BuscarPersonaUseCase;
import upeu.edu.pe.academic.domain.usecases.CrearPersonaUseCase;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PersonaApplicationService {

    @Inject
    CrearPersonaUseCase crearUseCase;

    @Inject
    BuscarPersonaUseCase buscarUseCase;

    @Inject
    ActualizarPersonaUseCase actualizarUseCase;

    @Inject
    PersonaMapper mapper;

    @Transactional
    public PersonaResponseDTO create(PersonaRequestDTO dto) {
        Persona persona = crearUseCase.execute(
                dto.getUniversidadId(),
                dto.getNombres(),
                dto.getApellidoPaterno(),
                dto.getApellidoMaterno(),
                dto.getTipoDocumento(),
                dto.getNumeroDocumento(),
                dto.getFechaNacimiento(),
                dto.getGenero(),
                dto.getEstadoCivil(),
                dto.getDireccion(),
                dto.getTelefono(),
                dto.getCelular(),
                dto.getEmail(),
                dto.getFotoUrl());
        return mapper.toResponseDTO(persona);
    }

    @Transactional
    public PersonaResponseDTO update(Long id, PersonaRequestDTO dto) {
        Persona persona = actualizarUseCase.execute(
                id,
                dto.getUniversidadId(),
                dto.getNombres(),
                dto.getApellidoPaterno(),
                dto.getApellidoMaterno(),
                dto.getTipoDocumento(),
                dto.getNumeroDocumento(),
                dto.getFechaNacimiento(),
                dto.getGenero(),
                dto.getEstadoCivil(),
                dto.getDireccion(),
                dto.getTelefono(),
                dto.getCelular(),
                dto.getEmail(),
                dto.getFotoUrl());
        return mapper.toResponseDTO(persona);
    }

    public PersonaResponseDTO findById(Long id) {
        return mapper.toResponseDTO(buscarUseCase.findById(id));
    }

    public PersonaResponseDTO findByNumeroDocumento(String numeroDocumento) {
        return mapper.toResponseDTO(buscarUseCase.findByNumeroDocumento(numeroDocumento));
    }

    public PersonaResponseDTO findByEmail(String email) {
        return mapper.toResponseDTO(buscarUseCase.findByEmail(email));
    }

    public List<PersonaResponseDTO> findAllActive() {
        return buscarUseCase.findAllActive().stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PersonaResponseDTO> findAll() {
        // Assuming findAllActive is what was meant by findAll in the original service
        // or we can expose findAllActive as findAll
        return findAllActive();
    }

    public List<PersonaResponseDTO> searchByNombres(String searchTerm) {
        return buscarUseCase.searchByNombres(searchTerm).stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Persona persona = buscarUseCase.findById(id);
        persona.setActive(false);
        // Repository persists automatically in transaction
    }
}
