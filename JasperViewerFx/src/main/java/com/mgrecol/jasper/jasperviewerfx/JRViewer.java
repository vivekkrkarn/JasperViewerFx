package com.mgrecol.jasper.jasperviewerfx;

import com.mgrecol.jasper.jasperviewerfx.controller.JRViewerController;
import com.mgrecol.jasper.jasperviewerfx.enums.JRViewerFileExportExtention;
import com.mgrecol.jasper.jasperviewerfx.enums.JRViewerSupportedLocale;
import com.mgrecol.jasper.jasperviewerfx.event.JRViewerEvent;
import com.mgrecol.jasper.jasperviewerfx.util.BundleUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mgrecol.jasper.jasperviewerfx.event.JRViewerEvent.JR_REPORT_LOAD_FAILED;
import static javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 * created on 19.08.2016
 */
public class JRViewer {

    private static final Log logger = LogFactory.getLog(JRViewer.class);

    private static final String JRVIEWER_FXML = "/fxml/JRViewer.fxml";

    private final int sceneWidth;
    private final int sceneHeight;
    private final String initialDirectory;
    private final String initialFileName;
    private final List<ExtensionFilter> extensionFilters;

    private JRViewerController jrViewerFxController;

    private void loadPages() {
        jrViewerFxController.loadPages();
    }

    private void displayPages() {
        jrViewerFxController.displayPages();
    }

    private Service<Boolean> service = new Service<Boolean>() {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try {
                        loadPages();
                        return true;
                    } catch (Exception e) {
                        logger.error("Failed to load pages: ", e);
                        return false;
                    }
                }
            };
        }
    };

    @SuppressWarnings("unused")
    public JRViewer() {
        this(JRViewerSupportedLocale.EN,
                640, 420, "./", "",
                Arrays.asList(
                        JRViewerFileExportExtention.PDF,
                        JRViewerFileExportExtention.DOCX));
    }

    public JRViewer(JRViewerSupportedLocale supportedLocale,
                    int sceneWidth, int sceneHeight,
                    String initialDirectory, String initialFileName,
                    List<JRViewerFileExportExtention> extensionFilters) {
        BundleUtils.init(supportedLocale);
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
        this.initialDirectory = initialDirectory;
        this.initialFileName = initialFileName;
        this.extensionFilters = extensionFilters.stream()
                .map(JRViewerFileExportExtention::getExtensionFilter)
                .collect(Collectors.toList());
    }

    private void onShow(Stage stage) {
        service.setOnSucceeded(event -> {
            final Object result = event.getSource().getValue();
            if (null != result && (boolean) result) {
                displayPages();
            } else {
                stage.close();
                stage.fireEvent(new JRViewerEvent(JR_REPORT_LOAD_FAILED));
            }
        });

        service.start();
    }

    public Stage buildStage(JasperPrint jasperPrint) {
        final Stage retval = new Stage();
        try {
            retval.setOnShown(event -> onShow(retval));

            FXMLLoader loader = new FXMLLoader();
            loader.setResources(BundleUtils.getBundle());
            InputStream fxmlStream = JRViewer.class.getResourceAsStream(JRVIEWER_FXML);
            Scene scene = new Scene(loader.load(fxmlStream), sceneWidth, sceneHeight);
            retval.setScene(scene);

            jrViewerFxController = loader.getController();
            jrViewerFxController.setInitialDirectory(initialDirectory);
            jrViewerFxController.setInitialFileName(initialFileName);
            jrViewerFxController.setExtensionFilters(extensionFilters);
            jrViewerFxController.setJasperPrint(jasperPrint);
            jrViewerFxController.init();

            retval.setWidth(jasperPrint.getPageWidth() + 100);

            return retval;
        } catch (Exception e) {
            logger.error("Could not view report", e);
            throw new RuntimeException(e);
        }
    }
}
