package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UserCodeParser - Parses and validates user source code.
 * 
 * Responsibilities:
 * - Verify exactly one public class
 * - Verify presence of execute() method
 * - Extract user class name
 * 
 * This enforces the single-class, single-hook rule.
 */
public class UserCodeParser {
    
    private String sourceCode;
    private String className;
    private boolean hasExecuteMethod;
    
    /**
     * Parse user source code
     */
    public UserCodeParser(String sourceCode) {
        this.sourceCode = sourceCode;
        parse();
    }
    
    /**
     * Parse the source code
     */
    private void parse() {
        // Extract class name
        className = extractClassName();
        
        // Check for execute() method
        hasExecuteMethod = checkExecuteMethod();
    }
    
    /**
     * Extract the public class name
     */
    private String extractClassName() {
        // Pattern: public class ClassName
        Pattern pattern = Pattern.compile("public\\s+class\\s+([A-Za-z_][A-Za-z0-9_]*)");
        Matcher matcher = pattern.matcher(sourceCode);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        throw new CompilationException("No public class found in user code");
    }
    
    /**
     * Check if execute() method exists
     */
    private boolean checkExecuteMethod() {
        // Pattern: public void execute() or void execute()
        Pattern pattern = Pattern.compile("(public\\s+)?void\\s+execute\\s*\\(\\s*\\)");
        Matcher matcher = pattern.matcher(sourceCode);
        
        return matcher.find();
    }
    
    /**
     * Validate user code structure
     */
    public void validate() {
        // Check class name
        if (className == null || className.isEmpty()) {
            throw new CompilationException("User code must contain exactly one public class");
        }
        
        // Check execute method
        if (!hasExecuteMethod) {
            throw new CompilationException("User class must implement execute() method");
        }
        
        // Check for multiple public classes
        Pattern pattern = Pattern.compile("public\\s+class\\s+");
        Matcher matcher = pattern.matcher(sourceCode);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        
        if (count > 1) {
            throw new CompilationException("User code must contain exactly one public class (found " + count + ")");
        }
        
        Logger.info("User code validated: class=" + className + ", hasExecute=" + hasExecuteMethod);
    }
    
    /**
     * Get extracted class name
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Get source code
     */
    public String getSourceCode() {
        return sourceCode;
    }
    
    /**
     * Check if execute method exists
     */
    public boolean hasExecuteMethod() {
        return hasExecuteMethod;
    }
}