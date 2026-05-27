package org.atymelancholy.bookstore.service;

import java.util.regex.Pattern;

/**
 * Minimal server-side validation (trim via {@link String#strip()}).
 */
public final class Validators {

    private static final Pattern LOGIN = Pattern.compile("^[a-zA-Z0-9._-]{3,64}$");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validators() {
    }

    public static String login(String raw) {
        String s = raw == null ? "" : raw.strip();
        if (!LOGIN.matcher(s).matches()) {
            throw new AppException("error.validation.login");
        }
        return s;
    }

    public static String password(char[] raw) {
        if (raw == null || raw.length < 6 || raw.length > 128) {
            throw new AppException("error.validation.password");
        }
        return new String(raw);
    }

    public static String email(String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.length() > 255 || !EMAIL.matcher(s).matches()) {
            throw new AppException("error.validation.email");
        }
        return s;
    }

    public static String displayName(String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.isEmpty() || s.length() > 255) {
            throw new AppException("error.validation.displayName");
        }
        return s;
    }

    public static String productName(String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.isEmpty() || s.length() > 255) {
            throw new AppException("error.validation.productName");
        }
        return s;
    }

    public static String productDescription(String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.length() > 2000) {
            throw new AppException("error.validation.productDescription");
        }
        return s;
    }

    public static int positiveMoneyCents(String raw) {
        try {
            double euros = Double.parseDouble(raw.strip().replace(',', '.'));
            if (euros < 0 || euros > 1_000_000) {
                throw new AppException("error.validation.price");
            }
            int cents = (int) Math.round(euros * 100.0);
            if (cents < 0) {
                throw new AppException("error.validation.price");
            }
            return cents;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("error.validation.price", e);
        }
    }

    public static int nonNegativeInt(String raw, int max) {
        try {
            int v = Integer.parseInt(raw.strip());
            if (v < 0 || v > max) {
                throw new AppException("error.validation.quantity");
            }
            return v;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("error.validation.quantity", e);
        }
    }

    public static long positiveLongId(String raw) {
        try {
            long v = Long.parseLong(raw.strip());
            if (v <= 0) {
                throw new AppException("error.validation.id");
            }
            return v;
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException("error.validation.id", e);
        }
    }
}
