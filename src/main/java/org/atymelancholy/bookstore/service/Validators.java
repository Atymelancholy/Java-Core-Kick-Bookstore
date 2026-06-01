package org.atymelancholy.bookstore.service;

import java.util.regex.Pattern;

/**
 * Minimal server-side validation (trim via {@link String#strip()}).
 */
public final class Validators {

    /** 3–64 characters: Latin letters, digits, and limited punctuation. */
    private static final Pattern LOGIN =
            Pattern.compile("^[a-zA-Z0-9._-]{3,64}$");
    /** Minimal e-mail format check. */
    private static final Pattern EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /** Minimum allowed password length. */
    private static final int PASSWORD_MIN = 6;
    /** Maximum allowed password length. */
    private static final int PASSWORD_MAX = 128;
    /** Common maximum length for short text fields. */
    private static final int TEXT_MAX_255 = 255;
    /** Maximum product description length. */
    private static final int PRODUCT_DESC_MAX = 2000;
    /** Maximum money amount for validation (in EUR). */
    private static final int MAX_MONEY_EUR = 1_000_000;
    /** Conversion ratio (cents per EUR). */
    private static final double CENTS_IN_EUR = 100.0;

    private Validators() {
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
            throw new AppException("error.validation.login");
        }
        return s;
    }

    /**
     * Validate password length.
     *
     * @param raw raw password characters
     * @return password as string
     */
    public static String password(final char[] raw) {
        if (raw == null
                || raw.length < PASSWORD_MIN
                || raw.length > PASSWORD_MAX) {
            throw new AppException("error.validation.password");
        }
        return new String(raw);
    }

    /**
     * Validate and normalize email.
     *
     * @param raw raw input
     * @return normalized email
     */
    public static String email(final String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.length() > TEXT_MAX_255 || !EMAIL.matcher(s).matches()) {
            throw new AppException("error.validation.email");
        }
        return s;
    }

    /**
     * Validate and normalize display name.
     *
     * @param raw raw input
     * @return normalized display name
     */
    public static String displayName(final String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.isEmpty() || s.length() > TEXT_MAX_255) {
            throw new AppException("error.validation.displayName");
        }
        return s;
    }

    /**
     * Validate and normalize product name.
     *
     * @param raw raw input
     * @return normalized name
     */
    public static String productName(final String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.isEmpty() || s.length() > TEXT_MAX_255) {
            throw new AppException("error.validation.productName");
        }
        return s;
    }

    /**
     * Validate and normalize product description.
     *
     * @param raw raw input
     * @return normalized description
     */
    public static String productDescription(final String raw) {
        String s = raw == null ? "" : raw.strip();
        if (s.length() > PRODUCT_DESC_MAX) {
            throw new AppException("error.validation.productDescription");
        }
        return s;
    }

    /**
     * Parse positive money amount into cents.
     *
     * @param raw raw input (dot or comma decimal separator)
     * @return amount in cents
     */
    public static int positiveMoneyCents(final String raw) {
        try {
            double euros = Double.parseDouble(raw.strip().replace(',', '.'));
            if (euros < 0 || euros > MAX_MONEY_EUR) {
                throw new AppException("error.validation.price");
            }
            int cents = (int) Math.round(euros * CENTS_IN_EUR);
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

    /**
     * Parse non-negative integer with maximum.
     *
     * @param raw raw input
     * @param max maximum allowed value
     * @return parsed integer
     */
    public static int nonNegativeInt(final String raw, final int max) {
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

    /**
     * Parse positive long id.
     *
     * @param raw raw input
     * @return parsed id
     */
    public static long positiveLongId(final String raw) {
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
