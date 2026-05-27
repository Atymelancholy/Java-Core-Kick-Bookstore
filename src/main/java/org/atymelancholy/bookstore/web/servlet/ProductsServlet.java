package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/products")
public final class ProductsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> m = new HashMap<>();
        m.put("user", AuthService.current(req.getSession(false)).orElse(null));
        m.put("products", app().products().listAll());
        m.put("flash", LoginServlet.popFlash(req, "flashProducts"));
        Views.render(getServletContext(), req, resp, "products", m);
    }
}
