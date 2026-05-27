package org.atymelancholy.bookstore.web;

import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.OrderService;
import org.atymelancholy.bookstore.service.ProductService;
import org.atymelancholy.bookstore.service.ProfileService;

/**
 * Application services exposed to the web layer (created once at startup).
 */
public record AppServices(
        AuthService auth,
        ProductService products,
        OrderService orders,
        ProfileService profiles) {
}
