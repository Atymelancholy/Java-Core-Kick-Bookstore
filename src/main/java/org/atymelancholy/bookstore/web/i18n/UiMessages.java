package org.atymelancholy.bookstore.web.i18n;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Minimal {@link ResourceBundle} wrapper for templates.
 */
public final class UiMessages {

    /** Resource bundle with UI translations. */
    private final ResourceBundle bundle;
    /** Locale used for number formatting. */
    private final Locale locale;

    /**
     * Creates message resolver for a given locale.
     *
     * @param locale locale to use
     */
    public UiMessages(final Locale locale) {
        Objects.requireNonNull(locale, "locale");
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("i18n/messages", locale);
    }

    /**
     * Translates a key. If missing, returns the key itself.
     *
     * @param key translation key
     * @return translated value or the key if missing
     */
    public String t(final String key) {
        if (key == null || key.isBlank()) {
            return "";
        }
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Formats a price stored as minor units (kopecks) as Belarusian rubles.
     *
     * @param priceCents amount in minor units
     * @return formatted value with {@code Br} suffix
     */
    public String priceByn(final int priceCents) {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(priceCents / 100.0) + "\u00a0Br";
    }

    /**
     * Localized label for an order status code.
     *
     * @param statusCode value from DB (for example {@code PLACED})
     * @return translated status or the code if unknown
     */
    public String orderStatus(final String statusCode) {
        if (statusCode == null || statusCode.isBlank()) {
            return "";
        }
        String key = "order.status." + statusCode.strip();
        String translated = t(key);
        return translated.equals(key) ? statusCode : translated;
    }
}
