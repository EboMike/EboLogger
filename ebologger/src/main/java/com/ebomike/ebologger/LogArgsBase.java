package com.ebomike.ebologger;

import androidx.annotation.Nullable;

/**
 * Collection of functions that allow for sanitizing and modifying arguments in log strings.
 */
public class LogArgsBase {
    /**
     * Makes a potentially null String safe for a log operation by returning the string if it's
     * valid, or "(null)" if it's null.
     * <p>
     * Example usage:
     * <pre>
     *     logger.info().log("Token found: %s", nullable(token));
     * </pre>
     *
     * @param text String to check.
     * @return The input string if non-null, or "(null)" if it's null.
     */
    public static String nullable(@Nullable String text) {
        return text == null ? "(null)" : text;
    }

    /**
     * Returns the toString() result of an Object that could potentially be null. Returns "(null)"
     * if the object is null.
     * <p>
     * Example usage:
     * <pre>
     *     logger.info().log("Current object: %s", string(object));
     * </pre>
     *
     * @param object Object to call toString() on.
     * @return The result of toString() if object is non-null, or "(null)".
     */
    public static String string(@Nullable Object object) {
        return object == null ? "(null)" : object.toString();
    }
}
