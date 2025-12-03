package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.commands.AgregarUnidadSilaboCommand;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.entities.SilaboUnidad;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;
import upeu.edu.pe.academic.domain.repositories.SilaboUnidadRepository;

/**
 * Caso de uso: Agregar una unidad a un sílabo
 * 
 * Responsabilidades:
 * - Validar que el sílabo exista
 * - Validar que el sílabo sea modificable
 * - Validar que el rango de semanas sea válido
 * - Validar que no se traslapen semanas con otras unidades
 * - Crear y agregar la unidad
 * - Registrar en historial
 */
@ApplicationScoped
public class AgregarUnidadSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Inject
    SilaboUnidadRepository silaboUnidadRepository;
    
    @Transactional
    public SilaboUnidad execute(AgregarUnidadSilaboCommand command, String usuarioCreador) {
        // Buscar el sílabo
        Silabo silabo = silaboRepository.findByIdOptional(command.silaboId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sílabo no encontrado con ID: " + command.silaboId()
                ));
        
        // Validar que sea modificable
        if (!silabo.esModificable()) {
            throw new IllegalStateException(
                "No se pueden agregar unidades a un sílabo en estado " + silabo.getEstado()
            );
        }
        
        // Validar rango de semanas
        if (command.semanaInicio() < 1 || command.semanaInicio() > 20) {
            throw new IllegalArgumentException(
                "La semana de inicio debe estar entre 1 y 20. Valor recibido: " + command.semanaInicio()
            );
        }
        if (command.semanaFin() < 1 || command.semanaFin() > 20) {
            throw new IllegalArgumentException(
                "La semana de fin debe estar entre 1 y 20. Valor recibido: " + command.semanaFin()
            );
        }
        if (command.semanaInicio() > command.semanaFin()) {
            throw new IllegalArgumentException(
                "La semana de inicio no puede ser mayor que la semana de fin"
            );
        }
        
        // Validar que no exista ya una unidad con ese número
        if (silaboUnidadRepository.existsByNumeroUnidad(command.silaboId(), command.numeroUnidad())) {
            throw new IllegalArgumentException(
                "Ya existe una unidad con el número " + command.numeroUnidad() + " en este sílabo"
            );
        }
        
        // Crear la unidad
        SilaboUnidad unidad = new SilaboUnidad(
            silabo,
            command.numeroUnidad(),
            command.titulo(),
            command.semanaInicio(),
            command.semanaFin()
        );
        unidad.setContenidos(command.contenidos());
        unidad.setLogroAprendizaje(command.logroAprendizaje());
        
        // Persistir
        silaboUnidadRepository.persist(unidad);
        
        // Agregar a la colección del sílabo
        silabo.getUnidades().add(unidad);
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "AGREGAR_UNIDAD",
            usuarioCreador != null ? usuarioCreador : "SYSTEM",
            "Agregada unidad " + command.numeroUnidad() + ": " + command.titulo()
        );
        silabo.getHistorial().add(historial);
        
        return unidad;
    }
}
