package com.example.codeplayfx.compiler;

import com.example.codeplayfx.core.GameWorld;
import com.example.codeplayfx.objects.Player;
import com.example.codeplayfx.objects.Enemy;

public class SimpleExecutor {

    public String execute(String code, GameWorld gameWorld) {
        try {
            System.out.println("📝 Executing code...");

            gameWorld.clearAll();
            String[] lines = code.split("[;\\n]");
            int objectsCreated = 0;

            for (String line : lines) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                if (line.contains("new Player(")) {
                    Player player = parsePlayer(line);
                    if (player != null) {
                        gameWorld.addObject(player);
                        objectsCreated++;
                    }
                } else if (line.contains("new Enemy(")) {
                    Enemy enemy = parseEnemy(line);
                    if (enemy != null) {
                        gameWorld.addObject(enemy);
                        objectsCreated++;
                    }
                }
            }

            String result = "✅ Created " + objectsCreated + " object(s)";
            System.out.println(result);
            return result;

        } catch (Exception e) {
            String error = "❌ Error: " + e.getMessage();
            System.out.println(error);
            return error;
        }
    }

    private Player parsePlayer(String line) {
        try {
            // Find "new Player(" in the line
            int start = line.indexOf("new Player(") + 11; // "new Player(" is 11 chars
            int end = line.indexOf(")", start);

            if (start < 11 || end < 0) {
                throw new IllegalArgumentException("Invalid Player constructor: " + line);
            }

            String args = line.substring(start, end);
            String[] parts = args.split(",");

            if (parts.length < 3) {
                throw new IllegalArgumentException("Player needs 3 arguments: x, y, id");
            }

            double x = parseDouble(parts[0].trim());
            double y = parseDouble(parts[1].trim());
            String id = parseString(parts[2].trim());

            System.out.println("✓ Parsed Player: " + id + " at (" + x + ", " + y + ")");
            return new Player(x, y, id);

        } catch (Exception e) {
            System.err.println("✗ Failed to parse Player: " + e.getMessage() + " in line: " + line);
            return null;
        }
    }

    private Enemy parseEnemy(String line) {
        try {
            // Find "new Enemy(" in the line
            int start = line.indexOf("new Enemy(") + 10; // "new Enemy(" is 10 chars
            int end = line.indexOf(")", start);

            if (start < 10 || end < 0) {
                throw new IllegalArgumentException("Invalid Enemy constructor: " + line);
            }

            String args = line.substring(start, end);
            String[] parts = args.split(",");

            if (parts.length < 3) {
                throw new IllegalArgumentException("Enemy needs 3 arguments: x, y, id");
            }

            double x = parseDouble(parts[0].trim());
            double y = parseDouble(parts[1].trim());
            String id = parseString(parts[2].trim());

            System.out.println("✓ Parsed Enemy: " + id + " at (" + x + ", " + y + ")");
            return new Enemy(x, y, id);

        } catch (Exception e) {
            System.err.println("✗ Failed to parse Enemy: " + e.getMessage() + " in line: " + line);
            return null;
        }
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: '" + str + "'");
        }
    }

    private String parseString(String str) {
        // Remove quotes if present
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        if (str.startsWith("'") && str.endsWith("'")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    // Helper method to extract variable name (optional)
    private String extractVariableName(String line) {
        try {
            int equalsIndex = line.indexOf('=');
            if (equalsIndex > 0) {
                String beforeEquals = line.substring(0, equalsIndex).trim();
                String[] parts = beforeEquals.split("\\s+");
                return parts[parts.length - 1];
            }
        } catch (Exception e) {
            // Ignore, return null
        }
        return null;
    }
}