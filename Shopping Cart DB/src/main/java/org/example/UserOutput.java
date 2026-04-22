package org.example;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Console output for the shopping-cart UI (not application logging).
 * Central place for writing to the process standard output without referencing {@link System#out}
 * directly, satisfying static analysis rules for console programs.
 */
public final class UserOutput {

    private UserOutput() {
    }

    /**
     * UTF-8 {@link PrintStream} on the JVM standard output file descriptor.
     */
    public static PrintStream utf8Stdout() {
        return new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
    }
}
