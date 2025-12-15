package upeu.edu.pe.security.casbin;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Service for managing Casbin policies and role assignments.
 */
@ApplicationScoped
public class CasbinPolicyService {

    @Inject
    CasbinConfig casbinConfig;

    /**
     * Add a permission policy for a role.
     * Example: addPolicy("PROFESOR", "/api/v1/notas/*", "POST")
     */
    public boolean addPolicy(String role, String path, String action) {
        return casbinConfig.addPolicy(role, path, action);
    }

    /**
     * Remove a permission policy from a role.
     */
    public boolean removePolicy(String role, String path, String action) {
        return casbinConfig.removePolicy(role, path, action);
    }

    /**
     * Assign a user to a role.
     * Example: assignRole("juan@upeu.edu.pe", "PROFESOR")
     */
    public boolean assignRole(String userEmail, String role) {
        return casbinConfig.addRoleForUser(userEmail, role);
    }

    /**
     * Remove a user from a role.
     */
    public boolean removeRole(String userEmail, String role) {
        return casbinConfig.deleteRoleForUser(userEmail, role);
    }

    /**
     * Get all roles assigned to a user.
     */
    public List<String> getUserRoles(String userEmail) {
        return casbinConfig.getRolesForUser(userEmail);
    }

    /**
     * Check if a user has a specific permission.
     */
    public boolean hasPermission(String userEmail, String path, String action) {
        return casbinConfig.enforce(userEmail, path, action);
    }

    /**
     * Reload policies from database.
     * Call this after bulk changes to policies.
     */
    public void reloadPolicies() {
        casbinConfig.reloadPolicy();
    }
}
