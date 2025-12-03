package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;

import java.util.List;

/**
 * Caso de uso: Activar un sílabo aprobado
 * 
 * Responsabilidades:
 * - Validar que el sílabo exista
 * - Validar que el sílabo esté APROBADO
 * - Activar el sílabo (lo marca como VIGENTE)
 * - Marcar como OBSOLETO cualquier otro sílabo VIGENTE del mismo curso
 * - Registrar en historial
 */
@ApplicationScoped
public class ActivarSilaboUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Transactional
    public Silabo execute(Long silaboId, String usuarioActivador) {
        // Buscar el sílabo
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sílabo no encontrado con ID: " + silaboId
                ));
        
        // Validar que esté APROBADO
        if (!silabo.estaAprobado()) {
            throw new IllegalStateException(
                "Solo se pueden activar sílabos aprobados. Estado actual: " + silabo.getEstado()
            );
        }
        
        // Buscar otros sílabos VIGENTES del mismo curso
        List<Silabo> silabosVigentes = silaboRepository.findVigenteByCurso(
            silabo.getCurso().getId(),
            silabo.getUniversidad().getId()
        );
        
        // Marcar como obsoletos
        for (Silabo silaboVigente : silabosVigentes) {
            if (!silaboVigente.getId().equals(silaboId)) {
                silaboVigente.marcarObsoleto();
                
                // Registrar en historial del sílabo obsoleto
                SilaboHistorial historialObsoleto = new SilaboHistorial(
                    silaboVigente,
                    "OBSOLESCENCIA",
                    usuarioActivador != null ? usuarioActivador : "SYSTEM",
                    "Marcado como obsoleto al activar nueva versión (ID: " + silaboId + ")"
                );
                silaboVigente.getHistorial().add(historialObsoleto);
            }
        }
        
        // Activar el nuevo sílabo
        silabo.activar();
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "ACTIVACION",
            usuarioActivador != null ? usuarioActivador : "SYSTEM",
            "Sílabo activado como versión vigente"
        );
        silabo.getHistorial().add(historial);
        
        return silabo;
    }
}
