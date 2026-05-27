package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.WebKeys;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.DomainException;
import org.atymelancholy.bookstore.web.util.FormValidation;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/profile")
public final class ProfileServlet extends BaseServlet {

    private static final Logger LOG = LogManager.getLogger(ProfileServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = AuthService.current(req.getSession(false)).orElseThrow();
        UserAccount fresh = app().profiles().refresh(user.id()).orElse(user);
        Map<String, Object> m = new HashMap<>();
        m.put("user", user);
        m.put("flash", LoginServlet.popFlash(req, "flashProfile"));
        Views.render(getServletContext(), req, resp, "profile", m);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserAccount user = AuthService.current(req.getSession(false)).orElseThrow();
            String email = FormValidation.requireLen(req.getParameter("email"), 3, 255, "error.validation.email");
            String displayName = FormValidation.requireLen(req.getParameter("displayName"), 1, 255, "error.validation.name");
            UserAccount updated = app().profiles().update(user.id(), email, displayName);
            req.getSession().setAttribute(WebKeys.CURRENT_USER, updated);
            resp.sendRedirect(req.getContextPath() + "/app/profile");
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashProfile", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/profile");
        } catch (Exception e) {
            LOG.error("profile", e);
            req.getSession(true).setAttribute("flashProfile", "error.internal");
            resp.sendRedirect(req.getContextPath() + "/app/profile");
        }
    }
}
