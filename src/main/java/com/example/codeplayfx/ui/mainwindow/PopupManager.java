package com.example.codeplayfx.ui.mainwindow;

import com.example.codeplayfx.ui.popup.SavePopupController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public class PopupManager {

    public static void showSavePopup(Node anchorButton, Stage ownerStage, String codeToSave, Consumer<String> onSaveSuccess) {
        try {
            // Use absolute path from resources root
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PopupManager.class.getResource("/com/example/codeplayfx/popups/savePopup.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("❌ CRITICAL: SavePopup.fxml not found!");
                System.err.println("Expected at: /com/example/codeplayfx/popup/SavePopup.fxml");
                System.err.println("Please create this folder and add the FXML file");
                return;
            }

            Parent root = loader.load();
            SavePopupController controller = loader.getController();

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.getContent().add(root);

            controller.setPopup(popup);
            controller.setCodeToSave(codeToSave);
            controller.setOnSaveSuccess(onSaveSuccess);

            Point2D point = anchorButton.localToScreen(anchorButton.getLayoutBounds().getWidth(), 0);
            popup.show(ownerStage, point.getX(), point.getY());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}