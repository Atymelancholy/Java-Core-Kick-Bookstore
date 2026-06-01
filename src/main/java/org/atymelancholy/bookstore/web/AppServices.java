package org.atymelancholy.bookstore.web;

import java.util.Objects;

import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.OrderService;
import org.atymelancholy.bookstore.service.ProductService;
import org.atymelancholy.bookstore.service.ProfileService;

/**
 * Single access point to the service layer from the web tier
 * (servlets, filters, listeners).
 * <p>Created in {@code AppContextListener} and stored in
 * {@code ServletContext}.</p>
 */
public final class AppServices {

    /** Authentication and current-user service. */
    private final AuthService auth;
    /** Catalog (CRUD) service. */
    private final ProductService products;
    /** Order service. */
    private final OrderService orders;
    /** Profile service. */
    private final ProfileService profiles;

    /**
     * Creates the application service bundle.
     *
     * @param authService authentication service
     * @param productService catalog service
     * @param orderService order service
     * @param profileService profile service
     */
    public AppServices(final AuthService authService,
                       final ProductService productService,
                       final OrderService orderService,
                       final ProfileService profileService) {
        this.auth = Objects.requireNonNull(authService, "auth");
        this.products = Objects.requireNonNull(productService, "products");
        this.orders = Objects.requireNonNull(orderService, "orders");
        this.profiles = Objects.requireNonNull(profileService, "profiles");
    }

    /**
     * Authentication service.
     *
     * @return {@link AuthService}
     */
    public AuthService auth() {
        return auth;
    }

    /**
     * Catalog service.
     *
     * @return {@link ProductService}
     */
    public ProductService products() {
        return products;
    }

    /**
     * Order service.
     *
     * @return {@link OrderService}
     */
    public OrderService orders() {
        return orders;
    }

    /**
     * Profile service.
     *
     * @return {@link ProfileService}
     */
    public ProfileService profiles() {
        return profiles;
    }
}
