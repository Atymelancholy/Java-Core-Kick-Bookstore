package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/")
public final class HomeServlet extends BaseServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/app/products");
    }
}
