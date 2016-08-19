/**
 *
 */
package com.mgrecol.jasper.jasperviewerfx.controller;

import com.mgrecol.jasper.jasperviewerfx.JRViewerMode;
import com.mgrecol.jasper.jasperviewerfx.TransactionResult;
import com.mgrecol.jasper.jasperviewerfx.WarningToast;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Popup;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Michael Grecol
 * @project JasperViewerFx
 * @filename JRViewerController.java
 * @date Mar 23, 2015
 */
public class JRViewerController {

    private final Log logger = LogFactory.getLog(getClass());

    private JRViewerMode printMode;
    private String reportFilename;
    private JRDataSource reportDataset;
    @SuppressWarnings("rawtypes")
    private Map reportParameters;
    private ChangeListener<Number> zoomListener;
    private JasperPrint jasperPrint;
    @FXML
    private ImageView imageView;
    @FXML
    ComboBox<Integer> pageList;
    @FXML
    Slider zoomLevel;
    @FXML
    private TitledPane resultPane;
    @FXML
    private Accordion resultAccordion;
    @FXML
    private Label resultDescription;
    @FXML
    protected Node view;
    private Stage parentStage;
    private Double zoomFactor;
    private double imageHeight;
    private double imageWidth;
    private List<Integer> pages = new ArrayList<>();
    private Popup popup;
    private Label errorLabel;
    boolean showingToast;

    public void show() {
        if (reportParameters == null) reportParameters = new HashMap();
        if (null == printMode || JRViewerMode.REPORT_VIEW.equals(printMode)) {
            view();
        } else if (JRViewerMode.REPORT_PRINT.equals(printMode)) {
            print();
        }
    }

    private void view() {
        popup = new Popup();
        errorLabel = new Label("Error");
        errorLabel.setWrapText(true);
        errorLabel.setMaxHeight(200);
        errorLabel.setMinSize(100, 100);
        errorLabel.setMaxWidth(100);
        errorLabel.setAlignment(Pos.TOP_LEFT);
        errorLabel.getStyleClass().add("errorToastLabel");
        popup.getContent().add(errorLabel);
        errorLabel.opacityProperty().bind(popup.opacityProperty());
        zoomFactor = 1d;
        zoomLevel.setValue(100d);
        imageView.setX(0);
        imageView.setY(0);
        imageHeight = jasperPrint.getPageHeight();
        imageWidth = jasperPrint.getPageWidth();
        if (zoomListener != null) {
            zoomLevel.valueProperty().removeListener(zoomListener);
        }
        zoomListener = (observable, oldValue, newValue) -> {
            zoomFactor = newValue.doubleValue() / 100;
            imageView.setFitHeight(imageHeight * zoomFactor);
            imageView.setFitWidth(imageWidth * zoomFactor);
        };

        zoomLevel.valueProperty().addListener(zoomListener);
        if (!jasperPrint.getPages().isEmpty()) {
            viewPage(0);
            pages = new ArrayList<>();
            for (int i = 0; i < jasperPrint.getPages().size(); i++)
                pages.add(i + 1);
        }
        pageList.setItems(FXCollections.observableArrayList(pages));
        pageList.getSelectionModel().select(0);
    }

    @FXML
    public boolean save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Document", Arrays.asList("*.pdf", "*.PDF")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG image", Arrays.asList("*.png", "*.PNG")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("DOCX Document", Arrays.asList("*.docx", "*.DOCX")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XLSX Document", Arrays.asList("*.xlsx", "*.XLSX")));
        fileChooser.getExtensionFilters().add(new ExtensionFilter("HTML Document", Arrays.asList("*.html", "*.HTML")));
        File file = fileChooser.showSaveDialog(parentStage);
        if (fileChooser.getSelectedExtensionFilter() != null && fileChooser.getSelectedExtensionFilter().getExtensions() != null) {
            List<String> selectedExtension = fileChooser.getSelectedExtensionFilter().getExtensions();
            if (selectedExtension.contains("*.pdf")) {
                try {
                    JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
                } catch (JRException e) {
                    logger.error(e);
                }
            } else if (selectedExtension.contains("*.png")) {
                for (int i = 0; i < jasperPrint.getPages().size(); i++) {
                    String fileNumber = "0000" + Integer.toString(i + 1);
                    fileNumber = fileNumber.substring(fileNumber.length() - 4, fileNumber.length());
                    WritableImage image = getImage(i);
                    String[] fileTokens = file.getAbsolutePath().split("\\.");
                    String filename = "";

                    //add number to filename
                    if (fileTokens.length > 0) {
                        for (int i2 = 0; i2 < fileTokens.length - 1; i2++) {
                            filename = filename + fileTokens[i2] + ((i2 < fileTokens.length - 2) ? "." : "");
                        }
                        filename = filename + fileNumber + "." + fileTokens[fileTokens.length - 1];
                    } else {
                        filename = file.getAbsolutePath() + fileNumber;
                    }
                    logger.info(filename);
                    File imageFile = new File(filename);
                    try {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
                        logger.info(imageFile.getAbsolutePath());
                    } catch (IOException e) {
                        logException(e);
                    }

                }

            } else if (selectedExtension.contains("*.html")) {
                try {
                    JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getAbsolutePath());
                } catch (JRException e) {
                    logException(e);
                }
            } else if (selectedExtension.contains("*.docx")) {
                JRDocxExporter exporter = new JRDocxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, file.getAbsolutePath());
                try {
                    exporter.exportReport();
                } catch (JRException e) {
                    logException(e);
                }
                logger.info("docx");
            } else if (selectedExtension.contains("*.xlsx")) {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, file.getAbsolutePath());
                try {
                    exporter.exportReport();
                } catch (JRException e) {
                    logException(e);
                }
                logger.info("xlsx");
            }
        }
        return false;
    }

    private void logException(Exception e) {
        TransactionResult t = new TransactionResult();
        t.setResultNumber(-1);
        t.setResult("Error Saving Report");
        t.setResultDescription(e.getMessage());
        setTransactionResult(t);
        logger.error(t.toString(), e);
    }

    private WritableImage getImage(int pageNumber) {
        BufferedImage image = null;
        try {
            image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, pageNumber, 2);
        } catch (JRException e) {
            logger.error(e);
        }
        WritableImage fxImage = new WritableImage(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
        return SwingFXUtils.toFXImage(image, fxImage);

    }

    private void viewPage(int pageNumber) {
        imageView.setFitHeight(imageHeight * zoomFactor);
        imageView.setFitWidth(imageWidth * zoomFactor);
        imageView.setImage(getImage(pageNumber));
    }

    @FXML
    private void print() {
        try {
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {
            logger.error(e);
        }
    }

    @FXML
    private void pageListSelected(final ActionEvent event) {
        logger.info("Selected page: " + (pageList.getSelectionModel().getSelectedItem() - 1));
        viewPage(pageList.getSelectionModel().getSelectedItem() - 1);
    }

    public void setTransactionResult(TransactionResult t) {
        if (t != null) {
            if (t.getTransactionTime() == null) {
                resultPane.setText(t.getResult() + "  Time: " + new Date());
            } else {
                resultPane.setText(t.getResult() + "  Time: "
                        + t.getTransactionTime());
            }

            resultDescription.setText(t.getResultDescription());
            resultPane.setVisible(true);
            resultAccordion.setVisible(true);
        } else {
            resultPane.setText("General Error Occurred" + "  Time: "
                    + new Date());
            resultDescription.setText("No data was returned.");
            resultPane.setVisible(true);
            resultAccordion.setVisible(true);
        }
        if (null != t && 0 != t.getResultNumber() && !showingToast) {
            showingToast = true;
            errorLabel.setText(t.getResult());
            popup.show(parentStage);
            popup.setOpacity(1.0d);
            WarningToast task = new WarningToast();
            task.progressProperty().addListener((observable, oldValue, newValue) -> {
                popup.setOpacity(newValue.doubleValue());
                if (newValue.doubleValue() <= 0.01d) {
                    popup.hide();
                    showingToast = false;
                }
            });
            popup.setX(view.getScene().getWindow().getX()
                    + view.getScene().getWindow().getWidth() - 100);
            popup.setY(view.getScene().getWindow().getY());
            new Thread(task).start();
        }
    }

    public void clearTransactionResult() {
        resultPane.setText("");
        resultDescription.setText("");
        resultPane.setVisible(false);
        resultAccordion.setVisible(false);
    }

    public JRViewerMode getPrintMode() {
        return printMode;
    }

    public void setPrintMode(JRViewerMode printMode) {
        this.printMode = printMode;
    }

    public String getReportFilename() {
        return reportFilename;
    }

    public void setReportFilename(String reportFilename) {
        this.reportFilename = reportFilename;
    }

    public JRDataSource getReportDataset() {
        return reportDataset;
    }

    public void setReportDataset(JRDataSource reportDataset) {
        this.reportDataset = reportDataset;
    }

    public Map getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(Map reportParameters) {
        this.reportParameters = reportParameters;
    }

    public Node getView() {
        return view;
    }

    public void setView(Node view) {
        this.view = view;
    }

    public void close() {
        parentStage.close();
    }

    public JasperPrint getJasperPrint() {
        return jasperPrint;
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }
}
