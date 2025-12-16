package upeu.edu.pe.security.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para generar hashes de contraseña con el mismo algoritmo del sistema
 * Ejecutar este programa para obtener el hash correcto de cualquier contraseña
 */
public class GeneratePasswordHash {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    public static String encode(String rawPassword) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(rawPassword.getBytes());

            // Combine salt and hash
            byte[] saltAndHash = new byte[SALT_LENGTH + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, SALT_LENGTH);
            System.arraycopy(hashedPassword, 0, saltAndHash, SALT_LENGTH, hashedPassword.length);

            // Encode to Base64
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

    public static void main(String[] args) {
        String password = "SuperAdmin2025!";
        String hash = encode(password);

        System.out.println("===========================================");
        System.out.println("PASSWORD HASH GENERATOR");
        System.out.println("===========================================");
        System.out.println("Password: " + password);
        System.out.println("");
        System.out.println("Generated Hash (copy this):");
        System.out.println(hash);
        System.out.println("");
        System.out.println("SQL Command:");
        System.out.println("UPDATE auth_usuario");
        System.out.println("SET password_hash = '" + hash + "'");
        System.out.println("WHERE id IN (");
        System.out.println("    SELECT au.id FROM auth_usuario au");
        System.out.println("    JOIN persona p ON au.persona_id = p.id");
        System.out.println("    WHERE p.email = 'superadmin@upeu.edu.pe'");
        System.out.println(");");
        System.out.println("");
        System.out.println("===========================================");
    }
}

