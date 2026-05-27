package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.atymelancholy.bookstore.WebKeys;
import org.atymelancholy.bookstore.web.AppServices;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JSON-каталог книжного магазина: {@code GET /api/v1/books} и {@code GET /api/v1/products} (одинаковый ответ).
 */
@WebServlet(urlPatterns = {"/api/v1/products", "/api/v1/books"})
public final class ProductsRestServlet extends HttpServlet {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var app = (AppServices) getServletContext().getAttribute(WebKeys.APP_SERVICES);
        var rows = app.products().listAll().stream()
                .map(p -> new BookJson(p.id(), p.name(), p.description(), p.priceCents(), p.stock()))
                .collect(Collectors.toList());
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        JSON.writeValue(resp.getWriter(), rows);
    }

    private record BookJson(long id, String title, String description, int priceCents, int stock) {
    }
}
