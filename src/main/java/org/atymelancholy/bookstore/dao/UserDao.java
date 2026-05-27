package org.atymelancholy.bookstore.dao;

import java.util.Optional;

import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.model.UserAuthRow;

public interface UserDao {

    Optional<UserAuthRow> findByLogin(String login);

    Optional<UserAccount> findById(long id);

    long insert(String login, String passwordHash, String email, String displayName);

    void updateProfile(long id, String email, String displayName);
}
