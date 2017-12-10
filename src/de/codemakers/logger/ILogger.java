package de.codemakers.logger;

/**
 * ILogger
 *
 * @author Paul Hagedorn
 */
public interface ILogger {

    /**
     * Logs an object
     *
     * @param object Object (e.g. String)
     * @param objects Optionally arguments
     */
    public void log(Object object, Object... objects);

    /**
     * Logs an object and an error
     *
     * @param object Object (e.g. String)
     * @param throwable Error (e.g. Exception)
     * @param objects Optionally arguments
     */
    public void logErr(Object object, Throwable throwable, Object... objects);

}
