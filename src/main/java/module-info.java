// module-info.java - Either REMOVE this file completely or keep it minimal:
module com.example.codeplayfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.compiler;

    // Opens EVERYTHING to JavaFX and compiler
    opens com.example.codeplayfx to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.ui to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.ui.screen to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.ui.panel to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.world to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.engine.api to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.engine.core to javafx.fxml, java.compiler;
    opens com.example.codeplayfx.engine.runtime to javafx.fxml, java.compiler;

    // Export everything
    exports com.example.codeplayfx;
    exports com.example.codeplayfx.ui;
    exports com.example.codeplayfx.ui.screen;
    exports com.example.codeplayfx.ui.panel;
    exports com.example.codeplayfx.world;
    exports com.example.codeplayfx.engine.api;
    exports com.example.codeplayfx.engine.core;
    exports com.example.codeplayfx.engine.runtime;
}