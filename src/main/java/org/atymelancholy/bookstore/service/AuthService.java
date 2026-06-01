package org.atymelancholy.bookstore.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.WebKeys;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.model.UserAuthRow;
import org.atymelancholy.bookstore.model.UserRoles;
import org.atymelancholy.bookstore.util.PasswordHasher;

import jakarta.servlet.http.HttpSession;

/**
 * Sign-in, sign-up, and current user stored in the HTTP session.
 */
public final class AuthService {

    /** Logger. */
    private static final Logger LOG = LogManager.getLogger(AuthService.class);

    /** DAO factory (DB access). */
    private final DaoFactory daoFactory;
    /** Password hashing/verifying strategy. */
    private final PasswordHasher passwordHasher;

    /**
     * Creates auth service.
     *
     * @param dao DAO factory
     * @param hasher password hasher
     */
    public AuthService(final DaoFactory dao, final PasswordHasher hasher) {
        this.daoFactory = dao;
        this.passwordHasher = hasher;
    }

    /**
     * Signs a user in and stores current user in the session.
     *
     * @param session HTTP session
     * @param login login
     * @param password password
     * @return user account if credentials match
     */
    public Optional<UserAccount> signIn(final HttpSession session,
                                        final String login,
                                        final String password) {
        Optional<UserAuthRow> row = daoFactory.users().findByLogin(login);
        if (row.isEmpty()
                || !passwordHasher.verify(password, row.get().passwordHash())) {
            LOG.info("Failed sign-in for login={}", login);
            return Optional.empty();
        }
        UserAccount acc = daoFactory.users()
                .findById(row.get().id())
                .orElse(row.get().toAccount());
        session.setAttribute(WebKeys.CURRENT_USER, acc);
        return Optional.of(acc);
    }

    /**
     * Signs a user up and stores current user in the session.
     *
     * @param session HTTP session
     * @param login login
     * @param password password
     * @param email email
     * @param displayName display name
     * @return created user account
     */
    public UserAccount signUp(final HttpSession session,
                              final String login,
                              final String password,
                              final String email,
                              final String displayName) {
        String hash = passwordHasher.hash(password);
        long id = daoFactory.users().insert(
                login, hash, email, displayName, UserRoles.USER);
        UserAccount acc = daoFactory.users().findById(id).orElseThrow();
        session.setAttribute(WebKeys.CURRENT_USER, acc);
        return acc;
    }

    /**
     * Signs current user out.
     *
     * @param session HTTP session
     */
    public void signOut(final HttpSession session) {
        session.invalidate();
    }

    /**
     * Reads current user from session.
     *
     * @param session HTTP session (may be null)
     * @return current user if present
     */
    public static Optional<UserAccount> current(final HttpSession session) {
        if (session == null) {
            return Optional.empty();
        }
        Object u = session.getAttribute(WebKeys.CURRENT_USER);
        if (u instanceof UserAccount ua) {
            return Optional.of(ua);
        }
        return Optional.empty();
    }
}
