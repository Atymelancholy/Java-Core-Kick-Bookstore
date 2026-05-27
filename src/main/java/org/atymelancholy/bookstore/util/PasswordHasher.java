package org.atymelancholy.bookstore.util;

/**
 * Password hashing for storage (BCrypt implementation).
 */
public interface PasswordHasher {

    String hash(String plainTextPassword);

    boolean verify(String plainTextPassword, String storedHash);
}
