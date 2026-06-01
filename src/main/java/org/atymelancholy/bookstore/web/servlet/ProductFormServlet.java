package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.model.Product;
import org.atymelancholy.bookstore.service.DomainException;
import org.atymelancholy.bookstore.web.util.Authz;
import org.atymelancholy.bookstore.web.util.ViewModel;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/products/form")
public final class ProductFormServlet extends BaseServlet {

    /** Upper bound for price in cents. */
    private static final int MAX_PRICE_CENTS = 10_000_000;
    /** Upper bound for stock quantity. */
    private static final int MAX_STOCK = 1_000_000;

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(ProductFormServlet.class);

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Authz.requireAdmin(req.getSession(false));
            Map<String, Object> m = new HashMap<>();
            ViewModel.putUser(m, req.getSession(false));
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
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashProducts", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/products");
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse resp)
            throws IOException {
        try {
            Authz.requireAdmin(req.getSession(false));
            String idRaw = req.getParameter("id");
            String name = require(
                    req.getParameter("name"), "error.validation.name");
            String rawDesc = req.getParameter("description");
            String description = rawDesc == null ? "" : rawDesc.strip();
            int priceCents = parsePriceByn(req.getParameter("priceByn"));
            int stock = parseInt(
                    req.getParameter("stock"),
                    0,
                    MAX_STOCK,
                    "error.validation.stock");
            if (idRaw == null || idRaw.isBlank()) {
                app().products().create(
                        name, description, priceCents, stock);
            } else {
                long id = Long.parseLong(idRaw.strip());
                app().products().update(
                        id, name, description, priceCents, stock);
            }
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashProducts", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/products");
            return;
        } catch (Exception e) {
            LOG.error("product form", e);
            req.getSession(true).setAttribute(
                    "flashProducts", "error.internal");
            resp.sendRedirect(req.getContextPath() + "/app/products");
        }
    }

    private static String require(final String v, final String err) {
        if (v == null || v.isBlank()) {
            throw new DomainException(err);
        }
        return v.strip();
    }

    private static int parsePriceByn(final String raw) {
        try {
            String s = raw == null ? "" : raw.strip().replace(',', '.');
            double byn = Double.parseDouble(s);
            if (byn < 0.01 || byn > MAX_PRICE_CENTS / 100.0) {
                throw new DomainException("error.validation.price");
            }
            long cents = Math.round(byn * 100);
            if (cents < 1 || cents > MAX_PRICE_CENTS) {
                throw new DomainException("error.validation.price");
            }
            return (int) cents;
        } catch (NumberFormatException e) {
            throw new DomainException("error.validation.price");
        }
    }

    private static int parseInt(final String raw,
                                final int min,
                                final int max,
                                final String err) {
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
