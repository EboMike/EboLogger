package com.ebomike.ebologger.client.ui;

import com.sun.istack.internal.Nullable;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAxis extends Canvas {
    @Nullable
    private TimelineView timelineView;
//    private ObservableObjectValue<TimelineView> timelineView;

    private Font font;

    private static class DateScale {
        private final long unit;

        private final DateFormat dateFormat;

        public DateScale(long unit, DateFormat dateFormat) {
            this.unit = unit;
            this.dateFormat = dateFormat;
        }

        public long getUnit() {
            return unit;
        }

        public DateFormat getDateFormat() {
            return dateFormat;
        }
    }

    private static final DateScale scales[] = {
            // Days
            new DateScale(1000L * 3600L * 24L, DateFormat.getDateInstance(DateFormat.MEDIUM)),

            // Hours
            new DateScale(1000L * 3600L, DateFormat.getTimeInstance(DateFormat.SHORT)),

            // Seconds
            new DateScale(1000L, DateFormat.getTimeInstance(DateFormat.LONG)),

            // Milliseconds
            new DateScale(250L, new SimpleDateFormat("HH:mm:ss.SSS")),

            // Milliseconds
            new DateScale(1L, new SimpleDateFormat("HH:mm:ss.SSS")),
    };

    private static final int MARKER_DIST = 200;

    public DateAxis() {
//        super(16000.0, 24.0);
        // Redraw canvas when size changes.
        widthProperty().addListener(event -> draw());
        heightProperty().addListener(event -> draw());

        heightProperty().set(24.0);
        widthProperty().set(160.0);

        font = new Font("Arial", 12.0);
    }

    public void setTimelineView(TimelineView timelineView) {
        this.timelineView = timelineView;
        timelineView.getObservable().addListener(e -> draw());
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setStroke(Color.RED);
        gc.setFill(Color.YELLOW);
        gc.setLineWidth(3.0);
        gc.strokeLine(8.0, 8.0, Math.random() * 200.0, 4.0);
        gc.strokeLine(8.0, 8.0, 78.0, 4.0);
        gc.strokeLine(4.0, 4.0, 2000.0, 10.0);

        if (timelineView == null) {
            return;
        }

        double width = getWidth();
        double height = getHeight();

        gc.clearRect(0, 0, width, height);

        // How many milliseconds worth of data can we see on the screen?
        long range = (long) (width * timelineView.getScale());

        System.out.println("Range: " + range + ", zoom=" + timelineView.getScale());

        for (DateScale scale : scales) {
            if (range > scale.getUnit() * 3) {
                // Find the next best value in this unit.
                long start = timelineView.getStartTime();

                System.out.println("Picking unit " + scale.getUnit());

                long factor = scale.getUnit();
                while (factor / timelineView.getScale() < MARKER_DIST) {
                    factor *= 10;
                }

                System.out.println("Final factor: " + factor);

                start -= MARKER_DIST;
                start -= start % factor;

                System.out.println("Start pos: " + start);

                int lastTextPos = -MARKER_DIST;
                gc.setFill(Color.BLACK);
                gc.setFont(font);
                gc.setTextAlign(TextAlignment.LEFT);
                gc.setTextBaseline(VPos.TOP);

                while (true) {
                    int xpos = timelineView.getXpos(start);

                    if (xpos >= width) {
                        return;
                    }

                    gc.fillText(scale.getDateFormat().format(new Date(start)), xpos, 0);
                    lastTextPos = xpos;
                    start += factor;
                }
            }
        }

        gc.setStroke(Color.RED);
        gc.setFill(Color.YELLOW);
        gc.setLineWidth(3.0);
        gc.strokeLine(8.0, 8.0, 78.0, 4.0);
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
        System.out.println("Pref height: " + getHeight());
        return getHeight();
    }

    @Override
    public double minHeight(double width) {
        return 24.0;
    }

}
