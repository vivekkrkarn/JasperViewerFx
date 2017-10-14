package com.mgrecol.jasper.jasperviewerfx.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * @author Alexey Silichenko (a.silichenko@gmail.com)
 * created on 14.10.2017
 */
public class JRViewerEvent extends Event {

    private static final EventType<JRViewerEvent> ANY =
            new EventType<>(Event.ANY, "JRViewerEvent");

    public static final EventType<JRViewerEvent> JR_REPORT_LOAD_FAILED =
            new EventType<>(ANY, "JR_REPORT_LOAD_FAILED");

    /**
     * Constructor
     *
     * @param eventType event type
     */
    public JRViewerEvent(EventType<JRViewerEvent> eventType) {
        super(eventType);
    }
}
