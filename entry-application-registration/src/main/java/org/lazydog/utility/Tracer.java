package org.lazydog.utility;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Tracer.
 *
 * @author  Ron Rickard
 */
public class Tracer {

    private static final Level DEFAULT_TRACE_LEVEL = Level.WARNING;
    private Logger logger;
    private String name;

    /**
     * Private constructor.
     *
     * @param  name  the tracer name.
     */
    private Tracer(String name) {
        logger = Logger.getLogger(name);
        logger.setLevel(DEFAULT_TRACE_LEVEL);
        this.name = name;
    }

    /**
     * Get the trace level.
     *
     * @return  the trace level.
     */
    public Level getLevel() {
        return logger.getLevel();
    }

    /**
     * Get the tracer.
     *
     * @param  name  the tracer name.
     *
     * @return  the tracer.
     */
    public static Tracer getTracer(String name) {
        return new Tracer(name);
    }

    /**
     * Set the trace level.
     *
     * @param  level  the trace level.
     */
    public void setLevel(Level level) {
        logger.setLevel(level);
    }

    /**
     * Set the trace level to the specified level name.  If the level name
     * is invalid, set the trace level to the specified default level.
     *
     * @param  name          the level name
     * @param  defaultLevel  the default level.
     */
    public void setLevel(String name, Level defaultLevel) {

        try {

            // Set the trace level to the level name.
            logger.setLevel(Level.parse(name));
        }
        catch(Exception e) {

            // The supplied level nameis invalid,
            // so set the trace level to the default level.
            logger.setLevel(defaultLevel);
        }
    }

    /**
     * Create the trace log at the specified level.
     *
     * @param  level   the trace level.
     * @param  format  the trace format.
     * @param  args    the trace arguments.
     */
    public void trace(Level level, String format, Object... args) {

        // Check if the level is appropriate to log.
        if (level.intValue() >= logger.getLevel().intValue()) {

            // Declare.
            StringBuffer message;

            // Set the trace message.
            message = new StringBuffer();
            message.append(String.format("[%1$tD %1$tT:%1$tL %1$tZ] ", new Date()))
                   .append(String.format("%s ", name))
                   .append(String.format(format, args));

            // Create the trace log.
            logger.log(level, message.toString());
        }
    }
}