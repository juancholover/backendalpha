package upeu.edu.pe.security.casbin;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.adapter.JDBCAdapter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.sql.DataSource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * CDI Bean that provides the Casbin Enforcer instance.
 * Uses JDBC adapter to persist policies in PostgreSQL.
 */
@ApplicationScoped
@Startup
public class CasbinConfig {

    @Inject
    DataSource dataSource;

    @ConfigProperty(name = "casbin.model.path", defaultValue = "casbin/model.conf")
    String modelPath;

    private Enforcer enforcer;

    @PostConstruct
    void init() {
        try {
            // Load model from classpath
            InputStream modelStream = getClass().getClassLoader().getResourceAsStream(modelPath);
            if (modelStream == null) {
                throw new RuntimeException("Casbin model not found at: " + modelPath);
            }

            // Copy to temp file (Casbin needs a file path)
            Path tempModel = Files.createTempFile("casbin-model", ".conf");
            Files.copy(modelStream, tempModel, StandardCopyOption.REPLACE_EXISTING);
            modelStream.close();

            // Create JDBC adapter for PostgreSQL
            JDBCAdapter adapter = new JDBCAdapter(dataSource);

            // Initialize enforcer with model and adapter
            this.enforcer = new Enforcer(tempModel.toString(), adapter);

            // Load policies from database
            this.enforcer.loadPolicy();

            System.out.println("=== CASBIN INITIALIZED ===");
            System.out.println("Model: " + modelPath);
            System.out.println("Policies loaded from database");

            // Cleanup temp file
            Files.deleteIfExists(tempModel);

        } catch (Exception e) {
            System.err.println("ERROR initializing Casbin: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize Casbin", e);
        }
    }

    /**
     * Returns the Casbin Enforcer instance.
     */
    public Enforcer getEnforcer() {
        return enforcer;
    }

    /**
     * Check if a subject has permission to perform an action on an object.
     * 
     * @param sub The subject (username/email)
     * @param obj The object (URL path)
     * @param act The action (HTTP method)
     * @return true if permission is granted
     */
    public boolean enforce(String sub, String obj, String act) {
        if (enforcer == null) {
            System.err.println("CASBIN ERROR: Enforcer not initialized");
            return false;
        }
        boolean result = enforcer.enforce(sub, obj, act);
        System.out.println("CASBIN: enforce(" + sub + ", " + obj + ", " + act + ") = " + result);
        return result;
    }

    /**
     * Add a policy (permission for a role).
     */
    public boolean addPolicy(String role, String path, String action) {
        boolean added = enforcer.addPolicy(role, path, action);
        if (added) {
            enforcer.savePolicy();
        }
        return added;
    }

    /**
     * Remove a policy.
     */
    public boolean removePolicy(String role, String path, String action) {
        boolean removed = enforcer.removePolicy(role, path, action);
        if (removed) {
            enforcer.savePolicy();
        }
        return removed;
    }

    /**
     * Assign a user to a role.
     */
    public boolean addRoleForUser(String user, String role) {
        boolean added = enforcer.addRoleForUser(user, role);
        if (added) {
            enforcer.savePolicy();
        }
        return added;
    }

    /**
     * Remove a user from a role.
     */
    public boolean deleteRoleForUser(String user, String role) {
        boolean removed = enforcer.deleteRoleForUser(user, role);
        if (removed) {
            enforcer.savePolicy();
        }
        return removed;
    }

    /**
     * Get all roles for a user.
     */
    public java.util.List<String> getRolesForUser(String user) {
        return enforcer.getRolesForUser(user);
    }

    /**
     * Reload policies from database.
     */
    public void reloadPolicy() {
        enforcer.loadPolicy();
        System.out.println("CASBIN: Policies reloaded from database");
    }
}
