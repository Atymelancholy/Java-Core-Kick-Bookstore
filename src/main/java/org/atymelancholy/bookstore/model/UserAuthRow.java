package org.atymelancholy.bookstore.model;

/**
 * Row loaded for authentication only; password hash must not leak to the view layer.
 */
public record UserAuthRow(long id, String login, String passwordHash, String email, String displayName) {

    public UserAccount toAccount() {
        return new UserAccount(id, login, email, displayName);
    }
}
