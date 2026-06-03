package org.atymelancholy.bookstore.web.listener;

import org.atymelancholy.bookstore.WebKeys;
import org.atymelancholy.bookstore.config.DataSources;
import org.atymelancholy.bookstore.config.SqlScripts;
import org.atymelancholy.bookstore.dao.DaoFactory;
import org.atymelancholy.bookstore.model.UserRoles;
import org.atymelancholy.bookstore.service.AuthService;
import org.atymelancholy.bookstore.service.OrderService;
import org.atymelancholy.bookstore.service.ProductService;
import org.atymelancholy.bookstore.service.ProfileService;
import org.atymelancholy.bookstore.util.BcryptHasher;
import org.atymelancholy.bookstore.web.AppServices;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Application bootstrap on container startup: DB pool, schema, services,
 * Thymeleaf.
 */
public final class AppContextListener implements ServletContextListener {

    /** BCrypt cost factor for demo app. */
    private static final int BCRYPT_COST = 10;

    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        var ds = DataSources.INSTANCE.get();
        // Tables are created idempotently (IF NOT EXISTS)
        SqlScripts.runClasspath(ds, "db/schema.sql");
        var dao = new DaoFactory(ds);
        var hasher = new BcryptHasher(BCRYPT_COST);
        seedAdminIfMissing(dao, hasher);
        var services = new AppServices(
                new AuthService(dao, hasher),
                new ProductService(dao),
                new OrderService(dao),
                new ProfileService(dao));
        sce.getServletContext().setAttribute(WebKeys.APP_SERVICES, services);

        JakartaServletWebApplication jakartaApp =
                JakartaServletWebApplication.buildApplication(
                        sce.getServletContext());
        WebApplicationTemplateResolver resolver =
                new WebApplicationTemplateResolver(jakartaApp);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        sce.getServletContext().setAttribute(WebKeys.TEMPLATE_ENGINE, engine);
    }

    private static void seedAdminIfMissing(final DaoFactory dao,
                                           final BcryptHasher hasher) {
        if (dao.users().findByLogin("admin").isEmpty()) {
            dao.users().insert(
                    "admin",
                    hasher.hash("admin"),
                    "admin@bookstore.local",
                    "Administrator",
                    UserRoles.ADMIN);
            return;
        }
        dao.users().updateRoleByLogin("admin", UserRoles.ADMIN);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        ((HikariDataSource) DataSources.INSTANCE.get()).close();
    }
}
