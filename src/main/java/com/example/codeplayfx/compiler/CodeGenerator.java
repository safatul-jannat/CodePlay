package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;

/**
 * CodeGenerator - Produces final Java source code.
 * 
 * Responsibilities:
 * - Format expanded code
 * - Keep generated code consistent
 * - Enable debugging (can log/save source)
 * 
 * Clean output and maintainability.
 */
public class CodeGenerator {
    
    private String expandedCode;
    private String className;
    
    /**
     * Create code generator
     */
    public CodeGenerator(String expandedCode, String className) {
        this.expandedCode = expandedCode;
        this.className = className;
    }
    
    /**
     * Generate final source code
     */
    public String generate() {
        // For now, just return expanded code
        // Could add formatting, optimization, etc.
        
        Logger.info("Generated source for: " + className);
        
        return expandedCode;
    }
    
    /**
     * Get generated source (for debugging)
     */
    public String getGeneratedSource() {
        return expandedCode;
    }
    
    /**
     * Save generated source to string (for IDE preview/debugging)
     */
    public String toDebugString() {
        StringBuilder debug = new StringBuilder();
        debug.append("=== Generated Source: ").append(className).append(" ===\n");
        debug.append(expandedCode);
        debug.append("\n=== End Generated Source ===\n");
        return debug.toString();
    }
}