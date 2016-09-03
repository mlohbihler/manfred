package lohbihler.manfred.datalog.enabler;

import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.signal.Signaller;
import lohbihler.manfred.signal.Signaller.Signal;

abstract public class Enabler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Enabler.class);
    private static final String SIGNALLER_CLIENT_NAME = "dataLoggingEnabler";

    private DataLogger dataLogger;
    private Signaller signaller;

    public void init(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer, JMap props,
            DataLogger dataLogger, Signaller signaller) {
        this.dataLogger = dataLogger;
        this.signaller = signaller;

        // Indicate with a warning signal that the logger hasn't yet started
        signaller.setSignal(SIGNALLER_CLIENT_NAME, Signal.warning);

        init(flightRegister, gpsRegister, timer, props);
    }

    abstract protected void init(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer,
            JMap props);

    protected void enable() {
        try {
            dataLogger.start();
            // Clear the signaller warning
            signaller.setSignal(SIGNALLER_CLIENT_NAME, Signal.ok);
        }
        catch (Exception e) {
            LOGGER.error("Failed to start data logger", e);
            signaller.setSignal(SIGNALLER_CLIENT_NAME, Signal.error);
        }
    }
}
