package com.ebomike.ebologger;

import android.support.annotation.AnyThread;
import android.support.annotation.Nullable;

public abstract class LazySingleton<T> {
    /**
     * The singleton instance. This may be null, but is immutable once it is first assigned.
     */
    @Nullable
    private T instance;

    /**
     * Serves as a mutex to ensure the singleton will only be created once.
     */
    private final Object instanceMutex = new Object();

    /**
     * Creates the singleton instance.
     */
    public abstract T create();

    /**
     * Returns the singleton instance, or creates one if there is none yet using the
     * {@link #create()} method. This function is thread-safe.
     */
    @AnyThread
    public T get() {
        // Since instance is immutable after being created, we don't need to synchronize here.
        if (instance != null) {
            return instance;
        }

        synchronized(instanceMutex) {
            // Perform a second check just in case another thread beat us to it.
            if (instance == null) {
                instance = create();
            }

            return instance;
        }
    }
}
