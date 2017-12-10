package de.codemakers.logger;

/**
 * Logger
 *
 * @author Paul Hagedorn
 */
public class Logger {

    private static ILogger LOGGER = createDefaultLogger();

    /**
     * Creates a default logger
     *
     * @return Default ILogger
     */
    public static final ILogger createDefaultLogger() {
        return new ILogger() {
            @Override
            public final void log(Object object, Object... objects) {
                if (objects == null || objects.length == 0) {
                    System.out.println(object);
                } else {
                    System.out.println(String.format("" + object, objects));
                }
            }

            @Override
            public final void logErr(Object object, Throwable throwable, Object... objects) {
                if (object != null) {
                    if (objects == null || objects.length == 0) {
                        System.err.println(object);
                    } else {
                        System.err.println(String.format("" + object, objects));
                    }
                }
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };
    }

    /**
     * Returns the logger
     *
     * @return ILogger
     */
    public static final ILogger getLogger() {
        return LOGGER;
    }

    /**
     * Sets the (Non-null!) logger
     *
     * @param logger ILogger
     */
    public static final void setLogger(ILogger logger) {
        if (logger == null) {
            throw new NullPointerException("The ILogger must not be null");
        }
        LOGGER = logger;
    }

    /**
     * Logs an object
     *
     * @param object Object (e.g. String)
     * @param objects Optionally arguments
     */
    public static final void log(Object object, Object... objects) {
        LOGGER.log(object, objects);
    }

    /**
     * Logs an object and an error
     *
     * @param object Object (e.g. String)
     * @param throwable Error (e.g. Exception)
     * @param objects Optionally arguments
     */
    public static final void logErr(Object object, Throwable throwable, Object... objects) {
        LOGGER.logErr(object, throwable, objects);
    }

}
