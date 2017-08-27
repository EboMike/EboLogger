package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.HostCallHierarchy;
import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Model;
import com.sun.istack.internal.Nullable;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;

public class TimelineView {
    // Timestamp at left edge of window
    private long startTime = 0;

    // Number of milliseconds per pixel
    private long scale = 100;

    private int leftHeaderOffset = 0;

    // Width of the viewport in pixels. This is the actual width we can use for displaying the timeline.
    private int viewportWidth = 1;

    @Nullable
    private Model model = null;

//    private final SetProperty<Integer> highlightedProperty = new SimpleMapProperty<>(highlighted);

    // The list of log messages, with all filters from "filter" applied.
    private ObservableFilteredList logsList;

    // A set with all log messages that are supposed to be highlighted.
    private ObservableHighlightSet highlightSet;

    private final LogFilter filter = new LogFilter();

    private final LogFilter highlighter = new LogFilter();

    // Primary selected element
    private LogMsg primarySelected;

    private BooleanProperty changeNotifierProperty = new SimpleBooleanProperty(false);

    public TimelineView() {
        /*
        filter.substringProperty().addListener(e -> notifyChange());
        filter.minSeverityProperty().addListener(e -> notifyChange());

        highlighter.substringProperty().addListener(e -> updateHighlights());
        highlighter.minSeverityProperty().addListener(e -> updateHighlights());
*/
        logsList = new ObservableFilteredList(getFilter());
        highlightSet = new ObservableHighlightSet(getHighlighter());

//        highlighter.substringProperty().addListener(e -> updateHighlighter());
//        highlighter.minSeverityProperty().addListener(e -> updateHighlighter());
    }

    public Observable getObservable() {
        return changeNotifierProperty;
    }

    public ObservableList<LogMsg> getLogsList() {
        return logsList;
    }

    public SetProperty<Integer> highlightProperty() {
        return highlightSet;
    }

    public void setModel(Model model) {
        logsList.setLogList(model.getLogMsgs());
        highlightSet.setLogList(model.getLogMsgs());
        this.model = model;
    }

    public void add(LogMsg logMsg) {
        logsList.add(logMsg);
    }
/*
    private void setLogs(ListProperty<LogMsg> logs) {
        sourceLogs = logs;

        // TODO: Unregister old listeners, clear existing lists?

        logs.addListener((ListChangeListener<LogMsg>) c -> {
            for (LogMsg logMsg : c.getAddedSubList()) {
                if (highlighter.passesFilter(logMsg)) {
                    highlighted.put(logMsg.getId(), true);
                }
            }

            for (LogMsg logMsg : c.getRemoved()) {
                highlighted.remove(logMsg.getId());
            }
        });
    }
*/
    private void notifyChange() {
        changeNotifierProperty.setValue(!changeNotifierProperty.get());
    }

    /**
     * Add a certain value to the current zoom factor.
     * The viewport will be moved so the timeline at centerX remains at the same place where it currently is.
     * Calling this will invoke notifyChange().
     * The zoom value will be clamped to 1.
     *
     * @param centerX Client-local X coordinate of the point that should stay unchanged
     * @param zoomMultiplier Value to add to the zoom.
     */
    public void zoomBy(double centerX, double zoomMultiplier) {
        centerX -= leftHeaderOffset;
        double oldScale = scale;
        scale += (long) zoomMultiplier;
        scale = Math.max(scale, 1);

        /*
        Re-center the viewport so centerX shows the same timestamp as before.
        timestampAtCenterX = centerX * oldScale + startTime;
        timestampAtCenterX = centerX * newScale + newStartTime;
        centerX * oldScale + startTime = centerX * newScale + newStartTime;
        newStartTime = centerX * oldScale + startTime - centerX * newScale;
        */

        startTime = (long) (centerX * oldScale + startTime - centerX * scale);
        notifyChange();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getScale() {
        return scale;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        notifyChange();
    }

    public void setScale(long scale) {
        this.scale = scale;
    }

    public int getXpos(long timestamp) {
        long xPos = timestamp - startTime;
        xPos /= scale;

        return (int) xPos + leftHeaderOffset;
    }

    public LogMsg getPrimarySelected() {
        return primarySelected;
    }

    public void setPrimarySelected(@Nullable LogMsg primarySelected) {
        this.primarySelected = primarySelected;

        // If this is outside the viewport, scroll in.
        if (primarySelected != null) {
            int xpos = getXpos(primarySelected.getTimestamp());
            if (xpos < 0 || xpos > viewportWidth) {
                long centerOffset = viewportWidth / 2 * scale;
                startTime = primarySelected.getTimestamp() - centerOffset;
            }

            // DEBUG: Show the callstack.
            HostCallHierarchy hierarchy = primarySelected.getHierarchy();

            if (hierarchy != null && model != null) {
                System.out.println("HAVE CALLSTACK");

                while (hierarchy != null) {
                    System.out.println(String.format("%s.%s(%s:%d)",
                            model.getClassName(hierarchy.getClassId()),
                            model.getMethodName(hierarchy.getMethodId()),
                            model.getSourceFile(hierarchy.getSourceFileId()),
                            hierarchy.getLine()));

                    hierarchy = hierarchy.getParent();
                }

            }
        }
        notifyChange();
    }

    public LogFilter getFilter() {
        return filter;
    }

    public LogFilter getHighlighter() {
        return highlighter;
    }

    public void setLeftHeaderOffset(int leftHeaderOffset) {
        this.leftHeaderOffset = leftHeaderOffset;
    }

    public void setWidth(int width) {
        this.viewportWidth = width;
}
}
