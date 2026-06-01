package org.atymelancholy.bookstore.service;

/**
 * Recoverable business rule violation (mapped to user-visible message key).
 */
public final class DomainException extends RuntimeException {

    /** I18n message key. */
    private final String messageKey;

    /**
     * Creates an exception with message key.
     *
     * @param key i18n message key
     */
    public DomainException(final String key) {
        super(key);
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
