package org.atymelancholy.bookstore.web.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Ensures UTF-8 request/response encoding (request body + rendered HTML).
 */
public final class EncodingFilter extends HttpFilter {

    @Override
    protected void doFilter(final HttpServletRequest req,
                            final HttpServletResponse res,
                            final FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");
        chain.doFilter(req, res);
    }
}
