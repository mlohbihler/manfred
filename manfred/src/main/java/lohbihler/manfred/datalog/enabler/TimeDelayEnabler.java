package lohbihler.manfred.datalog.enabler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.signal.Signaller;

public class TimeDelayEnabler extends Enabler {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeDelayEnabler.class);

    public TimeDelayEnabler() {
        // no op. 
    }

    /**
     * Separate initializer for programmatic use.
     */
    public TimeDelayEnabler(ScheduledExecutorService timer, long delay, DataLogger dataLogger, Signaller signaller) {
        init(null, null, timer, null, dataLogger, signaller);
        createSchedule(timer, delay);
    }

    @Override
    protected void init(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer,
            JMap props) {
        long delay = props.getLong("delay");
        createSchedule(timer, delay);
    }

    private void createSchedule(ScheduledExecutorService timer, long delay) {
        LOGGER.info("Scheduling start of logging after delay of {} ms", delay);
        timer.schedule(() -> {
            LOGGER.info("Delay complete. Starting data logger");
            enable();
            System.gc();
        }, delay, TimeUnit.MILLISECONDS);
    }
}
