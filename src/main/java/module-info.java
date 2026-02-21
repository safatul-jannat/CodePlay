module com.example.codeplayfx {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.media;
   // requires javafx.web;

    // Other Java modules
    requires java.compiler;
    requires java.desktop;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.fxmisc.flowless;
    requires org.fxmisc.undo;
    //requires org.fxmisc.wellbehaved;
    requires org.controlsfx.controls;
    //requires bootstrapfx.core;

    requires com.almasb.fxgl.all;
    // ===== EXPORTS =====
    exports com.example.codeplayfx;
    exports com.example.codeplayfx.compiler;
    exports com.example.codeplayfx.engine.core;
    exports com.example.codeplayfx.engine.system;
    exports com.example.codeplayfx.scripting.runtime;
    exports com.example.codeplayfx.ui;
    exports com.example.codeplayfx.ui.panel;
    exports com.example.codeplayfx.ui.screen;
    exports com.example.codeplayfx.util;
    exports com.example.codeplayfx.world;
    exports com.example.codeplayfx.world.component;
    exports com.example.codeplayfx.world.entity;
    exports com.example.codeplayfx.world.physics;
    exports com.example.codeplayfx.world.render;

    // ===== OPENS =====
    // For JavaFX
    opens com.example.codeplayfx to javafx.graphics, javafx.fxml;
    opens com.example.codeplayfx.ui to javafx.fxml;
    opens com.example.codeplayfx.ui.screen to javafx.fxml;

    // For scripting/runtime (class loading)
    opens com.example.codeplayfx.scripting.runtime to javafx.graphics;
}