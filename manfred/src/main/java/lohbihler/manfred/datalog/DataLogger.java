package lohbihler.manfred.datalog;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.enabler.Enabler;
import lohbihler.manfred.datalog.enabler.TimeDelayEnabler;
import lohbihler.manfred.signal.Signaller;

public interface DataLogger {
    static final Logger LOGGER = LoggerFactory.getLogger(DataLogger.class);

    /**
     * The initialization gives the data logger an opportunity to
     * set itself up in preparation for logging. Data loggers are
     * initially disabled, and do not start logging until start()
     * is called by an enabler.
     * 
     * @param flightRegister
     *            the current flight data
     * @param gpsRegister
     *            the current GPS data
     * @param timer
     *            the shared timer
     * @param props
     *            the configuration properties.
     * @throws Exception
     */
    public void init(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer, JMap props,
            Signaller signaller) throws Exception;

    /**
     * Called by the enabled to indicate that the logger should
     * begin storing data.
     */
    public void start() throws Exception;

    /**
     * Called by the system at shutdown to indicate that logging
     * should stop and resources should be closed.
     */
    public void stop();

    default void createEnabler(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer,
            JMap props, Signaller signaller) {
        // Create the logging enabler.
        final JMap enablerProps = props.get("enabler");
        if (enablerProps == null) {
            // Start the logger now-ish.
            new TimeDelayEnabler(timer, 1, this, signaller);
        }
        else {
            try {
                final String enablerClazz = enablerProps.get("class");
                LOGGER.info("enablerClazz = {}", enablerClazz);
                Enabler enabler = (Enabler) Class.forName(enablerClazz).newInstance();
                enabler.init(flightRegister, gpsRegister, timer, enablerProps, this, signaller);
            }
            catch (Exception e) {
                // This would be a configuration error, so convert to an RTE
                throw new RuntimeException(e);
            }
        }
    }

    default ScheduledFuture<?> scheduleAtFixedRate(final ScheduledExecutorService timer, final Runnable runnable,
            long rate) {
        return timer.scheduleAtFixedRate(runnable, rate - (System.currentTimeMillis() % rate), rate,
                TimeUnit.MILLISECONDS);
    }

    default void cancelScheduledTask(ScheduledFuture<?> task) {
        if (task != null) {
            task.cancel(false);
        }
    }
}
