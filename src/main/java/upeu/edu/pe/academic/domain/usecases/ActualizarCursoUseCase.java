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
public class ActualizarCursoUseCase {

    private final CursoRepository cursoRepository;
    private final UniversidadRepository universidadRepository;

    @Inject
    public ActualizarCursoUseCase(CursoRepository cursoRepository, UniversidadRepository universidadRepository) {
        this.cursoRepository = cursoRepository;
        this.universidadRepository = universidadRepository;
    }

    public Curso execute(Long id, Long universidadId, String codigoCurso, String nombre, String descripcion,
            Integer horasTeoricas, Integer horasPracticas, Integer horasSemanales,
            String tipoCurso, String areaCurricular, String silaboUrl) {

        Curso curso = cursoRepository.findByIdOptional(id)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));

        // Validar universidad si cambió
        if (!curso.getUniversidad().getId().equals(universidadId)) {
            Universidad nuevaUniversidad = universidadRepository.findByIdOptional(universidadId)
                    .filter(u -> u.getActive())
                    .orElseThrow(() -> new ResourceNotFoundException("Universidad", "id", universidadId));
            curso.setUniversidad(nuevaUniversidad);
        }

        // Validar código de curso si cambió
        if (!curso.getCodigoCurso().equals(codigoCurso) &&
                cursoRepository.existsByCodigoCursoAndIdNot(codigoCurso, id)) {
            throw new DuplicateResourceException("Curso", "codigoCurso", codigoCurso);
        }

        // Validar coherencia de horas
        if (horasTeoricas != null && horasPracticas != null && horasSemanales != null) {
            int totalHoras = horasTeoricas + horasPracticas;
            if (totalHoras != horasSemanales) {
                throw new BusinessRuleException(
                        "Las horas semanales deben ser igual a la suma de horas teóricas y prácticas");
            }
        }

        curso.setCodigoCurso(codigoCurso);
        curso.setNombre(nombre);
        curso.setDescripcion(descripcion);
        curso.setHorasTeoricas(horasTeoricas);
        curso.setHorasPracticas(horasPracticas);
        curso.setHorasSemanales(horasSemanales);
        curso.setTipoCurso(tipoCurso);
        curso.setAreaCurricular(areaCurricular);
        curso.setSilaboUrl(silaboUrl);

        cursoRepository.persist(curso);
        return curso;
    }
}
