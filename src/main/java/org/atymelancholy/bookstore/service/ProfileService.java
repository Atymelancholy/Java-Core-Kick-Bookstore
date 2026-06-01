package org.atymelancholy.bookstore.service;

import java.util.Optional;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.UserAccount;

/**
 * Profile updates loaded from DB.
 */
public final class ProfileService {

    /** DAO factory. */
    private final DaoFactory daoFactory;

    /**
     * Creates profile service.
     *
     * @param dao DAO factory
     */
    public ProfileService(final DaoFactory dao) {
        this.daoFactory = dao;
    }

    /**
     * Reload profile from DB.
     *
     * @param userId user id
     * @return refreshed user account
     */
    public Optional<UserAccount> refresh(final long userId) {
        return daoFactory.users().findById(userId);
    }

    /**
     * Update profile.
     *
     * @param userId user id
     * @param email email
     * @param displayName display name
     * @return updated user account
     */
    public UserAccount update(final long userId,
                              final String email,
                              final String displayName) {
        daoFactory.users().updateProfile(userId, email, displayName);
        return daoFactory.users().findById(userId).orElseThrow();
    }
}
