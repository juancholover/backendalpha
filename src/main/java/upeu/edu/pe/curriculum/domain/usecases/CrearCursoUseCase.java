package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.commands.CrearCursoCommand;
import upeu.edu.pe.curriculum.domain.entities.Curso;
import upeu.edu.pe.curriculum.domain.repositories.CursoRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;

/**
 * Caso de Uso: Crear un nuevo curso.
 * 
 * Reglas de negocio:
 * - Código de curso debe ser único
 * - Horas teóricas + prácticas = horas semanales
 */
@ApplicationScoped
public class CrearCursoUseCase {

    @Inject
    CursoRepository cursoRepository;

    @Transactional
    public Curso execute(CrearCursoCommand command) {

        // 1. Validar código único
        if (cursoRepository.existsByCodigoCurso(command.codigoCurso())) {
            throw new DuplicateResourceException("Curso", "codigoCurso", command.codigoCurso());
        }

        // 2. Validar coherencia de horas
        Integer horasTeoricas = command.horasTeoricas() != null ? command.horasTeoricas() : 0;
        Integer horasPracticas = command.horasPracticas() != null ? command.horasPracticas() : 0;
        Integer horasSemanales = command.horasSemanales() != null ? command.horasSemanales()
                : horasTeoricas + horasPracticas;

        if (horasTeoricas + horasPracticas != horasSemanales) {
            throw new BusinessRuleException(
                    "Las horas semanales (" + horasSemanales + ") deben ser igual a la suma de " +
                            "horas teóricas (" + horasTeoricas + ") y prácticas (" + horasPracticas + ")");
        }

        // 3. Crear curso
        Curso curso = new Curso();
        curso.setCodigoCurso(command.codigoCurso());
        curso.setNombre(command.nombreCurso());
        curso.setTipoCurso(command.tipoCurso() != null ? command.tipoCurso() : "OBLIGATORIO");
        curso.setHorasTeoricas(horasTeoricas);
        curso.setHorasPracticas(horasPracticas);
        curso.setHorasSemanales(horasSemanales);
        curso.setDescripcion(command.descripcion());

        // 4. Persistir
        cursoRepository.persist(curso);

        return curso;
    }
}
