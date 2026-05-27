package org.atymelancholy.bookstore.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.WebKeys;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.model.UserAuthRow;
import org.atymelancholy.bookstore.util.PasswordHasher;

import jakarta.servlet.http.HttpSession;

/**
 * Sign-in / sign-up / session-backed current user.
 */
public final class AuthService {

    private static final Logger LOG = LogManager.getLogger(AuthService.class);

    private final DaoFactory daoFactory;
    private final PasswordHasher passwordHasher;

    public AuthService(DaoFactory daoFactory, PasswordHasher passwordHasher) {
        this.daoFactory = daoFactory;
        this.passwordHasher = passwordHasher;
    }

    public Optional<UserAccount> signIn(HttpSession session, String login, String password) {
        Optional<UserAuthRow> row = daoFactory.users().findByLogin(login);
        if (row.isEmpty() || !passwordHasher.verify(password, row.get().passwordHash())) {
            LOG.info("Failed sign-in for login={}", login);
            return Optional.empty();
        }
        UserAccount acc = row.get().toAccount();
        session.setAttribute(WebKeys.CURRENT_USER, acc);
        return Optional.of(acc);
    }

    public UserAccount signUp(HttpSession session, String login, String password, String email, String displayName) {
        String hash = passwordHasher.hash(password);
        long id = daoFactory.users().insert(login, hash, email, displayName);
        UserAccount acc = new UserAccount(id, login, email, displayName);
        session.setAttribute(WebKeys.CURRENT_USER, acc);
        return acc;
    }

    public void signOut(HttpSession session) {
        session.invalidate();
    }

    public static Optional<UserAccount> current(HttpSession session) {
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
