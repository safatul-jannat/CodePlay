module com.example.codeplayfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.codeplayfx to javafx.fxml;
    exports com.example.codeplayfx;
}