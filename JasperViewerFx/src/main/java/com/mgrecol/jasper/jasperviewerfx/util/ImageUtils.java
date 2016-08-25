package com.mgrecol.jasper.jasperviewerfx.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.image.BufferedImage;

/**
 * @author Michael Grecol, Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 25.08.2016
 */
public class ImageUtils {

    private static final Log logger = LogFactory.getLog(ImageUtils.class);

    public static WritableImage getImage(JasperPrint jasperPrint, int pageNumber) {
        BufferedImage image;
        try {
            image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, pageNumber, 2);
        } catch (JRException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        WritableImage fxImage = new WritableImage(jasperPrint.getPageWidth(), jasperPrint.getPageHeight());
        return SwingFXUtils.toFXImage(image, fxImage);
    }
}
