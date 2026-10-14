package com.example.codeplayfx.ui.mainwindow;

import com.example.codeplayfx.ui.popup.DocPopupController;
import com.example.codeplayfx.ui.popup.HelpPopupController;
import com.example.codeplayfx.ui.popup.SavePopupController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.function.Consumer;

public class PopupManager {

    public static void showSavePopup(Node anchorButton, Stage ownerStage,
                                     String codeToSave, String docToSave,
                                     String currentNameForSaveAs,
                                     Consumer<String> onSaveSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PopupManager.class.getResource("/com/example/codeplayfx/popups/SavePopup.fxml"));

            if (loader.getLocation() == null) {
                System.err.println("CRITICAL: SavePopup.fxml not found!");
                return;
            }

            Parent root = loader.load();
            SavePopupController controller = loader.getController();

            // Create POPUP (not Stage)
            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.getContent().add(root);

            controller.setPopup(popup);
            controller.setCodeToSave(codeToSave);
            controller.setDocumentationToSave(docToSave);
            controller.setCurrentName(currentNameForSaveAs);
            controller.setOnSaveSuccess(onSaveSuccess);

            // Position near the anchor button
            Point2D point = anchorButton.localToScreen(0, anchorButton.getLayoutBounds().getHeight());
            popup.show(ownerStage, point.getX(), point.getY());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDocPopup(Node anchorButton, Stage ownerStage, String projectName, String code, String existingDoc, Runnable onSaveSuccess) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PopupManager.class.getResource("/com/example/codeplayfx/popups/DocPopup.fxml"));

            if (loader.getLocation() == null) {
                System.err.println(" CRITICAL: DocPopup.fxml not found!");
                System.err.println("Expected at: /com/example/codeplayfx/popups/DocPopup.fxml");
                return;
            }

            Parent root = loader.load();
            DocPopupController controller = loader.getController();

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.getContent().add(root);

            controller.setPopup(popup);
            controller.setProjectData(projectName, code, existingDoc);
            controller.setOnSaveSuccess(onSaveSuccess);

            // Position the popup near the anchor button
            Point2D point = anchorButton.localToScreen(0, anchorButton.getLayoutBounds().getHeight());
            popup.show(ownerStage, point.getX(), point.getY());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showHelpPopup(Node anchorButton, Stage ownerStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(PopupManager.class.getResource("/com/example/codeplayfx/popups/HelpPopup.fxml"));

            if (loader.getLocation() == null) {
                System.err.println(" CRITICAL: HelpPopup.fxml not found!");
                System.err.println("Expected at: /com/example/codeplayfx/popups/HelpPopup.fxml");
                return;
            }

            Parent root = loader.load();
            HelpPopupController controller = loader.getController();

            Popup popup = new Popup();
            popup.setAutoHide(true);
            popup.setHideOnEscape(true);
            popup.getContent().add(root);

            controller.setPopup(popup);

            // Center the popup on screen
            Point2D point = anchorButton.localToScreen(0, 0);
            popup.show(ownerStage, point.getX() + 100, point.getY() + 50);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}