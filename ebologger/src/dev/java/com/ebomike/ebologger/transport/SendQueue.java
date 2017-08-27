package com.ebomike.ebologger.transport;

import android.support.v4.util.Pair;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A queue that receives a list of data packets to send and sends them off. The queue has a fixed
 * number of slots, if all slots fill up, it will block. If it still can't get a free slot after
 * a few seconds, it will disable the send queue and no longer process any more items.
 *
 */
public class SendQueue extends Thread {
    private static final boolean DEBUG = true;

    private static final String TAG = "LoggerSendQueue";

    private static final int BUFFER_COUNT = 128;

    // This is the buffer we're currently populating.
    private final Map<Thread, Pair<ByteArrayOutputStream, DataOutputStream>> byteStreams =
            new HashMap<>();

    private final ArrayBlockingQueue<ByteArrayOutputStream> sendQueue =
            new ArrayBlockingQueue<>(BUFFER_COUNT);

    private final ArrayBlockingQueue<ByteArrayOutputStream> availableBuffers =
            new ArrayBlockingQueue<>(BUFFER_COUNT);

    private DataOutputStream stream;

    // If true, this connection is no longer good.
    private boolean terminated;

    private int sent = 0;

    @SuppressWarnings("ObjectAllocationInLoop")
    public SendQueue() {
        super("Send Queue");
        setDaemon(true);

        // Set up all the output buffers
        for (int i=0; i<BUFFER_COUNT; i++) {
            ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream(1024);
            availableBuffers.add(outputBuffer);
        }
    }

    public void startQueue(DataOutputStream stream) {
        this.stream = stream;
        start();
    }

    public int getBytesSent() {
        return sent;
    }

    /**
     * Starts a new command. This will normally return right away, but might block temporarily if
     * the send queue is full. Only one command may be processed at a time, once the command has
     * been built, {@link #endCommand} must be called, which will place it on the queue.
     *
     * @param command ID of the command to be sent.
     * @return A stream that the payload of the command can be written to.
     */
    public DataOutputStream startCommand(int command) {
        try {
            // We should never block here. The send queue should be entirely asynchronous
            // and never block a thread that belongs to the app.
            Thread thread = Thread.currentThread();
            if (byteStreams.containsKey(thread)) {
                throw new RuntimeException("Starting new command without terminating previous one");
            }

            ByteArrayOutputStream byteStream = null;

            if (!terminated) {
                if (availableBuffers.isEmpty()) {
                    Log.w(TAG, "Send queue full - blocking now");
                }
                byteStream = availableBuffers.poll(5, TimeUnit.SECONDS); /*.take();*/
            }

            if (byteStream == null) {
                Log.e(TAG, "Send queue jammed - terminating");
                terminated = true;
                byteStream = new ByteArrayOutputStream(1024);
            }

            DataOutputStream buffer = new DataOutputStream(byteStream);

            synchronized (byteStreams) {
                byteStreams.put(thread, Pair.create(byteStream, buffer));
            }

            // Begin a new command - start with the command ID
            buffer.write(command);
            return buffer;
        } catch (InterruptedException | IOException e) {
            // Neither should happen - nobody ever interrupts, and we're not really doing IO.
            throw new RuntimeException(e);
        }
    }

    /**
     * Takes the most recent command created with {@link #startCommand} and places it onto the send
     * queue. It will be sent across the network as soon as all previous packets have been
     * processed.
     */
    public void endCommand() {
        Thread thread = Thread.currentThread();
        Pair<ByteArrayOutputStream, DataOutputStream> buffers;

        synchronized (byteStreams) {
            buffers = byteStreams.get(thread);
            byteStreams.remove(thread);
        }

        if (buffers == null) {
            throw new IllegalStateException("endCommand() called without startCommand()");
        }

        if (!terminated) {
            sendQueue.add(buffers.first);
        }
    }

    boolean isTerminated() {
        return terminated;
    }

    void terminate() {
        terminated = true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ByteArrayOutputStream msg = sendQueue.take();

                stream.write(msg.toByteArray(), 0, msg.size());
                msg.reset();
                availableBuffers.add(msg);
                stream.flush();
            } catch (InterruptedException e) {
                // Shouldn't happen... but also doesn't matter.
            }
            catch (IOException e) {
                Log.e(TAG, "Error in logger send queue", e);
                break;
            }
        }

        terminated = true;

        // We'll continue to process buffers to make sure nobody is blocked waiting
        // for them to become available.
        while (true) {
            ByteArrayOutputStream msg = null;
            try {
                msg = sendQueue.take();
                msg.reset();
                availableBuffers.add(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
