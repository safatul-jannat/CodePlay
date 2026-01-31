package com.example.codeplayfx.ui.panel;

import javafx.scene.layout.Pane;

public abstract class UIPanel {
    protected Pane root;

    public Pane getRoot() {
        return root;
    }

    public abstract void initialize();
}