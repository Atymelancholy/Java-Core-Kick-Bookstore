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
 * Adds structured request logging.
 * <p>Can be extended for correlation ids / timing.</p>
 */
public final class RequestLogFilter extends HttpFilter {

    /** Request logger (debug level). */
    private static final Logger LOG =
            LogManager.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilter(final HttpServletRequest req,
                            final HttpServletResponse res,
                            final FilterChain chain)
            throws IOException, ServletException {
        LOG.debug("{} {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(req, res);
    }
}
