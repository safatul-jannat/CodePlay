package com.example.codeplayfx;

import com.example.codeplayfx.ui.CodePanel;
import com.example.codeplayfx.ui.GamePanel;
import com.example.codeplayfx.ui.ControlPanel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class HelloController {

    @FXML
    private TextArea codeTextArea;

//    @FXML
//    private Button runButton;

    @FXML
    private Pane gamepane;

    private GamePanel gamePanel;
    private CodePanel codePanel;
    private ControlPanel controlPanel;

    @FXML
    private void initialize() {
        System.out.println("==CodePlay Initialized==");
        //new CodePanel(codeTextArea, runButton, new DummyRunner());

        // 1. Create Game Panel (Game Display)
        gamePanel = new GamePanel(gamepane);

        // 2. Create Code Panel (Code Editor)
        codePanel = new CodePanel(codeTextArea);

        // 3. Create Control Panel (Buttons) - It wires itself!
        controlPanel = new ControlPanel(codePanel, gamePanel);

        // 4. Add Control Panel to layout
        addControlPanelToLayout();

        System.out.println("✓ All panels initialized");

    }

//    @FXML
//   // private void onRun() {
//        System.out.println("Run button clicked");
//    }

    private void addControlPanelToLayout() {
        if (gamepane.getParent() instanceof VBox) {
            VBox parent = (VBox) gamepane.getParent();
            parent.getChildren().add(controlPanel.getView());
        }
    }
}

