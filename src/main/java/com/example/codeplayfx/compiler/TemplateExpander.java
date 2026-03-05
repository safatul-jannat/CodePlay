package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;

/**
 * TemplateExpander - Wraps user code into engine-controlled template.
 * 
 * Responsibilities:
 * - Add package declaration
 * - Add required imports
 * - Ensure class extends UserGame
 * - Keep execute() method hookable
 * 
 * This bridges user script and engine application.
 */
public class TemplateExpander {
    
    private String userClassName;
    private String userCode;
    private String expandedCode;
    
    /**
     * Create template expander
     */
    public TemplateExpander(String userClassName, String userCode) {
        this.userClassName = userClassName;
        this.userCode = userCode;
    }
    
    /**
     * Expand user code into full template
     */
    public String expand() {
        StringBuilder template = new StringBuilder();
        
        // Package declaration
        template.append("package usergames;\n\n");
        
        // Required imports
        template.append("import com.example.codeplayfx.scripting.runtime.UserGame;\n");
        template.append("import com.example.codeplayfx.world.entity.BlockPlayer;\n");
        template.append("import com.example.codeplayfx.world.entity.Enemy;\n");
        template.append("import javafx.scene.paint.Color;\n");
        template.append("import com.example.codeplayfx.world.render.ShapeType;\n");
        template.append("import com.example.codeplayfx.world.entity.Collectible;\n");

        template.append("\n");
        
        // User code (modified to extend UserGame)
        String modifiedUserCode = ensureExtendsUserGame(userCode);
        template.append(modifiedUserCode);
        
        expandedCode = template.toString();
        
        Logger.info("Template expanded for class: " + userClassName);
        Logger.debug("Expanded code:\n" + expandedCode);
        
        return expandedCode;
    }
    
    /**
     * Ensure user class extends UserGame
     */
    private String ensureExtendsUserGame(String code) {
        // Check if already extends UserGame
        if (code.contains("extends UserGame")) {
            return code;
        }
        
        // Add "extends UserGame" to class declaration
        String pattern = "public\\s+class\\s+" + userClassName;
        String replacement = "public class " + userClassName + " extends UserGame";
        
        return code.replaceFirst(pattern, replacement);
    }
    
    /**
     * Get expanded code
     */
    public String getExpandedCode() {
        return expandedCode;
    }
    
    /**
     * Get fully qualified class name
     */
    public String getFullyQualifiedClassName() {
        return "usergames." + userClassName;
    }
}