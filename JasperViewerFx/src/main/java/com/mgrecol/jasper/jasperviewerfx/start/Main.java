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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Michael Grecol
 *         project JasperViewerFx
 *         filename Main.java
 *         date Mar 23, 2015
 */
public class Main extends Application {

    private static final Log logger = LogFactory.getLog(Main.class);

    private static final String JASPER_TEST_REPORT_JRXML = "/jasper/TestReport.jrxml";
    private static final String TITLE = "Jasper Viewer for JavaFx";
    private static final String REPORT_PATH_PARAM_PREFIX = "-report.path";

    private static InputStream reportInputStream;

    /**
     * @param args you can pass parameter -report.path="path_to_your_report"
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> argsList = Arrays.asList(args);

        String reportPathParam = argsList.stream()
                .filter(arg -> arg.contains(REPORT_PATH_PARAM_PREFIX))
                .findFirst()
                .orElse(null);

        if (null != reportPathParam) {
            String[] param = reportPathParam.split("=");
            if (param.length > 1) {
                reportInputStream = new FileInputStream(param[1]);
            }
        }
        if (null == reportInputStream) {
            reportInputStream = Main.class.getResourceAsStream(JASPER_TEST_REPORT_JRXML);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        JasperReport jasperReport = JasperCompileManager.compileReport(reportInputStream);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                new HashMap<>(), new JREmptyDataSource());

        JRViewer jrViewer = new JRViewer();
        Stage viewerStage = jrViewer.getViewerStage(jasperPrint);
        viewerStage.setTitle(TITLE);
        viewerStage.show();

        logger.info("Jasper viewer started");
    }
}
