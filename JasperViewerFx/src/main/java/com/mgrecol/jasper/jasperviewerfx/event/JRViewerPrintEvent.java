package com.mgrecol.jasper.jasperviewerfx.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 * created on 15.10.2017
 */
public class JRViewerPrintEvent extends Event {

    private static final EventType<JRViewerPrintEvent> ANY =
            new EventType<>(Event.ANY, "JVViewerPrintEvent");

    public static final EventType<JRViewerPrintEvent> JR_PRINT_DLG_CLOSED =
            new EventType<>(ANY, "JR_PRINT_DLG_CLOSED");

    public static final EventType<JRViewerPrintEvent> JR_PRINT_DLG_ERROR =
            new EventType<>(ANY, "JR_PRINT_DLG_ERROR");

    /**
     * Constructor
     *
     * @param eventType event type
     */
    public JRViewerPrintEvent(EventType<JRViewerPrintEvent> eventType) {
        super(eventType);
    }
}
