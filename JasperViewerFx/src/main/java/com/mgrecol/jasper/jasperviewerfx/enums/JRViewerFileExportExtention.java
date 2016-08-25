package com.mgrecol.jasper.jasperviewerfx.enums;

import static javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 24.08.2016
 */
public enum JRViewerFileExportExtention {
    PDF("PDF Document", "*.pdf"),
    PNG("PNG image", "*.png", "*.PNG"),
    DOCX("DOCX Document", "*.docx"),
    XLSX("XLSX Document", "*.xlsx", "*.XLSX"),
    HTML("HTML Document", "*.html", "*.HTML");

    private ExtensionFilter extensionFilter;

    JRViewerFileExportExtention(String description, String... extensions) {
        this.extensionFilter = new ExtensionFilter(description, extensions);
    }

    public ExtensionFilter getExtensionFilter() {
        return extensionFilter;
    }
}
