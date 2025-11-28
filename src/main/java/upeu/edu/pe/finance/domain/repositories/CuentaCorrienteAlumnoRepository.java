package upeu.edu.pe.finance.domain.repositories;

import upeu.edu.pe.finance.domain.entities.CuentaCorrienteAlumno;
import java.util.List;
import java.util.Optional;

public interface CuentaCorrienteAlumnoRepository {
    CuentaCorrienteAlumno save(CuentaCorrienteAlumno cuentaCorrienteAlumno);

    Optional<CuentaCorrienteAlumno> findByIdOptional(Long id);

    List<CuentaCorrienteAlumno> listAll();

    void delete(Long id);

    List<CuentaCorrienteAlumno> search(String query);

    List<CuentaCorrienteAlumno> findByEstudianteId(Long estudianteId);
}
