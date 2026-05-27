package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.model.Product;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.DomainException;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/products/form")
public final class ProductFormServlet extends BaseServlet {

    private static final Logger LOG = LogManager.getLogger(ProductFormServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = AuthService.current(req.getSession(false)).orElseThrow();
        Map<String, Object> m = new HashMap<>();
        m.put("user", user);
        String idRaw = req.getParameter("id");
        if (idRaw == null || idRaw.isBlank()) {
            m.put("product", null);
        } else {
            try {
                long id = Long.parseLong(idRaw.strip());
                Product p = app().products().find(id).orElseThrow();
                m.put("product", p);
            } catch (Exception e) {
                resp.sendRedirect(req.getContextPath() + "/app/products");
                return;
            }
        }
        Views.render(getServletContext(), req, resp, "product-form", m);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            AuthService.current(req.getSession(false)).orElseThrow();
            String idRaw = req.getParameter("id");
            String name = require(req.getParameter("name"), "error.validation.name");
            String rawDesc = req.getParameter("description");
            String description = rawDesc == null ? "" : rawDesc.strip();
            int priceCents = parseInt(req.getParameter("priceCents"), 1, 10_000_000, "error.validation.price");
            int stock = parseInt(req.getParameter("stock"), 0, 1_000_000, "error.validation.stock");
            if (idRaw == null || idRaw.isBlank()) {
                app().products().create(name, description, priceCents, stock);
            } else {
                long id = Long.parseLong(idRaw.strip());
                app().products().update(id, name, description, priceCents, stock);
            }
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashProducts", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (Exception e) {
            LOG.error("product form", e);
            req.getSession(true).setAttribute("flashProducts", "error.internal");
            resp.sendRedirect(req.getContextPath() + "/app/products");
        }
    }

    private static String require(String v, String err) {
        if (v == null || v.isBlank()) {
            throw new DomainException(err);
        }
        return v.strip();
    }

    private static int parseInt(String raw, int min, int max, String err) {
        try {
            int v = Integer.parseInt(raw == null ? "" : raw.strip());
            if (v < min || v > max) {
                throw new DomainException(err);
            }
            return v;
        } catch (NumberFormatException e) {
            throw new DomainException(err);
        }
    }
}
