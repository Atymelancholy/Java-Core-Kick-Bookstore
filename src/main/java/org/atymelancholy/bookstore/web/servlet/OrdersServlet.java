package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.atymelancholy.bookstore.service.OrderService;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/orders")
public final class OrdersServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = AuthService.current(req.getSession(false)).orElseThrow();
        int page = 0;
        try {
            String p = req.getParameter("page");
            if (p != null && !p.isBlank()) {
                page = Math.max(0, Integer.parseInt(p.strip()));
            }
        } catch (NumberFormatException e) {
            page = 0;
        }
        long total = app().orders().orderCount(user.id());
        int pages = (int) Math.ceil(total / (double) OrderService.PAGE_SIZE);
        Map<String, Object> m = new HashMap<>();
        m.put("user", user);
        m.put("orders", app().orders().listForUser(user.id(), page));
        m.put("page", page);
        m.put("totalPages", Math.max(1, pages));
        m.put("flash", LoginServlet.popFlash(req, "flashOrders"));
        Views.render(getServletContext(), req, resp, "orders", m);
    }
}
