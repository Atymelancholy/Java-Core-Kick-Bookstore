package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.service.DomainException;
import org.atymelancholy.bookstore.web.util.Authz;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/products/delete")
public final class ProductDeleteServlet extends BaseServlet {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(ProductDeleteServlet.class);

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse resp)
            throws IOException {
        try {
            Authz.requireAdmin(req.getSession(false));
            String idRaw = req.getParameter("id");
            if (idRaw == null || idRaw.isBlank()) {
                throw new IllegalStateException("missing id");
            }
            long id = Long.parseLong(idRaw.strip());
            app().products().delete(id);
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashProducts", e.messageKey());
        } catch (Exception e) {
            LOG.warn("product delete {}", e.toString());
            req.getSession(true).setAttribute(
                    "flashProducts", "error.product.delete");
        }
        resp.sendRedirect(req.getContextPath() + "/app/products");
    }
}
