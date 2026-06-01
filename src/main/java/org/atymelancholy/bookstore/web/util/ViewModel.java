package org.atymelancholy.bookstore.web.util;

import java.util.Map;
import java.util.Optional;

import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;

import jakarta.servlet.http.HttpSession;

/**
 * Common model attributes for Thymeleaf views.
 */
public final class ViewModel {

    private ViewModel() {
    }

    /**
     * Adds {@code user} and {@code admin} flags to the model.
     *
     * @param model view model
     * @param session HTTP session
     */
    public static void putUser(final Map<String, Object> model,
                               final HttpSession session) {
        Optional<UserAccount> user = AuthService.current(session);
        model.put("user", user.orElse(null));
        model.put("admin", user.map(UserAccount::isAdmin).orElse(false));
    }
}
