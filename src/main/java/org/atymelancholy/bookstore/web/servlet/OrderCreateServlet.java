package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.model.OrderLine;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.DomainException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/orders/create")
public final class OrderCreateServlet extends BaseServlet {

    private static final Logger LOG = LogManager.getLogger(OrderCreateServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserAccount user = AuthService.current(req.getSession(false)).orElseThrow();
            String pid = req.getParameter("productId");
            if (pid == null || pid.isBlank()) {
                throw new DomainException("error.validation.product");
            }
            long productId = Long.parseLong(pid.strip());
            int qty = parseQty(req.getParameter("quantity"));
            app().orders().placeOrder(user.id(), List.of(new OrderLine(productId, qty)));
            resp.sendRedirect(req.getContextPath() + "/app/orders");
        } catch (DomainException e) {
            req.getSession(true).setAttribute("flashProducts", e.messageKey());
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (NumberFormatException e) {
            req.getSession(true).setAttribute("flashProducts", "error.validation.product");
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (IllegalStateException e) {
            req.getSession(true).setAttribute("flashProducts", "error.order.stock");
            resp.sendRedirect(req.getContextPath() + "/app/products");
        } catch (Exception e) {
            LOG.error("order create", e);
            req.getSession(true).setAttribute("flashProducts", "error.internal");
            resp.sendRedirect(req.getContextPath() + "/app/products");
        }
    }

    private static int parseQty(String raw) {
        int q = Integer.parseInt(raw == null ? "0" : raw.strip());
        if (q < 1 || q > 999) {
            throw new DomainException("error.validation.qty");
        }
        return q;
    }
}
