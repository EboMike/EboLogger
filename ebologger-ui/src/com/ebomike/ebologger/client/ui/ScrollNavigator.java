package com.ebomike.ebologger.client.ui;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class ScrollNavigator {
    private final TimelineView timelineView;

    private class MouseHandler {
        private double startXpos;

        private double startYpos;

        private long startTranslateX;

        private double startTranslateY;

        private boolean pan;

//        private DragMode dragMode;


        public void onClick(MouseEvent event) {
            if (event.isPrimaryButtonDown()) {
                // Scroll.
                startXpos = event.getSceneX();
                startYpos = event.getSceneY();

                startTranslateX = timelineView.getStartTime();
//                startTranslateY = getTranslateY();
                pan = true;

            }
        }

        public void onDragged(MouseEvent event) {
            if (pan) {
                if (event.isPrimaryButtonDown()) {
                    double deltaX = event.getSceneX() - startXpos;
                    double deltaY = event.getSceneY() - startYpos;

                    timelineView.setStartTime(startTranslateX - (long)(deltaX * timelineView.getScale()));

                    //setTranslateX(startTranslateX + deltaX);
  //                  setTranslateY(startTranslateY + deltaY);

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
                timelineView.zoomBy(event.getX(), delta * -0.5);
                //setScaleZ(getScaleZ() * event.getMultiplierY());
                event.consume();
            }
        }

        public void onFinished(ScrollEvent event) {

        }
    }

    private ScrollHandler scrollHandler = new ScrollHandler();

    public ScrollNavigator(TimelineView timelineView, Node node) {
        this.timelineView = timelineView;

        node.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> mouseHandler.onClick(event));
        node.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> mouseHandler.onDragged(event));
        node.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> mouseHandler.onRelease(event));

        node.addEventFilter(ScrollEvent.SCROLL_STARTED, event -> scrollHandler.onStarted(event));
        node.addEventFilter(ScrollEvent.SCROLL, event -> scrollHandler.onScroll(event));
        node.addEventFilter(ScrollEvent.SCROLL_FINISHED, event -> scrollHandler.onFinished(event));
    }
}
