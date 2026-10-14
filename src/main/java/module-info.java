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
    requires java.sql;
    // ===== EXPORTS =====
    exports com.example.codeplayfx;
    exports com.example.codeplayfx.compiler;
    exports com.example.codeplayfx.engine.core;
    exports com.example.codeplayfx.engine.system;
    exports com.example.codeplayfx.scripting.runtime;
    exports com.example.codeplayfx.ui.mainwindow.panel;
    exports com.example.codeplayfx.ui.screen;
    exports com.example.codeplayfx.util;
    exports com.example.codeplayfx.world;
    exports com.example.codeplayfx.world.component;
    exports com.example.codeplayfx.world.entity;
    exports com.example.codeplayfx.world.physics;
    exports com.example.codeplayfx.world.render;
    exports com.example.codeplayfx.ui.popup to javafx.fxml;
    exports com.example.codeplayfx.ui.controller to javafx.fxml;

    // ===== OPENS =====
    // For JavaFX
    opens com.example.codeplayfx to javafx.graphics, javafx.fxml;
    opens com.example.codeplayfx.ui.screen to javafx.fxml;

    // For scripting/runtime (class loading)
    opens com.example.codeplayfx.scripting.runtime to javafx.graphics;
    exports com.example.codeplayfx.ui.animator;
    opens com.example.codeplayfx.ui.animator to javafx.fxml;
   // exports com.example.codeplayfx.ui.controller;
    opens com.example.codeplayfx.ui.controller to javafx.fxml;
    exports com.example.codeplayfx.ui.mainwindow;
    opens com.example.codeplayfx.ui.mainwindow to javafx.fxml;

    opens com.example.codeplayfx.ui.popup to javafx.fxml;
   // opens com.example.codeplayfx.ui.controller to javafx.fxml;
}