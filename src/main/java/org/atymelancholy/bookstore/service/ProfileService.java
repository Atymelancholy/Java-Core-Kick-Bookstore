package org.atymelancholy.bookstore.service;

import java.util.Optional;

import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.UserAccount;

/**
 * Profile updates loaded from DB.
 */
public final class ProfileService {

    private final DaoFactory daoFactory;

    public ProfileService(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    public Optional<UserAccount> refresh(long userId) {
        return daoFactory.users().findById(userId);
    }

    public UserAccount update(long userId, String email, String displayName) {
        daoFactory.users().updateProfile(userId, email, displayName);
        return daoFactory.users().findById(userId).orElseThrow();
    }
}
