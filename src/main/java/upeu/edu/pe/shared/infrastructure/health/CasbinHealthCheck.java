package upeu.edu.pe.shared.infrastructure.health;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.casbin.jcasbin.main.Enforcer;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import upeu.edu.pe.curriculum.domain.services.CasbinAuthorizationService;

/**
 * Health Check para verificar que Casbin RBAC esté funcionando correctamente.
 * Verifica que el enforcer esté cargado y que las políticas estén disponibles.
 */
@Readiness
@ApplicationScoped
public class CasbinHealthCheck implements HealthCheck {

    @Inject
    CasbinAuthorizationService casbinService;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Casbin RBAC health check");

        try {
            Enforcer enforcer = casbinService.getEnforcer();
            
            if (enforcer == null) {
                return responseBuilder.down()
                    .withData("status", "Enforcer not initialized")
                    .build();
            }

            // Verificar que haya políticas cargadas
            int policyCount = enforcer.getPolicy().size();
            int roleCount = enforcer.getGroupingPolicy().size();
            
            if (policyCount == 0) {
                return responseBuilder.down()
                    .withData("status", "No policies loaded")
                    .withData("policies", 0)
                    .withData("roles", roleCount)
                    .build();
            }

            // Test básico: verificar que el modelo esté cargado
            boolean modelLoaded = enforcer.getModel() != null;
            
            if (!modelLoaded) {
                return responseBuilder.down()
                    .withData("status", "Model not loaded")
                    .withData("policies", policyCount)
                    .withData("roles", roleCount)
                    .build();
            }

            // Todo correcto
            responseBuilder.up()
                .withData("status", "operational")
                .withData("policies", policyCount)
                .withData("roles", roleCount)
                .withData("model", "loaded")
                .withData("policy_file", "casbin/policy.csv")
                .withData("model_file", "casbin/model.conf");

        } catch (Exception e) {
            return responseBuilder.down()
                .withData("status", "error")
                .withData("error", e.getMessage())
                .build();
        }

        return responseBuilder.build();
    }
}
