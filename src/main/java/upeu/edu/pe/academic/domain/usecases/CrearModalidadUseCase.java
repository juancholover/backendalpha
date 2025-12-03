package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.CrearModalidadCommand;
import upeu.edu.pe.academic.domain.entities.Modalidad;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.ModalidadRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

/**
 * Caso de uso: Crear una nueva modalidad de dictado de cursos
 * 
 * Responsabilidades:
 * - Validar que la universidad exista
 * - Validar que el código no esté duplicado en la universidad
 * - Validar coherencia entre código y flags
 * - Crear y persistir la modalidad
 */
@ApplicationScoped
public class CrearModalidadUseCase {
    
    @Inject
    ModalidadRepository modalidadRepository;
    
    @Inject
    UniversidadRepository universidadRepository;
    
    public Modalidad execute(CrearModalidadCommand command) {
        // Validar que la universidad exista
        Universidad universidad = universidadRepository.findByIdOptional(command.universidadId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Universidad no encontrada con ID: " + command.universidadId()
                ));
        
        // Validar que el código no esté duplicado
        if (modalidadRepository.existsByCodigo(command.codigo(), command.universidadId())) {
            throw new IllegalArgumentException(
                "Ya existe una modalidad con el código: " + command.codigo()
            );
        }
        
        // Crear la entidad
        Modalidad modalidad = new Modalidad();
        modalidad.setUniversidad(universidad);
        modalidad.setCodigo(command.codigo());
        modalidad.setNombre(command.nombre());
        modalidad.setDescripcion(command.descripcion());
        modalidad.setRequiereAula(command.requiereAula() != null ? command.requiereAula() : true);
        modalidad.setRequierePlataforma(command.requierePlataforma() != null ? command.requierePlataforma() : false);
        modalidad.setPorcentajePresencialidad(command.porcentajePresencialidad());
        modalidad.setColorHex(command.colorHex());
        
        // La validación de coherencia se hace automáticamente en @PrePersist de la entidad
        modalidadRepository.persist(modalidad);
        
        return modalidad;
    }
}
