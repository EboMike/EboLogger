package com.ebomike.ebologgerdemo;

import com.ebomike.ebologger.EboLogger;

import java.util.Date;

class ThreadDemo extends Thread {
    private final EboLogger logger = EboLogger.get(this);

    ThreadDemo() {
        super("Thread Demo");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            logger.info().log("Thread tick at %d", new Date().getTime());
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                // Nobody should ever interrupt us.
            }
        }
    }
}
