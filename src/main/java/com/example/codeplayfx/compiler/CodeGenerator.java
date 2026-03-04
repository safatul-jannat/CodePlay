package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;

public class CodeGenerator {

    private String expandedCode;
    private String className;

    // just setting up the stuff we need
    public CodeGenerator(String expandedCode, String className) {
        this.expandedCode = expandedCode;
        this.className = className;
    }

    // this makes the final code
    public String generate() {
        // for now just gives back whatever we got
        // later could add formatting stuff

        Logger.info("Generated source for: " + className);

        return expandedCode;
    }

    // if someone wants to see the code
    public String getGeneratedSource() {
        return expandedCode;
    }

    // makes it look pretty for debugging
    public String toDebugString() {
        StringBuilder debug = new StringBuilder();
        debug.append("=== Generated Source: ").append(className).append(" ===\n");
        debug.append(expandedCode);
        debug.append("\n=== End Generated Source ===\n");
        return debug.toString();
    }
}