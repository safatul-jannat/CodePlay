package com.example.codeplayfx.ui.screen;

import javafx.scene.Parent;

public abstract class Screen {
    protected Parent root;

    public Parent getView() {
        return root;
    }

    public abstract void onShow();
}