package org.atymelancholy.bookstore.web.filter;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Adds structured request logging (can be extended for correlation ids / timing).
 */
public final class RequestLogFilter extends HttpFilter {

    private static final Logger LOG = LogManager.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        LOG.debug("{} {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(req, res);
    }
}
