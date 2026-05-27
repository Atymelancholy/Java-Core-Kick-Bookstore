package org.atymelancholy.bookstore.web.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atymelancholy.bookstore.service.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Requires an authenticated session for protected {@code /app/*} routes.
 */
public final class AuthFilter extends HttpFilter {

    private static final Logger LOG = LogManager.getLogger(AuthFilter.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String path = req.getServletPath();
        String method = req.getMethod();
        if (isPublic(path, method)) {
            chain.doFilter(req, res);
            return;
        }
        if (AuthService.current(req.getSession(false)).isEmpty()) {
            LOG.info("Blocked unauthenticated {} {}", method, path);
            res.sendRedirect(req.getContextPath() + "/app/login");
            return;
        }
        chain.doFilter(req, res);
    }

    private static boolean isPublic(String path, String method) {
        if (path.equals("/app/login") || path.equals("/app/register")) {
            return true;
        }
        return path.equals("/app/products") && "GET".equalsIgnoreCase(method);
    }
}
