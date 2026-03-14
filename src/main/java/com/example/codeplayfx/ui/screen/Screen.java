package com.example.codeplayfx.ui.screen;

import javafx.scene.Parent;
import javafx.scene.Scene;

public abstract class Screen {
    protected Parent root;
    protected Scene scene;

    public Parent getView() {
        return root;
    }

    public Scene getScene() {
        return scene;
    }
    public abstract void onShow();
}