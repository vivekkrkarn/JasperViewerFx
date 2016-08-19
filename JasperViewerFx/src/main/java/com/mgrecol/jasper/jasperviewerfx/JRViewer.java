package com.mgrecol.jasper.jasperviewerfx;

import com.mgrecol.jasper.jasperviewerfx.controller.JRViewerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 19.08.2016
 */
public class JRViewer {

    private static final Log logger = LogFactory.getLog(JRViewer.class);
    private static final String JRVIEWER_FXML = "/fxml/JRViewer.fxml";

    public void show(JasperPrint jasperPrint) {
        show(jasperPrint, "");
    }

    public void show(JasperPrint jasperPrint, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream fxmlStream = getClass().getResourceAsStream(JRVIEWER_FXML);
            Scene scene = new Scene(loader.load(fxmlStream));
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

            JRViewerController jrViewerFxController = loader.getController();
            jrViewerFxController.setJasperPrint(jasperPrint);
            jrViewerFxController.show();
        } catch (IOException e) {
            logger.error("Could not view report", e);
        }
    }
}
