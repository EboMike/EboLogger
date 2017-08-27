package com.ebomike.ebologger.model;

import android.support.annotation.AnyThread;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a glorified HashMap that maps an object of type K to a tracked object of type V.
 * A tracked object must always have an ID and a name so it can be sent to the app easily.
 *
 * {@link #get} will look up an object, and create a new tracked object if it's not in the map yet.
 * Creating a new tracked object is done by calling {@link #create} - this will typically assign a
 * new ID for this object, then send a packet to the stream, informing it about this new tracked
 * object, its name and ID.
 *
 * @param <K> Type of object being stored in this map as key
 * @param <V> Type of object that is used as a tracking object. Must be a NamedObject.
 */
public abstract class NamedObjectMap<K, V extends NamedObject> {
    /** The map of objects and their corresponding tracking objects. */
    private final Map<K, V> map = new HashMap<>();

    /**
     * Retrieves an element from the map by its key. If there is no element by this key yet,
     * it will create a new one and add it using the {@link #create} function, which will likely
     * send a packet to the stream and tell it about the new object and its name and ID.
     *
     * @param key
     * @return
     */
    @AnyThread
    public V get(K key) {
        synchronized(map) {
            V result = map.get(key);

            if (result == null) {
                result = create(key);
                map.put(key, result);
            }

            return result;
        }
    }

    @AnyThread
    public void put(K key, V value) {
        synchronized(map) {
            if (map.containsKey(key)) {
                throw new RuntimeException("Key " + key + " added twice");
            }

            map.put(key, value);
        }
    }

    /**
     * Removes all elements from this map.
     */
    public void clear() {
        synchronized(map) {
            map.clear();
        }
    }

    /**
     * Creates a new tracking object. This should assign a new ID, a name, and send a packet to
     * the stream with this information.
     *
     * @param key Object that we need a tracking object for.
     * @return The newly created tracking object.
     */
    public abstract V create(K key);
}
