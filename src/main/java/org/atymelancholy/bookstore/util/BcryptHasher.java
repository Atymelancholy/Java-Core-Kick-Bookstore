package org.atymelancholy.bookstore.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * BCrypt-backed {@link PasswordHasher}.
 */
public final class BcryptHasher implements PasswordHasher {

    /** BCrypt cost factor (work factor). */
    private final int cost;

    /**
     * Creates a BCrypt-based hasher.
     *
     * @param bcryptCost BCrypt cost factor
     */
    public BcryptHasher(final int bcryptCost) {
        this.cost = bcryptCost;
    }

    @Override
    public String hash(final String plainTextPassword) {
        return BCrypt.withDefaults()
                .hashToString(cost, plainTextPassword.toCharArray());
    }

    @Override
    public boolean verify(final String plainTextPassword,
                          final String storedHash) {
        BCrypt.Result result = BCrypt.verifyer()
                .verify(plainTextPassword.toCharArray(),
                        storedHash.toCharArray());
        return result.verified;
    }
}
