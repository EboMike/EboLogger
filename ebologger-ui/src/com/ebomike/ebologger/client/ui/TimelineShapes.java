package com.ebomike.ebologger.client.ui;

import com.ebomike.ebologger.client.model.HostThread;
import com.ebomike.ebologger.client.model.LogMsg;
import com.ebomike.ebologger.client.model.Severity;
import com.sun.istack.internal.Nullable;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class TimelineShapes extends Pane implements ListChangeListener<LogMsg> {
    @Nullable
    private TimelineView timelineView;

    // Map each thread to an index - we're rendering them in this order. 0-based.
    private Map<HostThread, Integer> threadOrder = new HashMap<>();

    // Map a severity to a color.
    private static Map<Integer, Color> colors = new HashMap<>();

    // Next index to use for the threadOrder.
    private int nextThreadIndex = 0;

    // Height of each thread header
    private int threadHeaderHeight = 32;

    // Height of each thread block
    private int threadHeight = 64;

    // Height of each grouping
    private int groupHeight = 20;

    // Width of each event
    private int eventWidth = 20;

    private static final DropShadow dropShadow = new DropShadow(2.0, 2.0, 2.0,
            Color.rgb(50, 50, 50, .588));

    @Nullable
    private ObservableList<LogMsg> logsList = null;

    static {
        colors.put(Severity.VERBOSE.getId(), Color.LIGHTGRAY);
        colors.put(Severity.DEBUG.getId(), Color.DARKGRAY);
        colors.put(Severity.INFO.getId(), Color.GRAY);
        colors.put(Severity.WARNING.getId(), Color.ORANGE);
        colors.put(Severity.ERROR.getId(), Color.RED);
        colors.put(Severity.WTF.getId(), Color.RED);
    }

    private class MouseHandler {
        private double startXpos;

        private double startYpos;

        private double startTranslateX;

        private double startTranslateY;

        private boolean pan;

//        private DragMode dragMode;


        public void onClick(MouseEvent event) {
            if (event.isPrimaryButtonDown()) {
                // Scroll.
                startXpos = event.getSceneX();
                startYpos = event.getSceneY();

                startTranslateX = getTranslateX();
                startTranslateY = getTranslateY();
                pan = true;

            }
        }

        public void onDragged(MouseEvent event) {
            if (pan) {
                if (event.isPrimaryButtonDown()) {
                    double deltaX = event.getSceneX() - startXpos;
                    double deltaY = event.getSceneY() - startYpos;

                    setTranslateX(startTranslateX + deltaX);
                    setTranslateY(startTranslateY + deltaY);

                    event.consume();
                }
            }
        }

        private void onRelease(MouseEvent event) {
            if (!event.isPrimaryButtonDown()) {
                System.out.println("END");
                pan = false;
            }
        }
    };

    private MouseHandler mouseHandler = new MouseHandler();

    private class ScrollHandler {
        public void onStarted(ScrollEvent event) {
        }

        public void onScroll(ScrollEvent event) {
            double delta = event.getDeltaY();
            if (delta != 0.0) {
                timelineView.zoomBy(event.getX(), delta * 0.5);
                //setScaleZ(getScaleZ() * event.getMultiplierY());
                System.out.println("Multiplier: " + event.getDeltaY() + ", new value: " + getScaleZ());
                event.consume();
            }
        }

        public void onFinished(ScrollEvent event) {

        }
    }

    private ScrollHandler scrollHandler = new ScrollHandler();

    TimelineShapes() {
        addEventFilter(MouseEvent.MOUSE_PRESSED, event -> mouseHandler.onClick(event));
        addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> mouseHandler.onDragged(event));
        addEventFilter(MouseEvent.MOUSE_RELEASED, event -> mouseHandler.onRelease(event));

        addEventFilter(ScrollEvent.SCROLL_STARTED, event -> scrollHandler.onStarted(event));
        addEventFilter(ScrollEvent.SCROLL, event -> scrollHandler.onScroll(event));
        addEventFilter(ScrollEvent.SCROLL_FINISHED, event -> scrollHandler.onFinished(event));
    }

    void setItems(@Nullable ObservableList<LogMsg> logsList) {
        if (this.logsList != null) {
            this.logsList.removeListener(this);
        }
        this.logsList = logsList;

        if (logsList != null) {
            logsList.addListener(this);
        }

        if (timelineView != null) {
            initStartTime();
            createElements();
        }
    }

    private void createElements() {
        getChildren().clear();

        if (logsList == null) {
            return;
        }

        for (LogMsg logMsg : logsList) {
            Rectangle rect = new Rectangle(getXPos(logMsg), getYpos(logMsg), eventWidth, groupHeight);
            rect.setFill(getColor(logMsg));
            rect.setStroke(Color.BLACK);
            rect.setEffect(dropShadow);
            rect.setOnContextMenuRequested(e -> new TimelineContextMenu(e, rect, logMsg).show());
            getChildren().add(rect);
        }

        for (Map.Entry<HostThread, Integer> thread : threadOrder.entrySet()) {
            if (thread.getKey() == null) {
                continue;
            }
            double yPos = thread.getValue() * threadHeight;

            assert(thread != null);
            assert(thread.getKey() != null);

            HostThread ht = thread.getKey();
            assert(ht != null);
            if (ht == null) {
                System.out.println("Null");
            }

            String name = ht.getName();
            assert(name != null);



            Text label = new Text(10.0, yPos, name);
            getChildren().add(label);
        }
    }

    private Color getColor(LogMsg msg) {
        Color result = colors.get(msg.getSeverity());

        if (result == null) {
            result = Color.GRAY;
        }

        return result;
    }

    private int getXPos(LogMsg msg) {
        return timelineView.getXpos(msg.getTimestamp());
    }

    private int getYpos(LogMsg msg) {
        // The primary factor is the thread.
        int threadIndex = threadOrder.computeIfAbsent(msg.getThread(), k -> nextThreadIndex++);

        // Add the thread header.
        int yPos = threadIndex * threadHeight + threadHeaderHeight;

        return yPos;
    }

    /**
     * Set the start time to something reasonable if we don't currently have a start time yet.
     * Won't do anything unless there is at least one log entry.
     */
    private void initStartTime() {
        if (timelineView.getStartTime() == 0 && logsList != null && logsList.size() != 0) {
            timelineView.setStartTime(logsList.get(0).getTimestamp());
        }
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public void onChanged(Change<? extends LogMsg> c) {
        // TODO: Don't do anything if affected elements are not visible.
        initStartTime();
        createElements();
    }

    public void setTimelineView(TimelineView timelineView) {
        this.timelineView = timelineView;
        timelineView.getObservable().addListener(e -> createElements());

        createElements();
    }
}
