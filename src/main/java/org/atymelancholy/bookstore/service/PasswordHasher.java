package org.atymelancholy.bookstore.service;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Password hashing using BCrypt (cost factor 12).
 */
public final class PasswordHasher {

    private static final int COST = 12;

    private PasswordHasher() {
    }

    public static String hash(char[] password) {
        return BCrypt.withDefaults().hashToString(COST, password);
    }

    public static boolean verify(char[] password, String hash) {
        return BCrypt.verifyer().verify(password, hash).verified;
    }
}
