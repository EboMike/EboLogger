package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.HostThread;
import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Model;
import com.ebomike.ebologger.client.model.Severity;
import com.sun.istack.internal.Nullable;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

import java.util.*;

public class Timeline extends Canvas implements ListChangeListener<LogMsg> {
    private TimelineView timelineView;

    private Model model;

    // Map each thread to an index - we're rendering them in this order. 0-based.
    private Map<HostThread, Integer> threadOrder = new HashMap<>();

    // Map a severity to a color.
    private static Map<Integer, Color> colors = new HashMap<>();

    private final List<List<RenderBucket>> renderBuckets = new ArrayList<>();

    private final List<FilterSpec> filterSpecs = new ArrayList<>();

    private final Color SELECTED_COLOR = Color.RED;
    private final Color HIGHLIGHT_COLOR = Color.GREEN;

    // Next index to use for the threadOrder.
    private int nextThreadIndex = 0;

    // Width of the thread column
    private int threadColumnWidth = 200;

    // Height of each thread header
    private int threadHeaderHeight = 32;

    // Height of each grouping
    private int groupHeight = 20;

    // Width of each event
    private int eventWidth = 20;

    private int frameId;

    @Nullable
    private ObservableList<LogMsg> logsList = null;

    class RenderBucket {
        private int highestSeverity;

        private int eventCount;

        private boolean hasSelected;

        private boolean hasHighlight;

        private int frameId;

        private ArrayList<LogMsg> logs = new ArrayList<>();

        void refreshFrameId(int frameId) {
            if (this.frameId != frameId) {
                reset();
                this.frameId = frameId;
            }
        }

        void reset() {
            highestSeverity = 0;
            eventCount = 0;
            hasSelected = false;
            logs.clear();   // TODO: Retain capacity?
        }

        void addLogMsg(LogMsg msg) {
            eventCount++;
            hasSelected |= timelineView.getPrimarySelected() == msg;
            hasHighlight |= timelineView.highlightProperty().contains(msg.getId());
            highestSeverity = Math.max(highestSeverity, msg.getSeverity());
            logs.add(msg);
        }

        int getHighestSeverity() {
            return highestSeverity;
        }

        int getEventCount() {
            return eventCount;
        }

        boolean isHasSelected() {
            return hasSelected;
        }

        boolean isHasHighlight() {
            return hasHighlight;
        }

        boolean isValid(int frameId) {
            return this.frameId == frameId;
        }

        List<LogMsg> getLogs() {
            return logs;
        }
    }

    static {
        colors.put(Severity.VERBOSE.getId(), Color.LIGHTGRAY);
        colors.put(Severity.DEBUG.getId(), Color.DARKGRAY);
        colors.put(Severity.INFO.getId(), Color.GRAY);
        colors.put(Severity.WARNING.getId(), Color.ORANGE);
        colors.put(Severity.ERROR.getId(), Color.RED);
        colors.put(Severity.WTF.getId(), Color.RED);
    }

    Timeline() {
        filterSpecs.add(new ThreadFilterSpec());

        // Redraw canvas when size changes.
        widthProperty().addListener(event -> draw());
        heightProperty().addListener(event -> draw());

        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> onClick(event));
        addEventFilter(MouseEvent.MOUSE_MOVED, event -> onMouseEvent(event));

        final ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnShowing(e -> {
            System.out.println("showing");
            MenuItem item1 = new MenuItem("About");
            item1.setOnAction(ev -> System.out.println("About"));
            MenuItem item2 = new MenuItem("Preferences");
            item2.setOnAction(ev -> System.out.println("Preferences"));
            contextMenu.getItems().addAll(item1, item2);
        });

        contextMenu.setOnShown(e -> System.out.println("shown"));

        setOnContextMenuRequested(e -> showContextMenu(e));
    }

    private void onMouseEvent(MouseEvent event) {
        draw();
    }

    @Nullable
    public RenderBucket getRenderBucket(double x, double y) {
        // Identify the render bucket we're on right now.
        int bucketSetIndex = getThreadId((int) y);
        int bucketIndex = getBucketIndex((int) x);

        if (bucketIndex < 0 || bucketSetIndex < 0) {
            return null;
        }

        if (renderBuckets.size() > bucketSetIndex) {
            List<RenderBucket> renderBucketSet = renderBuckets.get(bucketSetIndex);

            if (renderBucketSet.size() > bucketIndex) {
                return renderBucketSet.get(bucketIndex);
            }
        }

        return null;
    }

    private void showContextMenu(ContextMenuEvent event) {
        ContextMenu contextMenu = new ContextMenu();

        RenderBucket bucket = getRenderBucket(event.getX(), event.getY());

        if (bucket != null) {
            int eventCount = Math.min(bucket.getLogs().size(), 15);

            for (LogMsg log : bucket.getLogs()) {
                MenuItem item2 = new MenuItem(log.getMsg());
                item2.setOnAction(ev -> System.out.println("Preferences"));
                contextMenu.getItems().add(item2);

                if (--eventCount <= 0) {
                    break;
                }
            }
            contextMenu.show(getScene().getWindow());
            event.consume();
        }
    }

    private void onClick(MouseEvent event) {
        int bucketIndex = getBucketIndex((int) event.getX());
        int threadId = getThreadId((int) event.getY());

        if (threadId >= 0 && threadId < renderBuckets.size()) {
            List<RenderBucket> buckets = renderBuckets.get(threadId);

            if (bucketIndex >= 0 && bucketIndex < buckets.size()) {
                RenderBucket bucket = buckets.get(bucketIndex);

                if (bucket != null) {
                    if (bucket.isValid(frameId)) {
                        if (bucket.getLogs().size() > 0) {
                            timelineView.setPrimarySelected(bucket.getLogs().get(0));
                        }
                    }
                }
            }
        }
    }

    public void setTimelineView(TimelineView timelineView) {
        this.timelineView = timelineView;
        timelineView.setLeftHeaderOffset(threadColumnWidth);
        timelineView.setWidth((int) getWidth() - threadColumnWidth);
        timelineView.getObservable().addListener(e -> draw());

        widthProperty().addListener((observable, oldValue, newValue) ->
                timelineView.setWidth((int) newValue.doubleValue() - threadColumnWidth));

        setItems(timelineView.getLogsList());
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setItems(@Nullable ObservableList<LogMsg> logsList) {
        if (this.logsList != null) {
            this.logsList.removeListener(this);
        }

        this.logsList = logsList;
        initStartTime();

        if (logsList != null) {
            logsList.addListener(this);
        }
    }

    private boolean isVisible(LogMsg logMsg) {
        double width = getWidth();
        long maxTimestamp = timelineView.getStartTime() + ((long) width * timelineView.getScale());

        long timestamp = logMsg.getTimestamp();

        return timestamp >= timelineView.getStartTime() && timestamp < maxTimestamp;
    }

    private void draw() {
        double width = getWidth();
        double height = getHeight();

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);

        if (logsList == null || timelineView == null) {
            return;
        }

        long startTime = timelineView.getStartTime();
        long scale = timelineView.getScale();

        frameId++;

        long quantizedStartTime = startTime - (startTime % (eventWidth * scale));
        long maxTimestamp = quantizedStartTime + ((long) width * scale);

        List<LogFilter> filters = new ArrayList<>();

        if (model != null) {
            filters.addAll(Arrays.asList(filterSpecs.get(0).generateFilters(timelineView.getFilter(), model)));
        }

        List<String> labels = new ArrayList<>();

        for (LogFilter filter : filters) {
            labels.add(filter.createLabel());
        }

        // Background shading.
        gc.setFill(new Color(0.92f, 0.92f, 0.92f, 1.0f));
        for (int y=0; y<labels.size(); y += 2) {
            double ypos = (double) y * threadHeaderHeight;
            gc.fillRect(0.0f, ypos, width, threadHeaderHeight);
        }

        drawGrid(gc);

        // Step 1: Go through the log and create render buckets out of
        // all entries.
        for (LogMsg logMsg : logsList) {
            long timestamp = logMsg.getTimestamp();

            if (quantizedStartTime <= timestamp && timestamp < maxTimestamp) {
//                if (timelineView.getFilter().passesFilter(logMsg)) {
                    int bucketId = (int) ((timestamp - quantizedStartTime) / scale / eventWidth);

                    int threadId = 0;
                    for (LogFilter filter : filters) {
                        if (filter.passesFilter(logMsg)) {
                            break;
                        }

                        threadId++;
                    }

//                    int threadId = logMsg.getThread() != null ? logMsg.getThread().getId() : 0;
                    while (renderBuckets.size() <= threadId) {
                        renderBuckets.add(new ArrayList<>());
                    }

                    List<RenderBucket> buckets = renderBuckets.get(threadId);
                    while (buckets.size() <= bucketId) {
                        buckets.add(new RenderBucket());
                    }

                    RenderBucket bucket = buckets.get(bucketId);
                    bucket.refreshFrameId(frameId);
                    bucket.addLogMsg(logMsg);

                    // If it's a marker, we can render it right now.
                    if (logMsg.getMarker() != 0) {
                        int xpos = getXpos(bucketId);
                        gc.setFill(new Color(1.0f, 1.0f, 0.0f, 0.8f));
                        gc.fillRect(xpos, 0, eventWidth, height);
                    }
//                }
            }
        }

        int xOffset = (int) (startTime / scale % eventWidth);
        float offsetFade = 1.0f - (float) xOffset / (float) eventWidth;

        for (int y=0; y<renderBuckets.size(); y++) {
            List<RenderBucket> buckets = renderBuckets.get(y);
            for (int x=0; x<buckets.size(); x++) {
                RenderBucket bucket = buckets.get(x);

                if (bucket.isValid(frameId)) {
                    if (bucket.getEventCount() > 0) {
                        Color color = getColor(bucket);

                        int xPos = getXpos(x);
                        int yPos = y * threadHeaderHeight;

                        gc.setStroke(Color.BLACK);
                        gc.setFill(color);
                        gc.setGlobalAlpha(x != 0 ? 1.0 : offsetFade);
                        gc.fillRect(xPos, yPos, eventWidth, groupHeight);
                        gc.strokeRect(xPos, yPos, eventWidth, groupHeight);

                        if (bucket.getEventCount() > 1) {
                            gc.setTextBaseline(VPos.CENTER);
                            gc.setTextAlign(TextAlignment.CENTER);
                            gc.setFill(Color.BLACK);
                            gc.fillText(Integer.toString(bucket.getEventCount()),
                                    xPos + (double) eventWidth / 2.0,
                                    yPos + (double) groupHeight / 2.0);
                        }
                    }
                }
            }
        }

        gc.setGlobalAlpha(1.0);

        // Now all the threads.
        if (model != null) {
            gc.setFill(Color.BLACK);
            gc.setTextBaseline(VPos.CENTER);
            gc.setTextAlign(TextAlignment.CENTER);

            int ypos = 0;
            for (String label : labels) {
                gc.fillText(label, (double) threadColumnWidth / 2.0,
                        ((double) ypos + 0.5) * threadHeaderHeight, (double) threadColumnWidth);
                ypos++;
            }
/*
            for (HostThread thread : model.getThreads()) {
                gc.fillText(thread.getName(), (double) threadColumnWidth / 2.0,
                        ((double) thread.getId() + 0.5) * threadHeaderHeight, (double) threadColumnWidth);
            }
            */
/*
            int ypos = 0;
            for (LogFilter filter : filters) {
                gc.fillText(filter.getthread.getName(), (double) threadColumnWidth / 2.0,
                        ((double) thread.getId() + 0.5) * threadHeaderHeight, (double) threadColumnWidth);

            }
            */
        }


 /*       for (LogMsg logMsg : logsList) {
            long timestamp = logMsg.getTimestamp();

            if (timestamp >= startTime && timestamp < maxTimestamp) {
                int xPos = getXPos(logMsg);
                int yPos = getYpos(logMsg);
                Color color = getColor(logMsg);


                gc.setStroke(Color.BLACK);
                gc.setFill(color);
                gc.fillRect(xPos, yPos, eventWidth, groupHeight);
                gc.strokeRect(xPos, yPos, eventWidth, groupHeight);
            }
        }*/
/*
        gc.setStroke(Color.RED);
        gc.strokeLine(0, 0, width, height);
        gc.strokeLine(0, height, width, 0);*/


    }

    private void drawGrid(GraphicsContext gc) {
        // Seconds?
        if (timelineView.getScale() < 100) {
            drawGrid(gc, 1000, Color.LIGHTGRAY);
        }
        if (timelineView.getScale() < 6000) {
            drawGrid(gc, 60000, Color.BLACK);
        }
    }

    private void drawGrid(GraphicsContext gc, long step, Paint stroke) {
        gc.setStroke(stroke);

        long x = timelineView.getStartTime();
        x -= x % step;
        double width = getWidth();
        double height = getHeight();
        long maxTimestamp = timelineView.getStartTime() + ((long) width * timelineView.getScale());

        while (x < maxTimestamp) {
            int xpos = getXpos(x);
            if (xpos >= threadColumnWidth) {
                gc.strokeLine(xpos, 0, xpos, height);
            }

            x += step;
        }
    }

    private void createElements() {
//        this.get
    }

    private Color getColor(LogMsg msg) {
        if (msg == timelineView.getPrimarySelected()) {
            return SELECTED_COLOR;
        }

        Color result = colors.get(msg.getSeverity());

        if (result == null) {
            result = Color.GRAY;
        }

        return result;
    }

    private Color getColor(RenderBucket bucket) {
        if (bucket.isHasSelected()) {
            return SELECTED_COLOR;
        }
        if (bucket.isHasHighlight()) {
            return HIGHLIGHT_COLOR;
        }

        Color result = colors.get(bucket.getHighestSeverity());

        if (result == null) {
            result = Color.GRAY;
        }

        return result;
    }

    private int getXoffset() {
        return (int) (timelineView.getStartTime() / timelineView.getScale() % eventWidth);
    }

    private int getXpos(int bucketIndex) {
        return bucketIndex * eventWidth + threadColumnWidth - getXoffset();
    }

    private int getXpos(long timestamp) {
        return (int) ((timestamp - timelineView.getStartTime()) / timelineView.getScale()) + threadColumnWidth;
    }

    private int getYpos(int threadId) {
        return threadId * threadHeaderHeight;
    }

    private int getThreadId(int yPos) {
        return yPos / threadHeaderHeight;
    }

    private int getBucketIndex(int xPos) {
        return (xPos + getXoffset() - threadColumnWidth) / eventWidth;
    }

    private int getYpos(LogMsg msg) {
        // The primary factor is the thread.
        int threadIndex = threadOrder.computeIfAbsent(msg.getThread(), k -> nextThreadIndex++);

        // Add the thread header.
        int yPos = threadIndex * threadHeaderHeight;

        return yPos;
    }

    /**
     * Set the start time to something reasonable if we don't currently have a start time yet.
     * Won't do anything unless there is at least one log entry.
     */
    private void initStartTime() {
        if (timelineView != null && timelineView.getStartTime() == 0 && logsList != null && logsList.size() != 0) {
             timelineView.setStartTime(logsList.get(0).getTimestamp());
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    @Override
    public void onChanged(Change<? extends LogMsg> c) {
        // TODO: Don't do anything if affected elements are not visible.
        initStartTime();
        draw();
    }
}
