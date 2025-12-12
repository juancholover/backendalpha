package upeu.edu.pe.curriculum.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.curriculum.domain.entities.Silabo;
import upeu.edu.pe.curriculum.domain.entities.SilaboPlantilla;
import upeu.edu.pe.curriculum.domain.repositories.SilaboRepository;
import upeu.edu.pe.curriculum.domain.repositories.SilaboPlantillaRepository;
import upeu.edu.pe.shared.exceptions.BusinessException;
import upeu.edu.pe.shared.exceptions.NotFoundException;

import java.time.LocalDate;

/**
 * Use Case: Crear una plantilla a partir de un sílabo aprobado.
 * 
 * Reglas de negocio:
 * 1. El sílabo DEBE estar en estado APROBADO
 * 2. No debe existir otra plantilla activa para el mismo sílabo
 * 3. El usuario debe tener rol sede_central (validado en controller/Casbin)
 */
@ApplicationScoped
public class CrearPlantillaSilaboUseCase {

    @Inject
    SilaboRepository silaboRepository;

    @Inject
    SilaboPlantillaRepository plantillaRepository;

    @Transactional
    public SilaboPlantilla execute(
            Long silaboId,
            String autorizadoPor,
            LocalDate fechaInicioVigencia,
            LocalDate fechaFinVigencia,
            String nivelFlexibilidad,
            String notas) {

        // 1. Validar que el sílabo existe
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
                .orElseThrow(() -> new NotFoundException("Sílabo no encontrado con ID: " + silaboId));

        // 2. Validar que el sílabo está APROBADO
        if (!"APROBADO".equals(silabo.getEstado()) && !"VIGENTE".equals(silabo.getEstado())) {
            throw new BusinessException(
                "El sílabo debe estar APROBADO o VIGENTE para crear una plantilla. " +
                "Estado actual: " + silabo.getEstado()
            );
        }

        // 3. Validar que no existe plantilla activa para este sílabo
        plantillaRepository.findBySilabo(silaboId).ifPresent(existente -> {
            if ("ACTIVA".equals(existente.getEstado())) {
                throw new BusinessException(
                    "Ya existe una plantilla activa para este sílabo (ID: " + existente.getId() + ")"
                );
            }
        });

        // 4. Validar fechas de vigencia
        if (fechaInicioVigencia != null && fechaFinVigencia != null &&
            fechaFinVigencia.isBefore(fechaInicioVigencia)) {
            throw new BusinessException(
                "La fecha fin de vigencia no puede ser anterior a la fecha de inicio"
            );
        }

        // 5. Crear la plantilla
        SilaboPlantilla plantilla = new SilaboPlantilla();
        plantilla.setSilabo(silabo);
        plantilla.setEstado("ACTIVA");
        plantilla.setAutorizadoPor(autorizadoPor);
        plantilla.setFechaAutorizacion(LocalDate.now());
        plantilla.setFechaInicioVigencia(fechaInicioVigencia);
        plantilla.setFechaFinVigencia(fechaFinVigencia);
        plantilla.setNivelFlexibilidad(nivelFlexibilidad != null ? nivelFlexibilidad : "FECHAS_Y_PONDERACION");
        plantilla.setNotas(notas);

        // 6. Marcar el sílabo como "en uso" (no se puede editar)
        silabo.setEnUso(true);
        silaboRepository.persist(silabo);

        // 7. Persistir plantilla
        plantillaRepository.persist(plantilla);

        return plantilla;
    }
}
