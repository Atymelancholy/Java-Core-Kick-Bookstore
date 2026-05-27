package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.service.AuthService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/products/delete")
public final class ProductDeleteServlet extends BaseServlet {

    private static final Logger LOG = LogManager.getLogger(ProductDeleteServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AuthService.current(req.getSession(false)).orElseThrow();
        try {
            String idRaw = req.getParameter("id");
            if (idRaw == null || idRaw.isBlank()) {
                throw new IllegalStateException("missing id");
            }
            long id = Long.parseLong(idRaw.strip());
            app().products().delete(id);
        } catch (Exception e) {
            LOG.warn("product delete {}", e.toString());
            req.getSession(true).setAttribute("flashProducts", "error.product.delete");
        }
        resp.sendRedirect(req.getContextPath() + "/app/products");
    }
}
