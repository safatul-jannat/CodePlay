package com.example.codeplayfx.engine.runtime;

import com.example.codeplayfx.engine.core.GameEngine;

public class SimpleGameRunner {

    // FIXED TEMPLATE - provides proper imports and API access
    private static final String BACKEND_TEMPLATE = """
    package com.example.codeplayfx.user;
    
    // === AUTO-GENERATED IMPORTS ===
    import com.example.codeplayfx.engine.api.UserGameAPI;
    import com.example.codeplayfx.world.entities.Bot;
    import com.example.codeplayfx.world.entities.Zombie;
    
    // === USER GAME CLASS ===
    public class UserGame {
        
        // Game API instance (automatically created)
        private UserGameAPI api;
        
        // === USER-DEFINED FIELDS ===
        %s
        
        // === USER HELPER METHODS ===
        %s
        
        // === CONSTRUCTOR ===
        public UserGame() {
            // Create the API that users can use
            this.api = new UserGameAPI();
            System.out.println("UserGame initialized with API");
        }
        
        // === MAIN EXECUTE METHOD ===
        public void execute() {
            System.out.println("=== EXECUTING USER GAME ===");
            
            try {
                // === USER CODE INJECTED HERE ===
                %s
                
            } catch (Exception e) {
                System.err.println("Error in user code: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("=== USER GAME FINISHED ===");
        }
        
        // === CONVENIENCE METHODS FOR USER CODE ===
        // These allow users to call API methods directly
        
        public void log(String message) {
            api.log(message);
        }
        
        public void sleep(long milliseconds) {
            api.sleep(milliseconds);
        }
        
        public Bot createBot(double x, double y, String name) {
            return api.createBot(x, y, name);
        }
        
        public Bot createBot() {
            return api.createBot();
        }
        
        public Zombie createZombie(double x, double y) {
            return api.createZombie(x, y);
        }
        
        public Zombie createZombie() {
            return api.createZombie();
        }
        
        public void printWorldInfo() {
            api.printWorldInfo();
        }
        
        public void clearWorld() {
            api.clearWorld();
        }
        
        // Movement methods
        public void moveRight(Bot bot) {
            api.moveRight(bot);
        }
        
        public void moveLeft(Bot bot) {
            api.moveLeft(bot);
        }
        
        public void moveRight(Bot bot, double distance) {
            api.moveRight(bot, distance);
        }
        
        public void moveLeft(Bot bot, double distance) {
            api.moveLeft(bot, distance);
        }
        
        // Batch methods
        public java.util.List<Bot> createBots(int count, double startX, double startY, String baseName) {
            return api.createBots(count, startX, startY, baseName);
        }
        
        public java.util.List<Zombie> createZombies(int count, double startX, double startY) {
            return api.createZombies(count, startX, startY);
        }
    }
    """;

    public static boolean runUserGame(String userUITemplate, GameEngine engine) {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("RUNNING USER GAME");
            System.out.println("=".repeat(50));

            // 1. Extract user code parts
            UserCodeExtractor.UserCode userCode =
                    UserCodeExtractor.extractUserCode(userUITemplate);

            System.out.println("Parsed user code: " + userCode);

            // 2. Create complete Java source code
            String completeSource = String.format(
                    BACKEND_TEMPLATE,
                    userCode.fields,
                    userCode.helperMethods,
                    userCode.executeBody
            );

            // 3. Show first few lines for debugging
            System.out.println("\nGenerated source (first 20 lines):");
            String[] lines = completeSource.split("\n");
            for (int i = 0; i < Math.min(20, lines.length); i++) {
                System.out.println(String.format("%3d: %s", i + 1, lines[i]));
            }
            System.out.println("...\n");

            // 4. Save to file for debugging (optional)
            try {
                java.nio.file.Files.write(
                        java.nio.file.Paths.get("debug_generated.java"),
                        completeSource.getBytes()
                );
                System.out.println("Saved generated code to debug_generated.java");
            } catch (Exception e) {
                System.err.println("Could not save debug file: " + e.getMessage());
            }

            // 5. Compile in memory
            Class<?> clazz = InMemoryCompiler.compile(
                    "com.example.codeplayfx.user.UserGame",
                    completeSource
            );

            if (clazz == null) {
                System.err.println("Compilation failed!");
                return false;
            }

            // 6. Create instance and run execute() method
            System.out.println("Creating game instance...");
            Object gameInstance = clazz.getDeclaredConstructor().newInstance();

            // 7. Run in separate thread (non-blocking)
            Thread gameThread = new Thread(() -> {
                try {
                    System.out.println("Starting user game execution...");
                    clazz.getMethod("execute").invoke(gameInstance);
                    System.out.println("User game execution completed!");
                } catch (Exception e) {
                    System.err.println("Error during game execution: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            gameThread.setDaemon(true); // Don't block app exit
            gameThread.start();

            return true;

        } catch (Exception e) {
            System.err.println("Failed to run user game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}