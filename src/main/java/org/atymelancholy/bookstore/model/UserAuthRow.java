package org.atymelancholy.bookstore.model;

/**
 * Row loaded for authentication only; password hash must not leak to the view
 * layer.
 *
 * @param id user id
 * @param login login
 * @param passwordHash password hash
 * @param email email
 * @param displayName display name
 * @param role role name
 */
public record UserAuthRow(long id,
                          String login,
                          String passwordHash,
                          String email,
                          String displayName,
                          String role) {

    /**
     * Convert auth row to a public account object.
     *
     * @return account object without password hash
     */
    public UserAccount toAccount() {
        return new UserAccount(id, login, email, displayName, role);
    }
}
