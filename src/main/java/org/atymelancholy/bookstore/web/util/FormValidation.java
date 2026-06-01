package org.atymelancholy.bookstore.web.util;

import java.util.regex.Pattern;

import org.atymelancholy.bookstore.service.DomainException;

/**
 * Shared server-side validation (trim via {@link String#strip()}).
 */
public final class FormValidation {

    /** Minimum email length. */
    public static final int EMAIL_MIN_LEN = 3;
    /** Maximum length for email and display name. */
    public static final int TEXT_MAX_LEN = 255;
    /** Minimum password length. */
    public static final int PASSWORD_MIN_LEN = 6;
    /** Maximum password length. */
    public static final int PASSWORD_MAX_LEN = 128;

    /**
     * Login pattern: 3-64 chars (latin letters, digits, underscore).
     */
    private static final Pattern LOGIN = Pattern.compile("[a-zA-Z0-9_]{3,64}");

    private FormValidation() {
    }

    /**
     * Validate and normalize login.
     *
     * @param raw raw input
     * @return normalized login
     */
    public static String login(final String raw) {
        String s = raw == null ? "" : raw.strip();
        if (!LOGIN.matcher(s).matches()) {
            throw new DomainException("error.validation.login");
        }
        return s;
    }

    /**
     * Require that a string length is within bounds.
     *
     * @param raw raw input
     * @param min min length
     * @param max max length
     * @param errKey message key
     * @return normalized value
     */
    public static String requireLen(final String raw,
                                    final int min,
                                    final int max,
                                    final String errKey) {
        String s = raw == null ? "" : raw.strip();
        if (s.length() < min || s.length() > max) {
            throw new DomainException(errKey);
        }
        return s;
    }
}
