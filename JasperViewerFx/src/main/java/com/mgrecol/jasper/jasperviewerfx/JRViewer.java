package com.mgrecol.jasper.jasperviewerfx;

import com.mgrecol.jasper.jasperviewerfx.controller.JRViewerController;
import com.mgrecol.jasper.jasperviewerfx.enums.JRViewerFileExportExtention;
import com.mgrecol.jasper.jasperviewerfx.enums.JRViewerSupportedLocale;
import com.mgrecol.jasper.jasperviewerfx.util.BundleUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 19.08.2016
 */
public class JRViewer {

    private static final Log logger = LogFactory.getLog(JRViewer.class);

    private static final String JRVIEWER_FXML = "/fxml/JRViewer.fxml";

    private final int sceneWidth;
    private final int sceneHeight;
    private final List<ExtensionFilter> extensionFilters;

    public JRViewer() {
        this(JRViewerSupportedLocale.EN.getLocale(),
                640, 420,
                Arrays.asList(
                        JRViewerFileExportExtention.PDF,
                        JRViewerFileExportExtention.DOCX));
    }

    public JRViewer(Locale locale,
                    int sceneWidth, int sceneHeight,
                    List<JRViewerFileExportExtention> extensionFilters) {
        BundleUtils.init(locale);
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
        this.extensionFilters = extensionFilters.stream()
                .map(JRViewerFileExportExtention::getExtensionFilter)
                .collect(Collectors.toList());
    }

    public Stage getViewerStage(JasperPrint jasperPrint) {
        Stage stage = new Stage();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(BundleUtils.getBundle());
            InputStream fxmlStream = JRViewer.class.getResourceAsStream(JRVIEWER_FXML);
            Scene scene = new Scene(loader.load(fxmlStream), sceneWidth, sceneHeight);
            stage.setScene(scene);

            JRViewerController jrViewerFxController = loader.getController();
            jrViewerFxController.setExtensionFilters(extensionFilters);
            jrViewerFxController.setJasperPrint(jasperPrint);
            jrViewerFxController.init();

            return stage;
        } catch (Exception e) {
            logger.error("Could not view report", e);
            throw new RuntimeException(e);
        }
    }
}
