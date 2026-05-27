package org.atymelancholy.bookstore;

/**
 * Servlet context attribute keys shared across the web layer.
 */
public final class WebKeys {
    public static final String TEMPLATE_ENGINE = "org.example.task4.TEMPLATE_ENGINE";
    public static final String APP_SERVICES = "org.example.task4.APP_SERVICES";
    public static final String CURRENT_USER = "currentUser";
    public static final String LOCALE_SESSION = "org.example.task4.LOCALE";

    private WebKeys() {
    }
}
