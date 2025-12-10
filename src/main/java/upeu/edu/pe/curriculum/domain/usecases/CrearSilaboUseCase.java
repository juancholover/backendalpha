package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.commands.CrearSilaboCommand;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboHistorial;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.curriculum.domain.repositories.SilaboRepository;

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
    
    @Transactional
    public Silabo execute(CrearSilaboCommand command, String usuarioCreador) {
        // Validar que el curso exista
        Curso curso = cursoRepository.findByIdOptional(command.cursoId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Curso no encontrado con ID: " + command.cursoId()
                ));
        
        // Validar que no exista ya un sílabo para ese curso/año
        if (silaboRepository.existsByCursoAndAnio(command.cursoId(), command.anioAcademico())) {
            throw new IllegalArgumentException(
                "Ya existe un sílabo para el curso " + curso.getNombre() + " del año " + command.anioAcademico()
            );
        }
        
        // Crear el sílabo
        Silabo silabo = new Silabo();
        silabo.setCurso(curso);
        silabo.setAnioAcademico(command.anioAcademico());
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

