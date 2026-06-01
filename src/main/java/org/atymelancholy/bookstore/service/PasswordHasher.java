package org.atymelancholy.bookstore.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password hashing using BCrypt (cost factor 12).
 */
public final class PasswordHasher {

    /** Default BCrypt cost factor. */
    private static final int COST = 12;

    private PasswordHasher() {
    }

    /**
     * Hashes a password using BCrypt.
     *
     * @param password password
     * @return hash
     */
    public static String hash(final char[] password) {
        return BCrypt.withDefaults().hashToString(COST, password);
    }

    /**
     * Verifies password against the stored hash.
     *
     * @param password password
     * @param hash stored hash
     * @return true if matches
     */
    public static boolean verify(final char[] password, final String hash) {
        return BCrypt.verifyer().verify(password, hash).verified;
    }
}
