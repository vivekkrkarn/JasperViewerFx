package com.mgrecol.jasper.jasperviewerfx.service;

import com.mgrecol.jasper.jasperviewerfx.event.JRViewerPrintEvent;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.mgrecol.jasper.jasperviewerfx.event.JRViewerPrintEvent.JR_PRINT_DLG_CLOSED;
import static com.mgrecol.jasper.jasperviewerfx.event.JRViewerPrintEvent.JR_PRINT_DLG_ERROR;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 * created on 15.10.2017
 */
public class PrintService extends Service<Boolean> {

    private final Log logger = LogFactory.getLog(getClass());

    private JasperPrint jasperPrint;

    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    /*If message "no print service found" appears then use:
                        java.awt.print.PrinterJob pj = PrinterJob.getPrinterJob();
                        java.awt.print.PrintService ps = getPrintService();
                    should not be null
                    null when default printer is not set*/
                    JasperPrintManager.printReport(jasperPrint, true);
                    return true;
                } catch (Exception e) {
                    logger.error("Failed to print report", e);
                    return false;
                }
            }
        };
    }

    public void showPrintDialog(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
        setOnSucceeded(event -> {
            final Object result = event.getSource().getValue();
            if (null == result || !(boolean) result) {
                fireEvent(new JRViewerPrintEvent(JR_PRINT_DLG_ERROR));
            }
            fireEvent(new JRViewerPrintEvent(JR_PRINT_DLG_CLOSED));
        });
        restart();
    }

    public void onDialogClosed(EventHandler<JRViewerPrintEvent> eventHandler) {
        addEventHandler(JR_PRINT_DLG_CLOSED, eventHandler);
    }

    public void onDialogError(EventHandler<JRViewerPrintEvent> eventHandler) {
        addEventHandler(JR_PRINT_DLG_ERROR, eventHandler);
    }
}
