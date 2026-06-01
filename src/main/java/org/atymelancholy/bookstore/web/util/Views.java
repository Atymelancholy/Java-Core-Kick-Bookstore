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

    /**
     * Render a Thymeleaf view with the given model.
     *
     * @param servletContext servlet context
     * @param request request
     * @param response response
     * @param viewName template name without suffix
     * @param model model map
     * @throws IOException if writing response fails
     */
    public static void render(
            final ServletContext servletContext,
            final HttpServletRequest request,
            final HttpServletResponse response,
            final String viewName,
            final Map<String, Object> model) throws IOException {
        TemplateEngine engine = (TemplateEngine) servletContext.getAttribute(
                WebKeys.TEMPLATE_ENGINE);
        HttpSession session = request.getSession(false);
        Locale locale = session == null
                ? request.getLocale()
                : (Locale) session.getAttribute(WebKeys.LOCALE_SESSION);
        if (locale == null) {
            locale = request.getLocale();
        }
        JakartaServletWebApplication webApp =
                JakartaServletWebApplication.buildApplication(servletContext);
        var exchange = webApp.buildExchange(request, response);
        Map<String, Object> vars = new HashMap<>(model);
        UiMessages messages = new UiMessages(locale);
        vars.put("msg", messages);
        // Name must not start with "btn" — Thymeleaf treats ${btnGoBackLabel} as ${btn.GoBackLabel}.
        vars.put("labelGoBack", messages.t("btnGoBack"));
        // #httpServletRequest is not bound in WebContext; expose path for @{...} links.
        vars.put("currentPath", request.getServletPath());
        ViewModel.putUser(vars, session);
        WebContext ctx = new WebContext(exchange, locale, vars);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        engine.process(viewName, ctx, response.getWriter());
    }
}
