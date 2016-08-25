/**
 *
 */
package com.mgrecol.jasper.jasperviewerfx.controller;

import com.mgrecol.jasper.jasperviewerfx.enums.JRViewerFileExportExtention;
import com.mgrecol.jasper.jasperviewerfx.service.ExportService;
import com.mgrecol.jasper.jasperviewerfx.util.AlertUtils;
import com.mgrecol.jasper.jasperviewerfx.util.ImageUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Michael Grecol
 *         project JasperViewerFx
 *         filename JRViewerController.java
 *         date Mar 23, 2015
 */
public class JRViewerController implements Initializable {

    private final Log logger = LogFactory.getLog(getClass());

    private ResourceBundle resourceBundle;

    private JasperPrint jasperPrint;
    private List<ExtensionFilter> extensionFilters;

    private Double zoomFactor;
    private double imageHeight;
    private double imageWidth;

    @FXML
    protected Node view;
    @FXML
    private ComboBox<Integer> pageList;
    @FXML
    private Slider zoomLevel;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;

        zoomLevel.valueProperty().addListener((observable, oldValue, newValue) -> {
            zoomFactor = newValue.doubleValue() / 100;
            imageView.setFitHeight(imageHeight * zoomFactor);
            imageView.setFitWidth(imageWidth * zoomFactor);
        });
    }

    public void init() {
        zoomFactor = 1d;
        zoomLevel.setValue(100d);
        imageView.setX(0);
        imageView.setY(0);
        imageHeight = jasperPrint.getPageHeight();
        imageWidth = jasperPrint.getPageWidth();

        List<Integer> pages = new ArrayList<>();
        for (int i = 0; i < jasperPrint.getPages().size(); ) pages.add(++i);
        pageList.setItems(FXCollections.observableArrayList(pages));
        pageList.getSelectionModel().select(0);

        if (!jasperPrint.getPages().isEmpty()) viewPage(0);
    }

    @FXML
    private void save(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle(resourceBundle.getString("save.file.title"));
        fileChooser.getExtensionFilters().setAll(extensionFilters);

        File file = fileChooser.showSaveDialog(view.getScene().getWindow());
        ExtensionFilter selectedExtensionFilter = fileChooser.getSelectedExtensionFilter();

        if (null != selectedExtensionFilter) {
            try {
                if (JRViewerFileExportExtention.PDF.getExtensionFilter().equals(selectedExtensionFilter)) {
                    ExportService.savePdf(jasperPrint, file);
                } else if (JRViewerFileExportExtention.PNG.getExtensionFilter().equals(selectedExtensionFilter)) {
                    ExportService.savePng(jasperPrint, file);
                } else if (JRViewerFileExportExtention.HTML.getExtensionFilter().equals(selectedExtensionFilter)) {
                    ExportService.saveHtml(jasperPrint, file);
                } else if (JRViewerFileExportExtention.DOCX.getExtensionFilter().equals(selectedExtensionFilter)) {
                    ExportService.saveDocx(jasperPrint, file);
                } else if (JRViewerFileExportExtention.XLSX.getExtensionFilter().equals(selectedExtensionFilter)) {
                    ExportService.saveXlsx(jasperPrint, file);
                }
            } catch (Exception e) {
                logger.error("Error saving report", e);
                AlertUtils.showAlert(e, this.resourceBundle.getString("error.could.not.save"));
            }
        }
    }

    private void viewPage(int pageNumber) {
        imageView.setFitHeight(imageHeight * zoomFactor);
        imageView.setFitWidth(imageWidth * zoomFactor);
        imageView.setImage(ImageUtils.getImage(jasperPrint, pageNumber));
    }

    @FXML
    private void print() {
        try {
            JasperPrintManager.printReport(jasperPrint, true);
        } catch (JRException e) {
            logger.error(e);
            AlertUtils.showAlert(e, this.resourceBundle.getString("error.could.not.print"));
        }
    }

    @FXML
    private void pageListSelected(final ActionEvent event) {
        viewPage(pageList.getSelectionModel().getSelectedItem() - 1);

        scrollPane.setVvalue(0);
        scrollPane.setHvalue(0);
    }

    @FXML
    public void goFirstPage(ActionEvent actionEvent) {
        pageList.getSelectionModel().selectFirst();
    }

    @FXML
    public void goPrevPage(ActionEvent actionEvent) {
        pageList.getSelectionModel().selectPrevious();
    }

    @FXML
    public void goNextPage(ActionEvent actionEvent) {
        pageList.getSelectionModel().selectNext();
    }

    @FXML
    public void goLastPage(ActionEvent actionEvent) {
        pageList.getSelectionModel().selectLast();
    }

    public void setJasperPrint(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    public void setExtensionFilters(List<ExtensionFilter> extensionFilters) {
        this.extensionFilters = extensionFilters;
    }
}
