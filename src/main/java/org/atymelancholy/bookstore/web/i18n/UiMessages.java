package org.atymelancholy.bookstore.web.i18n;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Thin wrapper for templates ({@code th:text="${msg.t('key')}"}).
 * <p>If there is no bundle for the browser locale (e.g. {@code ru}), falls back to the
 * language-only locale, then English.</p>
 */
public final class UiMessages {

    private static final String BASE = "i18n.messages";

    private final ResourceBundle bundle;

    public UiMessages(Locale locale) {
        this.bundle = resolveBundle(locale);
    }

    private static ResourceBundle resolveBundle(Locale preferred) {
        Locale languageOnly = Locale.forLanguageTag(preferred.getLanguage());
        for (Locale loc : List.of(preferred, languageOnly, Locale.ENGLISH)) {
            try {
                return ResourceBundle.getBundle(BASE, loc);
            } catch (MissingResourceException ignored) {
                // try next
            }
        }
        return ResourceBundle.getBundle(BASE, Locale.ENGLISH);
    }

    public String t(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
}
