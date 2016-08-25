package com.mgrecol.jasper.jasperviewerfx.util;

import javafx.scene.control.Alert;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 25.08.2016
 */
public class AlertUtils {

    public static void showAlert(Exception e, String header) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(BundleUtils.getLocalizedString("error.title"));
        alert.setHeaderText(header);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}
