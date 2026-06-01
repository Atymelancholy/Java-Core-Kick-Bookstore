package org.atymelancholy.bookstore.util;

/**
 * Password hashing for storage (BCrypt implementation).
 */
public interface PasswordHasher {

    /**
     * Hashes a password for storage.
     *
     * @param plainTextPassword password in plain text
     * @return hashed password suitable for storage
     */
    String hash(String plainTextPassword);

    /**
     * Verifies a plain-text password against a stored hash.
     *
     * @param plainTextPassword password in plain text
     * @param storedHash stored password hash
     * @return true if password matches the hash
     */
    boolean verify(String plainTextPassword, String storedHash);
}
