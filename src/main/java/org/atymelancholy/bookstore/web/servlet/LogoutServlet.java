package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;

import org.atymelancholy.bookstore.service.AuthService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/logout")
public final class LogoutServlet extends BaseServlet {

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse resp)
            throws IOException {
        AuthService.current(req.getSession(false))
                .ifPresent(u -> app().auth().signOut(req.getSession(false)));
        resp.sendRedirect(req.getContextPath() + "/app/products");
    }
}
