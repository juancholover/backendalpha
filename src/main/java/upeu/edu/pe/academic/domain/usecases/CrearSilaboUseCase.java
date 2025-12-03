package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.commands.CrearSilaboCommand;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;

/**
 * Caso de uso: Crear un nuevo sílabo para un curso
 * 
 * Responsabilidades:
 * - Validar que el curso exista
 * - Validar que la universidad exista
 * - Validar que no exista ya un sílabo para ese curso/año
 * - Crear el sílabo en estado BORRADOR
 * - Registrar en historial
 */
@ApplicationScoped
public class CrearSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Inject
    CursoRepository cursoRepository;
    
    @Inject
    UniversidadRepository universidadRepository;
    
    @Transactional
    public Silabo execute(CrearSilaboCommand command, String usuarioCreador) {
        // Validar que la universidad exista
        Universidad universidad = universidadRepository.findByIdOptional(command.universidadId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Universidad no encontrada con ID: " + command.universidadId()
                ));
        
        // Validar que el curso exista
        Curso curso = cursoRepository.findByIdOptional(command.cursoId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Curso no encontrado con ID: " + command.cursoId()
                ));
        
        // Validar que no exista ya un sílabo para ese curso/año
        if (silaboRepository.existsByCursoAndAnio(command.cursoId(), command.anioAcademico(), command.universidadId())) {
            throw new IllegalArgumentException(
                "Ya existe un sílabo para el curso " + curso.getNombre() + " del año " + command.anioAcademico()
            );
        }
        
        // Crear el sílabo
        Silabo silabo = new Silabo(universidad, curso, command.anioAcademico());
        silabo.setCompetencias(command.competencias());
        silabo.setSumilla(command.sumilla());
        silabo.setBibliografia(command.bibliografia());
        silabo.setMetodologia(command.metodologia());
        silabo.setRecursosDidacticos(command.recursosDidacticos());
        silabo.setEstado("BORRADOR");
        silabo.setVersion(1);
        
        // Persistir
        silaboRepository.persist(silabo);
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "CREACION",
            usuarioCreador != null ? usuarioCreador : "SYSTEM",
            "Sílabo creado en estado BORRADOR"
        );
        silabo.getHistorial().add(historial);
        
        return silabo;
    }
}
