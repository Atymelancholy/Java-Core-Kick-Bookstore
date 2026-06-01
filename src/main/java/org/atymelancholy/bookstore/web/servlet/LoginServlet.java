package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.DomainException;
import org.atymelancholy.bookstore.web.util.FormValidation;
import org.atymelancholy.bookstore.web.util.ViewModel;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/login")
public final class LoginServlet extends BaseServlet {

    /** Maximum password length accepted on login. */
    private static final int PASSWORD_MAX = FormValidation.PASSWORD_MAX_LEN;

    /** Logger. */
    private static final Logger LOG = LogManager.getLogger(LoginServlet.class);

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, Object> m = new HashMap<>();
        ViewModel.putUser(m, req.getSession(false));
        m.put("flash", popFlash(req, "flashLogin"));
        Views.render(getServletContext(), req, resp, "login", m);
    }

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse resp) throws IOException {
        try {
            String login = FormValidation.login(req.getParameter("login"));
            String password = FormValidation.requireLen(
                    req.getParameter("password"),
                    1,
                    PASSWORD_MAX,
                    "error.validation.password");
            Optional<UserAccount> ok = app().auth().signIn(
                    req.getSession(true),
                    login,
                    password);
            if (ok.isEmpty()) {
                req.getSession(true).setAttribute(
                        "flashLogin", "error.login.failed");
                resp.sendRedirect(req.getContextPath() + "/app/login");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashLogin", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/login");
        } catch (Exception e) {
            LOG.error("login", e);
            req.getSession(true).setAttribute("flashLogin", "error.internal");
            resp.sendRedirect(req.getContextPath() + "/app/login");
        }
    }

    static String popFlash(final HttpServletRequest req, final String key) {
        if (req.getSession(false) == null) {
            return null;
        }
        Object v = req.getSession(false).getAttribute(key);
        if (v != null) {
            req.getSession(false).removeAttribute(key);
            return String.valueOf(v);
        }
        return null;
    }
}
