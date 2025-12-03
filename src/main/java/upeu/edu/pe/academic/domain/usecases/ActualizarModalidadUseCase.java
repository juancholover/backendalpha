package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.commands.ActualizarModalidadCommand;
import upeu.edu.pe.academic.domain.entities.Modalidad;
import upeu.edu.pe.academic.domain.repositories.ModalidadRepository;

/**
 * Caso de uso: Actualizar una modalidad existente
 * 
 * Responsabilidades:
 * - Validar que la modalidad exista
 * - Validar que no se duplique el código si cambia
 * - Actualizar los campos permitidos
 */
@ApplicationScoped
public class ActualizarModalidadUseCase {
    
    @Inject
    ModalidadRepository modalidadRepository;
    
    public Modalidad execute(ActualizarModalidadCommand command) {
        // Buscar la modalidad existente
        Modalidad modalidad = modalidadRepository.findByIdOptional(command.id())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Modalidad no encontrada con ID: " + command.id()
                ));
        
        // Si el código cambió, validar que no esté duplicado
        if (!modalidad.getCodigo().equals(command.codigo())) {
            if (modalidadRepository.existsByCodigo(command.codigo(), modalidad.getUniversidad().getId())) {
                throw new IllegalArgumentException(
                    "Ya existe una modalidad con el código: " + command.codigo()
                );
            }
        }
        
        // Actualizar campos
        modalidad.setCodigo(command.codigo());
        modalidad.setNombre(command.nombre());
        modalidad.setDescripcion(command.descripcion());
        modalidad.setRequiereAula(command.requiereAula());
        modalidad.setRequierePlataforma(command.requierePlataforma());
        modalidad.setPorcentajePresencialidad(command.porcentajePresencialidad());
        modalidad.setColorHex(command.colorHex());
        
        // La validación de coherencia se hace automáticamente en @PreUpdate
        modalidadRepository.persist(modalidad);
        
        return modalidad;
    }
}
