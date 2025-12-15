package upeu.edu.pe.security.casbin;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import upeu.edu.pe.shared.context.AuditContext;

import java.io.IOException;

/**
 * JAX-RS filter that checks authorization using Casbin.
 * Runs AFTER JwtAuthenticationFilter (priority 1000 vs 2000).
 */
@Provider
@Priority(Priorities.AUTHORIZATION) // 2000 - after authentication (1000)
public class CasbinAuthorizationFilter implements ContainerRequestFilter {

    @Inject
    CasbinConfig casbinConfig;

    @Inject
    AuditContext auditContext;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = "/" + requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        System.out.println("\n=== CASBIN AUTHORIZATION FILTER ===");
        System.out.println("Path: " + path);
        System.out.println("Method: " + method);

        // Skip public endpoints (same as JwtAuthenticationFilter)
        if (isPublicEndpoint(path)) {
            System.out.println("DECISION: Public endpoint, skipping authorization");
            System.out.println("=== CASBIN FILTER END (PUBLIC) ===\n");
            return;
        }

        // Get current user from audit context (set by JwtAuthenticationFilter)
        String currentUser = auditContext.getCurrentUser();
        System.out.println("Current user: " + currentUser);

        if (currentUser == null || currentUser.isEmpty()) {
            System.out.println("ERROR: No authenticated user found");
            abortWithForbidden(requestContext, "User not authenticated");
            return;
        }

        // Check authorization with Casbin
        boolean allowed = casbinConfig.enforce(currentUser, path, method);

        if (!allowed) {
            System.out.println("DENIED: User " + currentUser + " cannot " + method + " " + path);
            abortWithForbidden(requestContext, "Access denied");
            return;
        }

        System.out.println("ALLOWED: Authorization granted");
        System.out.println("=== CASBIN FILTER END (SUCCESS) ===\n");
    }

    private boolean isPublicEndpoint(String path) {
        String normalizedPath = path.toLowerCase();

        return normalizedPath.isEmpty() ||
                normalizedPath.equals("/") ||
                normalizedPath.startsWith("/openapi") ||
                normalizedPath.startsWith("/swagger-ui") ||
                normalizedPath.startsWith("/q/") ||
                normalizedPath.startsWith("/health") ||
                normalizedPath.startsWith("/metrics") ||
                normalizedPath.startsWith("/api/v1/auth/") ||
                normalizedPath.startsWith("/api/v1/public/");
    }

    private void abortWithForbidden(ContainerRequestContext requestContext, String message) {
        System.out.println("ABORT: Returning 403 Forbidden - " + message);
        requestContext.abortWith(
                Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"" + message + "\"}")
                        .type("application/json")
                        .build());
        System.out.println("=== CASBIN FILTER END (FORBIDDEN) ===\n");
    }
}
