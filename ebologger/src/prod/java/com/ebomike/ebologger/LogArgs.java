package com.ebomike.ebologger;

public class LogArgs extends LogArgsBase {
    /**
     * Modifies a string that could contain PII based on the development environment.
     *
     * Every string that could possibly contain personally identifiable information, such as names,
     * email addresses, phone numbers, location, etc, should be wrapped in this function. In release
     * builds, the strings will be replaced with a generic string.
     * <p>
     * Example usage:
     * <pre>
     *     logger.info().log("Email address found: %s", pii(emailAddress));
     * </pre>
     *
     * @param text String that could contain PII.
     * @return The input string in dev builds, a placeholder string in release builds.
     */
    public static String pii(String text) {
        return "[PII]";
    }
}
