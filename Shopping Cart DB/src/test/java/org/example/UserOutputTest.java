package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserOutputTest {

    @Test
    void utf8StdoutIsNonNull() {
        assertNotNull(UserOutput.utf8Stdout());
    }
}
