package com.mgrecol.jasper.jasperviewerfx.controller;

import com.mgrecol.jasper.jasperviewerfx.enums.JRViewerFileExportExtention;
import com.mgrecol.jasper.jasperviewerfx.service.ExportService;
import com.mgrecol.jasper.jasperviewerfx.service.PrintService;
import com.mgrecol.jasper.jasperviewerfx.util.AlertUtils;
import com.mgrecol.jasper.jasperviewerfx.util.ImageUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Michael Grecol
 * project JasperViewerFx
 * filename JRViewerController.java
 * date Mar 23, 2015
 * @author improved by Alexey Silichenko (a.silichenko@gmail.com)
 * date Sep 27, 2017
 */
public class JRViewerController implements Initializable {

    private final Log logger = LogFactory.getLog(getClass());

    private static final double SCREEN_DPI = 96;
    private static final double JASPER_DPI = 72;
    private static final double JASPER_TO_SCREEN_DPI_FIX = SCREEN_DPI / JASPER_DPI;

    private ResourceBundle resourceBundle;

    private JasperPrint jasperPrint;
    private String initialDirectory;
    private String initialFileName;
    private List<ExtensionFilter> extensionFilters;

    private Double zoomFactor = 1d;
    private double vvalue;
    private double originalPageHeight;
    private double originalPageWidth;

    private List<ImageView> pages = new ArrayList<>();

    private PrintService printService;

    @FXML
    protected BorderPane view;
    @FXML
    private ComboBox<Integer> pageList;
    @FXML
    private Slider zoomLevel;
    @FXML
    private StackPane imageHolder;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox vbox;
    @FXML
    public ImageView loadingIcon;

    @FXML
    private Button firstPageBtn;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML
    private Button lastPageBtn;

    public boolean busy() {
        return printService.isRunning();
    }

    private double pageToScrollValue(int pageNumber) {
        final double nodeY = vbox.getChildren().get(pageNumber).getLayoutY();
        final double contentH = imageHolder.getHeight();
        final double viewportH = scrollPane.getViewportBounds().getHeight();
        final double calcH = contentH - viewportH;
        return nodeY / calcH;
    }

    /**
     * number of page which body crosses middle of viewport
     *
     * @param value scroll v value
     * @return page number
     */
    private int scrollValueToPage(double value) {
        final double imageHolderH = imageHolder.getHeight();
        final double viewportH = scrollPane.getViewportBounds().getHeight();
        final double checkLinePos = viewportH / 2;
        final double calcH = imageHolderH - viewportH;
        final double testY = checkLinePos + calcH * value; // under middle of viewport

        for (int i = 0; i < vbox.getChildren().size(); i++) {
            final Node node = vbox.getChildren().get(i);
            final double nodeY = node.getLayoutY();
            final double nodeH = ((ImageView) node).getFitHeight() + vbox.getSpacing();
            if (testY >= nodeY && testY <= (nodeY + nodeH)) return i + 1;
        }
        return 1;
    }

    private ImageView scaleImageView(ImageView imageView) {
        imageView.setFitHeight(originalPageHeight * zoomFactor);
        imageView.setFitWidth(originalPageWidth * zoomFactor);
        return imageView;
    }

    /**
     * Load report and create a list of images for pages
     */
    public void loadPages() {
        final int pagesCount = jasperPrint.getPages().size();
        for (int i = 0; i < pagesCount; i++) {
            final Image image = ImageUtils.getImage(jasperPrint, i);
            pages.add(scaleImageView(new ImageView(image)));
        }
        loadingIcon.setVisible(false);
    }

    /**
     * Put images list into container on GUI
     */
    public void displayPages() {
        vbox.getChildren().setAll(pages);
        view.setDisable(false);
        disableNextBtns(pages.size() <= 1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;

        view.setDisable(true);
        view.disableProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) scrollPane.requestFocus();
        });

        imageHolder.heightProperty()
                .addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(vvalue));

        imageHolder.setOnMousePressed(event -> imageHolder.setCursor(Cursor.CLOSED_HAND));
        imageHolder.setOnDragDetected(event -> imageHolder.setCursor(Cursor.CLOSED_HAND));
        imageHolder.setOnMouseReleased(event -> imageHolder.setCursor(Cursor.OPEN_HAND));

        zoomLevel.valueProperty().addListener((observable, oldValue, newValue) -> {
            zoomFactor = newValue.doubleValue() / 100;
            vvalue = scrollPane.getVvalue();

            vbox.getChildren().forEach(node -> scaleImageView((ImageView) node));
        });

        scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            final int value = scrollValueToPage(newValue.doubleValue());
            final EventHandler<ActionEvent> onAction = pageList.getOnAction();
            pageList.setOnAction(null);
            pageList.setValue(value);
            pageList.setOnAction(onAction);
            disablePrevBtns(1 == value);
            disableNextBtns(pages.size() == value);
        });

        printService = new PrintService();
        printService.onDialogClosed(event -> view.setDisable(false));
        printService.onDialogError(event -> AlertUtils.showAlert(resourceBundle.getString("error.could.not.print")));
    }

    private void disablePrevBtns(boolean disable) {
        firstPageBtn.setDisable(disable);
        prevPageBtn.setDisable(disable);
    }

    private void disableNextBtns(boolean disable) {
        nextPageBtn.setDisable(disable);
        lastPageBtn.setDisable(disable);
    }

    public void init() {
        originalPageHeight = jasperPrint.getPageHeight() * JASPER_TO_SCREEN_DPI_FIX;
        originalPageWidth = jasperPrint.getPageWidth() * JASPER_TO_SCREEN_DPI_FIX;

        final List<Integer> pages = new ArrayList<>();
        final int pagesCount = jasperPrint.getPages().size();
        for (int i = 0; i < pagesCount; ) pages.add(++i);
        pageList.setItems(FXCollections.observableArrayList(pages));
        pageList.getSelectionModel().select(0);
    }

    @FXML
    private void onScrollKeyPressed(KeyEvent event) {
        Integer mod = null;
        if (KeyCode.PAGE_UP.equals(event.getCode())) mod = -1;
        if (KeyCode.PAGE_DOWN.equals(event.getCode())) mod = 1;

        if (null != mod) {
            final double contentH = imageHolder.getHeight();
            final double viewportH = scrollPane.getViewportBounds().getHeight();
            final double vvalue = scrollPane.getVvalue();
            scrollPane.setVvalue(vvalue + mod * (viewportH / contentH));
            event.consume();
        }
    }

    @FXML
    private void save() {
        FileChooser fileChooser = new FileChooser();
        File directory = new File(null != initialDirectory ? initialDirectory : "");
        if (!directory.exists()) directory = new File("");
        fileChooser.setInitialDirectory(directory);
        fileChooser.setInitialFileName(initialFileName);
        fileChooser.setTitle(resourceBundle.getString("save.file.title"));
        fileChooser.getExtensionFilters().setAll(extensionFilters);

        File file = fileChooser.showSaveDialog(view.getScene().getWindow());
        if (null == file) return;

        ExtensionFilter selectedExtensionFilter = fileChooser.getSelectedExtensionFilter();
        if (null != selectedExtensionFilter) {

            // Windows XP does not append extension to file name
            final String ext = selectedExtensionFilter.getExtensions().get(0)
                    .replace("*", "").toLowerCase();
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.toLowerCase().endsWith(ext)) file = new File(absolutePath + ext);

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

    @FXML
    private void print() {
        view.setDisable(true);
        printService.showPrintDialog(jasperPrint);
    }

    @FXML
    private void pageListSelected() {
        final int pageNumber = pageList.getSelectionModel().getSelectedItem() - 1;
        scrollPane.setVvalue(pageToScrollValue(pageNumber));
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

    public void setInitialDirectory(String initialDirectory) {
        this.initialDirectory = initialDirectory;
    }

    public void setInitialFileName(String initialFileName) {
        this.initialFileName = initialFileName;
    }

    public void setExtensionFilters(List<ExtensionFilter> extensionFilters) {
        this.extensionFilters = extensionFilters;
    }
}
