package org.wms.utils;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class SceneUtil {
    private static double x;
    private static double y;
    public static void centerSceneOnScreen(Stage stage, Scene scene) {
        stage.centerOnScreen();
        addDragFunctionality(stage, scene);
    }

    private static void addDragFunctionality(Stage stage, Scene scene) {
        scene.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });
    }

}
