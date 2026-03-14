package com.example.codeplayfx.ui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Popup;

public class HelpPopupController {

    @FXML private Button closeButton;

    private Popup popup;

    @FXML
    public void initialize() {
        System.out.println("HelpPopupController initialized");
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    @FXML
    private void handleClose() {
        if (popup != null) {
            popup.hide();
        }
    }
}