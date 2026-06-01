package org.atymelancholy.bookstore.service;

/**
 * Domain-level failure mapped to user-visible feedback.
 */
public final class AppException extends RuntimeException {

    /** I18n message key. */
    private final String messageKey;

    /**
     * Creates an exception with a message key.
     *
     * @param key i18n message key
     */
    public AppException(final String key) {
        super(key);
        this.messageKey = key;
    }

    /**
     * Creates an exception with a message key and cause.
     *
     * @param key i18n message key
     * @param cause cause
     */
    public AppException(final String key, final Throwable cause) {
        super(key, cause);
        this.messageKey = key;
    }

    /**
     * Returns i18n message key.
     *
     * @return message key
     */
    public String messageKey() {
        return messageKey;
    }
}
