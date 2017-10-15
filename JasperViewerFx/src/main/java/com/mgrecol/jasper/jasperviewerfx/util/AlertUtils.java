package com.mgrecol.jasper.jasperviewerfx.util;

import javafx.scene.control.Alert;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 * created on 25.08.2016
 */
public class AlertUtils {

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(BundleUtils.getLocalizedString("error.title"));
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showAlert(Exception e, String title) {
        showAlert(title, e.getMessage());
    }

    public static void showAlert(String title) {
        showAlert("", title);
    }
}
