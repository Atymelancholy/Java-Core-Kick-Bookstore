package org.atymelancholy.bookstore.web.servlet;

import org.atymelancholy.bookstore.WebKeys;
import org.atymelancholy.bookstore.web.AppServices;

import jakarta.servlet.http.HttpServlet;

abstract class BaseServlet extends HttpServlet {

    protected final AppServices app() {
        return (AppServices) getServletContext().getAttribute(WebKeys.APP_SERVICES);
    }
}
