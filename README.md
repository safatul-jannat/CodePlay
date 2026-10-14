# CodePlay - Educational Game IDE

CodePlay is an educational JavaFX-based Game IDE that teaches Object-Oriented Programming (OOP) through visual game development. Students write Java code using a built-in API to create interactive 2D games, and the code is compiled and executed in real time.

## Features

- **Visual Game IDE**: Built with JavaFX and FXML, featuring a code editor, game canvas, console, and control panel
- **Real-time Game Execution**: User Java code is parsed, validated, compiled in-memory, and injected into a live game engine using FXGL
- **Entity API**: Pre-built entities including BlockPlayer, Enemy, Ground, KillZone, Collectible, and MessageBoard with physics, collisions, health, and more
- **Compiler Pipeline**: Custom compilation pipeline (UserCodeParser -> SafetyValidator -> TemplateExpander -> CodeGenerator -> InMemoryCompiler) that runs user code safely
- **Game Engine**: Entity-component system with collision detection, physics, rendering, and scripting runtime
- **Database Integration**: SQLite for project/library management
- **Syntax Highlighting**: RichTextFX-based code editor with syntax highlighting
- **Multi-screen UI**: Welcome, Library, Gallery, and About screens with smooth navigation

## Tech Stack

- Java 25
- JavaFX 21.0.6 (Controls, FXML, Media)
- FXGL 17.3
- RichTextFX 0.11.2
- ControlsFX 11.2.1
- SQLite (sqlite-jdbc 3.44.1.0)
- Maven for build management

## Architecture

```
User Code (API)
  -> Compiler Pipeline (parse, validate, template, generate, compile in-memory)
    -> Compiled UserGame Class
      -> ScriptRuntime
        -> GameEngine (Entity-Component System + Physics + Collision + Rendering)
          -> GamePanel (JavaFX Canvas) + Console + Controls
```

## Running the Project

```bash
# Using Maven (JavaFX plugin)
mvn clean javafx:run
```

Or run the main class `com.example.codeplayfx.MainApp` directly from your IDE.

## Project Structure

- `compiler/` - In-memory compilation and code transformation pipeline
- `engine/` - Game loop, systems (collision, physics, input), engine core
- `world/` - Entities, components, physics, and renderers
- `scripting/runtime/` - User code runtime integration
- `ui/` - Screens, controllers, panels, popups, and animations
- `manager/` - Database and gallery managers
- `model/` - Data models (Project)

## Status

The application is a functional educational IDE. It allows writing game logic in Java against the provided API, compiles it safely, and runs it in a real-time 2D game view with physics and collision detection.
