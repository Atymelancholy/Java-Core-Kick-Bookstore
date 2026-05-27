package org.atymelancholy.bookstore.model;

/** Public user profile fields (no credentials). */
public record UserAccount(long id, String login, String email, String displayName) {
}
