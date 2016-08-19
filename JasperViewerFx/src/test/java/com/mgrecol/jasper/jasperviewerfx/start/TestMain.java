/**
 *
 */
package com.mgrecol.jasper.jasperviewerfx.start;

import com.mgrecol.jasper.jasperviewerfx.JRViewer;
import javafx.application.Application;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.util.HashMap;

/**
 * @author Michael Grecol
 * @project JasperViewerFx
 * @filename TestMain.java
 * @date Mar 23, 2015
 */
public class TestMain extends Application {

    private static final Log logger = LogFactory.getLog(TestMain.class);

    private static final String JASPER_TEST_REPORT_JRXML = "/jasper/TestReport.jrxml";
    private static final String TITLE = "Jasper Viewer for JavaFx";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            InputStream testReportStream = getClass().getResourceAsStream(JASPER_TEST_REPORT_JRXML);
            JasperReport jasperReport = JasperCompileManager.compileReport(testReportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                    new HashMap<>(), new JREmptyDataSource());

            JRViewer jrViewer = new JRViewer();
            jrViewer.show(jasperPrint, TITLE);

            logger.info("Started");
        } catch (JRException e) {
            logger.error("Could not start application", e);
            throw e;
        }
    }
}
