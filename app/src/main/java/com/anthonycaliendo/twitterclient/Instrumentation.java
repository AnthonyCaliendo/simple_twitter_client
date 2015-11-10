package com.anthonycaliendo.twitterclient;

import android.util.Log;


/**
 * Provides instrumentation support such as logging, tracing, etc.
 */
public abstract class Instrumentation {

    /**
     * Used to enable/disable debug logging.  Set to false to disable logging.
     */
    public static boolean isDebugEnabled = true;

    /**
     * Logs the message at DEBUG level.
     * @param instrumentedObject
     *      the object being instrumented.  used to tag the log message
     * @param message
     *      the message to log
     */
    public static void debug(final Object instrumentedObject, final String message) {
        debug(instrumentedObject, message, null);
    }

    /**
     * Logs the message and exception at DEBUG level.
     * @param instrumentedObject
     *      the object being instrumented.  used to tag the log message
     * @param message
     *      the message to log
     * @param exception
     *      the exception to log
     */
    public static void debug(final Object instrumentedObject, String message, final Exception exception) {
        debug(instrumentedObject.getClass(), message, exception);
    }

    /**
     * Logs the message and exception at DEBUG level.
     * @param instrumentedClass
     *      the class being instrumented.  used to tag the log message
     * @param message
     *      the message to log
     * @param exception
     *      the exception to log
     */
    public static void debug(final Class instrumentedClass, String message, final Exception exception) {
        if (!isDebugEnabled) {
            return;
        }

        if (exception != null) {
            message = new StringBuilder(message)
                    .append(" exceptionClass=")
                    .append(exception.getClass().getSimpleName())
                    .append(" exceptionMessage=")
                    .append(exception.getMessage())
                    .toString();
        }

        Log.d(instrumentedClass.getSimpleName(), message);
    }
}
