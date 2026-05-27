package org.atymelancholy.bookstore.service;

/** Domain-level failure mapped to user-visible feedback. */
public final class AppException extends RuntimeException {

    private final String messageKey;

    public AppException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public AppException(String messageKey, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
    }

    public String messageKey() {
        return messageKey;
    }
}
