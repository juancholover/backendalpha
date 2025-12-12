package upeu.edu.pe.shared.infrastructure.health;

import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Health Check para verificar la conectividad de la base de datos.
 * - @Liveness: Verifica si la aplicación está viva (puede recuperarse)
 * - @Readiness: Verifica si la aplicación está lista para recibir tráfico
 */
@Liveness
@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    @Inject
    AgroalDataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder responseBuilder = HealthCheckResponse.named("Database connection health check");

        try {
            // Intentar obtener una conexión y verificar que sea válida
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(2); // 2 segundos de timeout
                
                if (isValid) {
                    responseBuilder.up()
                        .withData("database", "PostgreSQL")
                        .withData("connection", "active")
                        .withData("validation_timeout", "2s");
                } else {
                    responseBuilder.down()
                        .withData("database", "PostgreSQL")
                        .withData("connection", "invalid")
                        .withData("reason", "Connection validation failed");
                }
            }
        } catch (SQLException e) {
            responseBuilder.down()
                .withData("database", "PostgreSQL")
                .withData("connection", "failed")
                .withData("error", e.getMessage())
                .withData("error_code", String.valueOf(e.getErrorCode()));
        } catch (Exception e) {
            responseBuilder.down()
                .withData("database", "PostgreSQL")
                .withData("connection", "failed")
                .withData("error", e.getMessage());
        }

        return responseBuilder.build();
    }
}
