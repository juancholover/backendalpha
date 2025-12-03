package upeu.edu.pe.academic.domain.usecases;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import upeu.edu.pe.academic.domain.entities.Silabo;
import upeu.edu.pe.academic.domain.entities.SilaboHistorial;
import upeu.edu.pe.academic.domain.repositories.SilaboRepository;

/**
 * Caso de uso: Enviar sílabo a revisión
 * 
 * Responsabilidades:
 * - Validar que el sílabo exista
 * - Validar que el sílabo esté en BORRADOR
 * - Validar completitud (tiene competencias, sumilla, unidades, etc.)
 * - Cambiar estado a EN_REVISION
 * - Registrar en historial
 */
@ApplicationScoped
public class EnviarSilaboRevisionUseCase {
    
    @Inject
    SilaboRepository silaboRepository;
    
    @Transactional
    public Silabo execute(Long silaboId, String usuarioSolicitante) {
        // Buscar el sílabo
        Silabo silabo = silaboRepository.findByIdOptional(silaboId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sílabo no encontrado con ID: " + silaboId
                ));
        
        // Validar que esté en BORRADOR
        if (!"BORRADOR".equals(silabo.getEstado())) {
            throw new IllegalStateException(
                "Solo se pueden enviar a revisión sílabos en estado BORRADOR. Estado actual: " + silabo.getEstado()
            );
        }
        
        // Validar completitud
        if (!silabo.estaCompleto()) {
            throw new IllegalStateException(
                "El sílabo no está completo. Debe tener competencias, sumilla, al menos una unidad, " +
                "y las actividades sumativas deben sumar 100%"
            );
        }
        
        // Cambiar estado
        silabo.enviarARevision();
        
        // Registrar en historial
        SilaboHistorial historial = new SilaboHistorial(
            silabo,
            "ENVIO_A_REVISION",
            usuarioSolicitante != null ? usuarioSolicitante : "SYSTEM",
            "Sílabo enviado a revisión"
        );
        silabo.getHistorial().add(historial);
        
        return silabo;
    }
}
