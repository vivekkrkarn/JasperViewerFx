package com.mgrecol.jasper.jasperviewerfx.service;

import com.mgrecol.jasper.jasperviewerfx.util.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * @author Michael Grecol, Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 25.08.2016
 */
public class ExportService {

    private static void exportReport(JasperPrint jasperPrint, File file,
                                     JRAbstractExporter<?, ?, OutputStreamExporterOutput, ?> exporter)
            throws JRException {
        ExporterInput inp = new SimpleExporterInput(jasperPrint);
        exporter.setExporterInput(inp);

        OutputStreamExporterOutput output = new SimpleOutputStreamExporterOutput(file);
        exporter.setExporterOutput(output);

        exporter.exportReport();
    }

    public static void saveXlsx(JasperPrint jasperPrint, File file) throws JRException {
        JRAbstractExporter<?, ?, OutputStreamExporterOutput, ?> exporter = new JRXlsxExporter();
        exportReport(jasperPrint, file, exporter);
    }

    public static void saveDocx(JasperPrint jasperPrint, File file) throws JRException {
        JRAbstractExporter<?, ?, OutputStreamExporterOutput, ?> exporter = new JRDocxExporter();
        exportReport(jasperPrint, file, exporter);
    }

    public static void saveHtml(JasperPrint jasperPrint, File file) throws JRException {
        JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getAbsolutePath());
    }

    public static void savePng(JasperPrint jasperPrint, File file) throws IOException {
        for (int i = 0; i < jasperPrint.getPages().size(); i++) {
            String fileNumber = "0000" + Integer.toString(i + 1);
            fileNumber = fileNumber.substring(fileNumber.length() - 4, fileNumber.length());
            WritableImage image = ImageUtils.getImage(jasperPrint, i);
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
            File imageFile = new File(filename);
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
        }
    }

    public static void savePdf(JasperPrint jasperPrint, File file) throws JRException {
        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
    }
}
