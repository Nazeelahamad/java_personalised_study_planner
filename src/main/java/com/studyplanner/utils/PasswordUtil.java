package com.studyplanner.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {
    private static final int ITERATIONS = 10000;
    private static final int HASH_LENGTH = 256;

    /**
     * Hash a password using SHA-256 algorithm with random salt
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            // Create hash using SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Combine salt and hash
            byte[] saltAndHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltAndHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltAndHash, salt.length, hashedPassword.length);

            // Encode to Base64
            return Base64.getEncoder().encodeToString(saltAndHash);
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    /**
     * Verify a password against its hash
     */
    public static boolean verifyPassword(String password, String hash) {
        try {
            byte[] decodedHash = Base64.getDecoder().decode(hash);

            // Extract salt (first 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(decodedHash, 0, salt, 0, 16);

            // Hash the password with the extracted salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());

            // Compare hashes (remaining bytes after salt)
            byte[] storedHash = new byte[decodedHash.length - 16];
            System.arraycopy(decodedHash, 16, storedHash, 0, storedHash.length);

            return MessageDigest.isEqual(hashedPassword, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
