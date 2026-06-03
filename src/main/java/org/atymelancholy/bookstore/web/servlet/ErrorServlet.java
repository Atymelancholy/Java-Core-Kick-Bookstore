package org.atymelancholy.bookstore.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.web.util.ViewModel;
import org.atymelancholy.bookstore.web.util.Views;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/error")
public final class ErrorServlet extends HttpServlet {

    /** HTTP status when the container did not set one. */
    private static final int DEFAULT_ERROR_STATUS = 500;

    /** Logger. */
    private static final Logger LOG = LogManager.getLogger(ErrorServlet.class);

    @Override
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse resp)
            throws ServletException, IOException {
        Throwable ex = (Throwable) req.getAttribute(
                jakarta.servlet.RequestDispatcher.ERROR_EXCEPTION);
        Integer code = (Integer) req.getAttribute(
                jakarta.servlet.RequestDispatcher.ERROR_STATUS_CODE);
        int sc = code == null ? DEFAULT_ERROR_STATUS : code;
        resp.setStatus(sc);
        if (ex != null) {
            LOG.error("Unhandled error code={}", sc, ex);
        }
        Map<String, Object> m = new HashMap<>();
        ViewModel.putUser(m, req.getSession(false));
        m.put("code", sc);
        Views.render(getServletContext(), req, resp, "error", m);
    }
}
