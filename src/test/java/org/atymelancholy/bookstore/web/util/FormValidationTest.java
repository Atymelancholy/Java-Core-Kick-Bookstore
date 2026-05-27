package org.atymelancholy.bookstore.web.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.atymelancholy.bookstore.service.DomainException;
import org.junit.jupiter.api.Test;

class FormValidationTest {

    @Test
    void loginAcceptsValid() {
        assertEquals("ab_c", FormValidation.login("  ab_c  "));
    }

    @Test
    void loginRejectsInvalid() {
        assertThrows(DomainException.class, () -> FormValidation.login("ab"));
    }
}
