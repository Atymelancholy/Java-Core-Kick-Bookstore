package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.atymelancholy.bookstore.service.ProductService;
import org.atymelancholy.bookstore.web.util.ViewModel;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/products")
public final class ProductsServlet extends BaseServlet {

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {
        int page = parsePage(req.getParameter("page"));
        long total = app().products().productCount();
        int pages = (int) Math.ceil(total / (double) ProductService.PAGE_SIZE);

        Map<String, Object> m = new HashMap<>();
        ViewModel.putUser(m, req.getSession(false));
        m.put("products", app().products().listPage(page));
        m.put("page", page);
        m.put("totalPages", Math.max(1, pages));
        m.put("flash", LoginServlet.popFlash(req, "flashProducts"));
        Views.render(getServletContext(), req, resp, "products", m);
    }

    private static int parsePage(final String raw) {
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return Math.max(0, Integer.parseInt(raw.strip()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
