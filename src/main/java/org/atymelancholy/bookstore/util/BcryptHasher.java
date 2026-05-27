package org.atymelancholy.bookstore.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * BCrypt-backed {@link PasswordHasher}.
 */
public final class BcryptHasher implements PasswordHasher {

    private final int cost;

    public BcryptHasher(int cost) {
        this.cost = cost;
    }

    @Override
    public String hash(String plainTextPassword) {
        return BCrypt.withDefaults().hashToString(cost, plainTextPassword.toCharArray());
    }

    @Override
    public boolean verify(String plainTextPassword, String storedHash) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainTextPassword.toCharArray(), storedHash.toCharArray());
        return result.verified;
    }
}
