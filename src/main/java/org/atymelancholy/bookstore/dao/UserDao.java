package org.atymelancholy.bookstore.dao;

import java.util.Optional;

import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.model.UserAuthRow;

public interface UserDao {

    /**
     * Find user auth row by login.
     *
     * @param login user login
     * @return auth row with password hash
     */
    Optional<UserAuthRow> findByLogin(String login);

    /**
     * Find user by id.
     *
     * @param id user id
     * @return user account if exists
     */
    Optional<UserAccount> findById(long id);

    /**
     * Insert a new user.
     *
     * @param login login
     * @param passwordHash hashed password
     * @param email email
     * @param displayName display name
     * @return generated id
     */
    long insert(String login,
                String passwordHash,
                String email,
                String displayName,
                String role);

    /**
     * Update email and display name.
     *
     * @param id user id
     * @param email email
     * @param displayName display name
     */
    void updateProfile(long id, String email, String displayName);

    /**
     * Updates role for a user identified by login.
     *
     * @param login user login
     * @param role new role
     */
    void updateRoleByLogin(String login, String role);
}
