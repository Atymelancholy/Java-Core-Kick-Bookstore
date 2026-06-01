package org.atymelancholy.bookstore.model;

/**
 * User role names stored in the database.
 */
public final class UserRoles {

    /** Administrator: manage catalog. */
    public static final String ADMIN = "ADMIN";
    /** Regular customer: place orders only. */
    public static final String USER = "USER";

    private UserRoles() {
    }

    /**
     * @param role role string from DB
     * @return true if admin
     */
    public static boolean isAdmin(final String role) {
        return ADMIN.equalsIgnoreCase(role);
    }
}
