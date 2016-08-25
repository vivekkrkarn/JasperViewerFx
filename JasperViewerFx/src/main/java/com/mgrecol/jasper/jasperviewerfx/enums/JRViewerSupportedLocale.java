package com.mgrecol.jasper.jasperviewerfx.enums;

import java.util.Locale;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 *         created on 25.08.2016
 */
public enum JRViewerSupportedLocale {
    EN,
    RU,
    UA;

    private Locale locale;

    JRViewerSupportedLocale() {
        this.locale = new Locale(this.name());
    }

    public Locale getLocale() {
        return locale;
    }
}
