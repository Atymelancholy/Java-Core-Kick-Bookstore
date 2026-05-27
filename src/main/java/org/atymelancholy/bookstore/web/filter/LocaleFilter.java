package org.atymelancholy.bookstore.web.filter;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.atymelancholy.bookstore.WebKeys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Picks UI language from {@code ?lang=} (supported: en, de, be, ru) and stores it in the session.
 */
public final class LocaleFilter extends HttpFilter {

    private static final List<Locale> SUPPORTED = List.of(
            Locale.ENGLISH, Locale.GERMAN, Locale.of("be"), Locale.forLanguageTag("ru"));

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String lang = req.getParameter("lang");
        if (lang != null && !lang.isBlank()) {
            Locale chosen = Locale.forLanguageTag(lang.strip());
            if (SUPPORTED.stream().anyMatch(l -> l.getLanguage().equalsIgnoreCase(chosen.getLanguage()))) {
                HttpSession s = req.getSession(true);
                s.setAttribute(WebKeys.LOCALE_SESSION, chosen);
            }
        }
        chain.doFilter(req, res);
    }
}
