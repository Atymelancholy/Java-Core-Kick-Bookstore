package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/error")
public final class ErrorServlet extends HttpServlet {

    private static final Logger LOG = LogManager.getLogger(ErrorServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Throwable ex = (Throwable) req.getAttribute(jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION);
        Integer code = (Integer) req.getAttribute(jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE);
        int sc = code == null ? 500 : code;
        resp.setStatus(sc);
        if (ex != null) {
            LOG.error("Unhandled error code={}", sc, ex);
        }
        Map<String, Object> m = new HashMap<>();
        m.put("user", AuthService.current(req.getSession(false)).orElse(null));
        m.put("code", sc);
        Views.render(getServletContext(), req, resp, "error", m);
    }
}
