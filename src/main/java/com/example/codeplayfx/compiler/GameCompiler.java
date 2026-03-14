package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.scripting.runtime.UserGame;

/**
 * GameCompiler - Orchestrates the entire compilation pipeline.
 * 
 * Pipeline:
 * 1. UserCodeParser    - Extract class name, verify structure
 * 2. SafetyValidator   - Enforce security restrictions
 * 3. TemplateExpander  - Wrap in engine template
 * 4. CodeGenerator     - Produce final source
 * 5. InMemoryCompiler  - Compile and load
 * 
 * Single entry point for compiling user games.
 */
public class GameCompiler {
    
    /**
     * Compile user source code into executable UserGame instance
     * 
     * @param userSourceCode raw user code (just their class)
     * @return compiled and loaded UserGame instance
     * @throws CompilationException if compilation fails
     */
    public static UserGame compile(String userSourceCode) {
        Logger.info("=== Starting game compilation ===");
        
        try {
            // Step 1: Parse user code
            Logger.info("Step 1: Parsing user code...");
            UserCodeParser parser = new UserCodeParser(userSourceCode);
            parser.validate();
            String className = parser.getClassName();
            Logger.info("  Found class: " + className);
            
            // Step 2: Validate safety
            Logger.info("Step 2: Validating safety...");
            SafetyValidator validator = new SafetyValidator(userSourceCode);
            validator.validate();
            Logger.info("  Safety check passed");
            
            // Step 3: Expand template
            Logger.info("Step 3: Expanding template...");
            TemplateExpander expander = new TemplateExpander(className, userSourceCode);
            String expandedCode = expander.expand();
            String fullyQualifiedName = expander.getFullyQualifiedClassName();
            Logger.info("  Template expanded: " + fullyQualifiedName);
            
            // Step 4: Generate final code
            Logger.info("Step 4: Generating final code...");
            CodeGenerator generator = new CodeGenerator(expandedCode, className);
            String finalSource = generator.generate();
            Logger.info("  Code generated (" + finalSource.length() + " chars)");
            
            // Step 5: Compile in memory
            Logger.info("Step 5: Compiling in memory...");
            InMemoryCompiler compiler = new InMemoryCompiler(fullyQualifiedName, finalSource);
            UserGame gameInstance = compiler.compile();
            Logger.info("  Compilation successful");
            
            Logger.info("=== Game compilation complete ===");
            
            return gameInstance;
            
        } catch (CompilationException e) {
            Logger.error("Compilation failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            Logger.error("Unexpected compilation error", e);
            throw new CompilationException("Unexpected error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compile with detailed debug output
     */
    public static UserGame compileWithDebug(String userSourceCode) {
        Logger.setLevel(Logger.Level.DEBUG);
        return compile(userSourceCode);
    }
}