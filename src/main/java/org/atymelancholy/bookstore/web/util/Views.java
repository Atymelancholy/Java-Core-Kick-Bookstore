package org.atymelancholy.bookstore.web.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.atymelancholy.bookstore.web.i18n.UiMessages;
import org.atymelancholy.bookstore.WebKeys;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Renders Thymeleaf HTML views with UTF-8 and escaped text by default.
 */
public final class Views {

    private Views() {
    }

    public static void render(
            ServletContext servletContext,
            HttpServletRequest request,
            HttpServletResponse response,
            String viewName,
            Map<String, Object> model) throws IOException {
        TemplateEngine engine = (TemplateEngine) servletContext.getAttribute(WebKeys.TEMPLATE_ENGINE);
        HttpSession session = request.getSession(false);
        Locale locale = session == null ? request.getLocale() : (Locale) session.getAttribute(WebKeys.LOCALE_SESSION);
        if (locale == null) {
            locale = request.getLocale();
        }
        JakartaServletWebApplication webApp = JakartaServletWebApplication.buildApplication(servletContext);
        var exchange = webApp.buildExchange(request, response);
        Map<String, Object> vars = new HashMap<>(model);
        vars.put("msg", new UiMessages(locale));
        WebContext ctx = new WebContext(exchange, locale, vars);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        engine.process(viewName, ctx, response.getWriter());
    }
}
