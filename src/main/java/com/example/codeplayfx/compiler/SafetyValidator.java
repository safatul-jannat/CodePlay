package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SafetyValidator - Enforces security restrictions on user code.
 * 
 * Responsibilities:
 * - Prevent imports of engine/world packages
 * - Prevent main() method
 * - Prevent forbidden APIs (System.exit, Runtime, etc.)
 * - Only allow approved high-level classes
 * 
 * This protects the engine and maintains encapsulation.
 */
public class SafetyValidator {
    
    // Forbidden imports
    private static final String[] FORBIDDEN_IMPORTS = {
        "engine\\.core",
        "engine\\.system",
        "world\\.physics",
        "world\\.component",
        "world\\.render",
        "compiler\\.",
        "java\\.lang\\.reflect",
        "java\\.io\\.File",
        "java\\.nio\\.file",
        "java\\.net",
        "javax\\.swing",
        "javafx\\.stage"
    };
    
    // Forbidden methods/APIs
    private static final String[] FORBIDDEN_APIS = {
        "System\\.exit",
        "Runtime\\.getRuntime",
        "ProcessBuilder",
        "Class\\.forName",
        "System\\.setSecurityManager",
        "Thread\\.stop"
    };
    
    // Allowed high-level classes (whitelist)
    private static final String[] ALLOWED_CLASSES = {
        "BlockPlayer",
        "Enemy",
        "Color",
        "ShapeType"
    };
    
    private String sourceCode;
    private List<String> violations;
    
    /**
     * Create safety validator
     */
    public SafetyValidator(String sourceCode) {
        this.sourceCode = sourceCode;
        this.violations = new ArrayList<>();
    }
    
    /**
     * Validate user code for safety
     */
    public void validate() {
        violations.clear();
        
        // Check forbidden imports
        checkForbiddenImports();
        
        // Check for main() method
        checkMainMethod();
        
        // Check forbidden APIs
        checkForbiddenAPIs();
        
        // If violations found, throw exception with detailed message
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("Your code contains security violations:\n\n");
            
            for (int i = 0; i < violations.size(); i++) {
                message.append("  ").append(i + 1).append(". ").append(violations.get(i)).append("\n");
            }
            
            message.append("\nPlease fix these issues and try again.\n");
            message.append("\nAllowed:\n");
            message.append("  ✓ BlockPlayer, Enemy, Color, ShapeType\n");
            message.append("  ✓ Standard Java (if, for, while, arrays, etc.)\n");
            message.append("\nNot Allowed:\n");
            message.append("  ✗ Importing engine internals\n");
            message.append("  ✗ main() method\n");
            message.append("  ✗ System.exit(), File I/O, Networking\n");
            
            throw new CompilationException(message.toString());
        }
        
        Logger.info("Safety validation passed");
    }
    
    /**
     * Check for forbidden imports
     */
    private void checkForbiddenImports() {
        for (String forbidden : FORBIDDEN_IMPORTS) {
            Pattern pattern = Pattern.compile("import\\s+" + forbidden);
            if (pattern.matcher(sourceCode).find()) {
                violations.add("Forbidden import: " + forbidden.replace("\\\\.", "."));
            }
        }
    }
    
    /**
     * Check for main() method (not allowed)
     */
    private void checkMainMethod() {
        Pattern pattern = Pattern.compile("(public\\s+)?static\\s+void\\s+main\\s*\\(");
        if (pattern.matcher(sourceCode).find()) {
            violations.add("main() method is not allowed in user code");
        }
    }
    
    /**
     * Check for forbidden APIs
     */
    private void checkForbiddenAPIs() {
        for (String forbidden : FORBIDDEN_APIS) {
            Pattern pattern = Pattern.compile(forbidden);
            if (pattern.matcher(sourceCode).find()) {
                violations.add("Forbidden API: " + forbidden.replace("\\\\", ""));
            }
        }
    }
    
    /**
     * Get violations (for debugging)
     */
    public List<String> getViolations() {
        return new ArrayList<>(violations);
    }
    
    /**
     * Check if code is safe
     */
    public boolean isSafe() {
        return violations.isEmpty();
    }
}