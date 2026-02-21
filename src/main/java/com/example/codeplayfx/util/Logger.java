package com.example.codeplayfx.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logger for the CodePlay engine.
 * Provides different log levels and formatted output.
 */
public class Logger {

    public enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    private static Level currentLevel = Level.INFO;
    private static final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    /**
     * Set the minimum log level to display
     */
    public static void setLevel(Level level) {
        currentLevel = level;
    }

    /**
     * Log a debug message
     */
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    /**
     * Log an info message
     */
    public static void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log a warning message
     */
    public static void warning(String message) {
        log(Level.WARNING, message);
    }

    /**
     * Log an error message
     */
    public static void error(String message) {
        log(Level.ERROR, message);
    }

    /**
     * Log an error with exception details
     */
    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message + " - " + throwable.getMessage());
        throwable.printStackTrace();
    }

    /**
     * Internal logging method
     */
    private static void log(Level level, String message) {
        if (level.ordinal() >= currentLevel.ordinal()) {
            String timestamp = LocalDateTime.now().format(timeFormatter);
            String levelStr = String.format("%-7s", "[" + level + "]");
            System.out.println(timestamp + " " + levelStr + " " + message);
        }
    }
}

