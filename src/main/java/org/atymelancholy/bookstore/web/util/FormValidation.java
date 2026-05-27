package org.atymelancholy.bookstore.web.util;

import java.util.regex.Pattern;

import org.atymelancholy.bookstore.service.DomainException;

/**
 * Shared server-side validation (trim via {@link String#strip()}).
 */
public final class FormValidation {

    private static final Pattern LOGIN = Pattern.compile("[a-zA-Z0-9_]{3,64}");

    private FormValidation() {
    }

    public static String login(String raw) {
        String s = raw == null ? "" : raw.strip();
        if (!LOGIN.matcher(s).matches()) {
            throw new DomainException("error.validation.login");
        }
        return s;
    }

    public static String requireLen(String raw, int min, int max, String errKey) {
        String s = raw == null ? "" : raw.strip();
        if (s.length() < min || s.length() > max) {
            throw new DomainException(errKey);
        }
        return s;
    }
}
