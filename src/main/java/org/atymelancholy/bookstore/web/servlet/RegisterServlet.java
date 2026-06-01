package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.DomainException;
import org.atymelancholy.bookstore.web.util.FormValidation;
import org.atymelancholy.bookstore.web.util.ViewModel;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/register")
public final class RegisterServlet extends BaseServlet {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(RegisterServlet.class);

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, Object> m = new HashMap<>();
        ViewModel.putUser(m, req.getSession(false));
        m.put("flash", LoginServlet.popFlash(req, "flashRegister"));
        Views.render(getServletContext(), req, resp, "register", m);
    }

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse resp)
            throws IOException {
        try {
            String login = FormValidation.login(req.getParameter("login"));
            String password = FormValidation.requireLen(
                    req.getParameter("password"),
                    FormValidation.PASSWORD_MIN_LEN,
                    FormValidation.PASSWORD_MAX_LEN,
                    "error.validation.password");
            String email = FormValidation.requireLen(
                    req.getParameter("email"),
                    FormValidation.EMAIL_MIN_LEN,
                    FormValidation.TEXT_MAX_LEN,
                    "error.validation.email");
            String displayName = FormValidation.requireLen(
                    req.getParameter("displayName"),
                    1,
                    FormValidation.TEXT_MAX_LEN,
                    "error.validation.name");
            app().auth().signUp(
                    req.getSession(true),
                    login,
                    password,
                    email,
                    displayName);
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (DomainException e) {
            req.getSession(true).setAttribute(
                    "flashRegister", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/register");
        } catch (Exception e) {
            LOG.error("register", e);
            if (isUniqueViolation(e)) {
                req.getSession(true).setAttribute(
                        "flashRegister", "error.register.duplicate");
            } else {
                req.getSession(true).setAttribute(
                        "flashRegister", "error.internal");
            }
            resp.sendRedirect(req.getContextPath() + "/app/register");
        }
    }

    private static boolean isUniqueViolation(final Throwable e) {
        for (Throwable t = e; t != null; t = t.getCause()) {
            String m = t.getMessage();
            if (m != null && (m.contains("Unique") || m.contains("23505"))) {
                return true;
            }
        }
        return false;
    }
}
