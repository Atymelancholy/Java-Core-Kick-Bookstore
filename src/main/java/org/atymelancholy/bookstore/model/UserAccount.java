package org.atymelancholy.bookstore.model;

/**
 * Public user profile fields (no credentials).
 *
 * @param id user id
 * @param login login
 * @param email email
 * @param displayName display name
 * @param role role name ({@link UserRoles#ADMIN} or {@link UserRoles#USER})
 */
public record UserAccount(long id,
                          String login,
                          String email,
                          String displayName,
                          String role) {

    /**
     * @return true if this user is an administrator
     */
    public boolean isAdmin() {
        return UserRoles.isAdmin(role);
    }
}
