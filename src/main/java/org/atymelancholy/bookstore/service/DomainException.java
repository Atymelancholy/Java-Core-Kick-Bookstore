package org.atymelancholy.bookstore.service;

/**
 * Recoverable business rule violation (mapped to user-visible message key).
 */
public final class DomainException extends RuntimeException {

    private final String messageKey;

    public DomainException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String messageKey() {
        return messageKey;
    }
}
