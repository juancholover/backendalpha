package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import upeu.edu.pe.academic.domain.entities.Curso;
import upeu.edu.pe.academic.domain.entities.Universidad;
import upeu.edu.pe.academic.domain.repositories.CursoRepository;
import upeu.edu.pe.academic.domain.repositories.UniversidadRepository;
import upeu.edu.pe.shared.exceptions.BusinessRuleException;
import upeu.edu.pe.shared.exceptions.DuplicateResourceException;
import upeu.edu.pe.shared.exceptions.ResourceNotFoundException;

@ApplicationScoped
public class CrearCursoUseCase {

    private final CursoRepository cursoRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public CrearCursoUseCase(CursoRepository cursoRepository, UniversidadRepository universidadRepository) {
        this.cursoRepository = cursoRepository;
        this.universidadRepository = universidadRepository;
    }

    public Curso execute(Long universidadId, String codigoCurso, String nombre, String descripcion,
            Integer horasTeoricas, Integer horasPracticas, Integer horasSemanales,
            String tipoCurso, String areaCurricular, String silaboUrl) {

        // Validar que exista la universidad
        Universidad universidad = universidadRepository.findByIdOptional(universidadId)
                .filter(u -> u.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));

        // Validar que no exista el código del curso
        if (cursoRepository.existsByCodigoCurso(codigoCurso)) {
            throw new DuplicateResourceException("Curso", "codigoCurso", codigoCurso);
        }

        // Validar coherencia de horas
        if (horasTeoricas != null && horasPracticas != null && horasSemanales != null) {
            int totalHoras = horasTeoricas + horasPracticas;
            if (totalHoras != horasSemanales) {
                throw new BusinessRuleException(
                        "Las horas semanales (" + horasSemanales + ") deben ser igual a la suma de " +
                                "horas teóricas (" + horasTeoricas + ") y prácticas (" + horasPracticas + ")");
            }
        }

        Curso curso = Curso.crear(universidad, codigoCurso, nombre, descripcion, horasTeoricas,
                horasPracticas, horasSemanales, tipoCurso, areaCurricular, silaboUrl);

        cursoRepository.persist(curso);
        return curso;
    }
}
