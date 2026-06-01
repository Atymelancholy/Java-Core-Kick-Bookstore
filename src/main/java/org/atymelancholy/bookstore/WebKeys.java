package org.atymelancholy.bookstore;

/**
 * Attribute names in {@link jakarta.servlet.ServletContext} and the HTTP session.
 * <p>Keys are package-prefixed strings to avoid collisions with other apps in the
 * same container.</p>
 */
public final class WebKeys {
    /** Shared Thymeleaf engine for all servlets. */
    public static final String TEMPLATE_ENGINE =
            "org.atymelancholy.bookstore.TEMPLATE_ENGINE";
    /** Application services (DAO, business logic), created at startup. */
    public static final String APP_SERVICES =
            "org.atymelancholy.bookstore.APP_SERVICES";
    /** Current user in session (after sign-in). */
    public static final String CURRENT_USER = "currentUser";
    /** Selected UI locale (EN, RU, …). */
    public static final String LOCALE_SESSION =
            "org.atymelancholy.bookstore.LOCALE";

    private WebKeys() {
    }
}
