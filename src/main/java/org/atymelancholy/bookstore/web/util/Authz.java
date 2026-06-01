package org.atymelancholy.bookstore.web.util;

import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.DomainException;

import jakarta.servlet.http.HttpSession;

/**
 * Authorization helpers for servlets.
 */
public final class Authz {

    private Authz() {
    }

    /**
     * @param session HTTP session
     * @return signed-in user
     */
    public static UserAccount requireUser(final HttpSession session) {
        return AuthService.current(session).orElseThrow();
    }

    /**
     * @param session HTTP session
     * @return signed-in administrator
     */
    public static UserAccount requireAdmin(final HttpSession session) {
        UserAccount user = requireUser(session);
        if (!user.isAdmin()) {
            throw new DomainException("error.auth.forbidden");
        }
        return user;
    }
}
