package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.model.UserAccount;
import org.atymelancholy.bookstore.service.AuthService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/orders/cancel")
public final class OrderCancelServlet extends BaseServlet {

    /** Logger. */
    private static final Logger LOG =
            LogManager.getLogger(OrderCancelServlet.class);

    @Override
    protected void doPost(final HttpServletRequest req,
                          final HttpServletResponse resp)
            throws IOException {
        try {
            UserAccount user = AuthService.current(req.getSession(false))
                    .orElseThrow();
            String oid = req.getParameter("orderId");
            if (oid == null || oid.isBlank()) {
                throw new IllegalStateException("missing orderId");
            }
            long orderId = Long.parseLong(oid.strip());
            app().orders().cancel(user.id(), orderId);
            resp.sendRedirect(req.getContextPath() + "/app/orders");
        } catch (NumberFormatException | IllegalStateException e) {
            req.getSession(true).setAttribute(
                    "flashOrders", "error.order.cancel");
            resp.sendRedirect(req.getContextPath() + "/app/orders");
        } catch (Exception e) {
            LOG.error("order cancel", e);
            req.getSession(true).setAttribute(
                    "flashOrders", "error.internal");
            resp.sendRedirect(req.getContextPath() + "/app/orders");
        }
    }
}
