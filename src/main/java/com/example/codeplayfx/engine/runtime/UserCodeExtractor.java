package com.example.codeplayfx.engine.runtime;

import java.util.regex.*;
import java.util.*;

public class UserCodeExtractor {

    public static UserCode extractUserCode(String uiTemplate) {
        UserCode result = new UserCode();

        try {
            // Clean the code
            String cleanCode = removeComments(uiTemplate);

            // Extract class name
            result.className = extractClassName(cleanCode);

            // Extract execute() method
            result.executeBody = extractMethodBody(cleanCode, "execute");

            // Extract helper methods
            result.helperMethods = extractAllMethodsExcept(cleanCode, "execute");

            // Extract fields
            result.fields = extractFields(cleanCode);

            System.out.println("Extracted code parts:");
            System.out.println("  Class: " + result.className);
            System.out.println("  Execute body length: " + result.executeBody.length());
            System.out.println("  Helper methods: " + result.helperMethods.length());
            System.out.println("  Fields: " + result.fields.length());

        } catch (Exception e) {
            System.err.println("Error extracting code: " + e.getMessage());
            // Fallback: use entire code as execute body
            result.executeBody = uiTemplate;
        }

        return result;
    }

    public static class UserCode {
        public String className = "UserGame";
        public String executeBody = "";
        public String helperMethods = "";
        public String fields = "";

        @Override
        public String toString() {
            return String.format("UserCode[class=%s, execute=%d chars, helpers=%d chars, fields=%d chars]",
                    className, executeBody.length(), helperMethods.length(), fields.length());
        }
    }

    // ===== HELPER METHODS =====

    private static String removeComments(String code) {
        // Remove single-line comments
        code = code.replaceAll("//.*", "");
        // Remove multi-line comments
        code = code.replaceAll("/\\*.*?\\*/", "");
        // Remove extra whitespace
        code = code.replaceAll("\\s+", " ");
        return code;
    }

    private static String extractClassName(String code) {
        Pattern pattern = Pattern.compile("(?:public\\s+)?class\\s+(\\w+)");
        Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "UserGame";
    }

    private static String extractMethodBody(String code, String methodName) {
        // Pattern for method like: "public void execute() { ... }"
        String pattern = String.format("(?:public|private|protected)?\\s+(?:\\w+\\s+)?%s\\s*\\([^)]*\\)\\s*\\{",
                Pattern.quote(methodName));

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(code);

        if (m.find()) {
            int start = m.end();
            int braceCount = 1;
            int end = start;

            while (end < code.length() && braceCount > 0) {
                char c = code.charAt(end);
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                end++;
            }

            if (end > start) {
                return code.substring(start, end - 1).trim();
            }
        }

        // If no method found, try to find just the code block
        return extractMainLogic(code);
    }

    private static String extractMainLogic(String code) {
        // Simple extraction: get everything between first and last brace
        int firstBrace = code.indexOf('{');
        int lastBrace = code.lastIndexOf('}');

        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return code.substring(firstBrace + 1, lastBrace).trim();
        }

        return code;
    }

    private static String extractAllMethodsExcept(String code, String excludeMethod) {
        StringBuilder methods = new StringBuilder();

        // Find method signatures
        Pattern methodPattern = Pattern.compile(
                "(?:public|private|protected)?\\s+\\w+\\s+(\\w+)\\s*\\([^)]*\\)\\s*\\{"
        );

        Matcher matcher = methodPattern.matcher(code);

        while (matcher.find()) {
            String methodName = matcher.group(1);

            if (!methodName.equals(excludeMethod)) {
                // Extract this method
                int start = matcher.start();
                int end = findMatchingBrace(code, start);

                if (end > start) {
                    String method = code.substring(start, end).trim();
                    methods.append("\n    ").append(method);
                }
            }
        }

        return methods.toString();
    }

    private static String extractFields(String code) {
        StringBuilder fields = new StringBuilder();

        // Simple pattern for field declarations
        // Matches: "private int score = 0;" or "Player player;"
        Pattern fieldPattern = Pattern.compile(
                "(?:private|public|protected)?\\s+\\w+\\s+\\w+\\s*(?:=\\s*[^;]+)?;"
        );

        Matcher matcher = fieldPattern.matcher(code);
        while (matcher.find()) {
            String field = matcher.group().trim();
            if (!field.contains("(") && !field.contains(")")) { // Not a method
                fields.append("\n    ").append(field);
            }
        }

        return fields.toString();
    }

    private static int findMatchingBrace(String code, int start) {
        int braceCount = 0;
        boolean enteredBrace = false;

        for (int i = start; i < code.length(); i++) {
            char c = code.charAt(i);
            if (c == '{') {
                braceCount++;
                enteredBrace = true;
            } else if (c == '}') {
                braceCount--;
                if (enteredBrace && braceCount == 0) {
                    return i + 1; // Include the closing brace
                }
            }
        }

        return code.length();
    }
}