package lohbihler.manfred;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.gpio.GpioFactory;
import lohbihler.manfred.i2c.I2CReader;
import lohbihler.manfred.nmea.GPSSerialReader;
import lohbihler.manfred.signal.Signaller;
import lohbihler.manfred.signal.Signaller.Signal;
import lohbihler.manfred.signal.SignallerFactory;
import lohbihler.manfred.util.JsonPropertiesLoader;
import lohbihler.manfred.util.Util;

// TODO add a battery monitor with signalling
// Add monitors to detect when data is not being received, temporally or if values are out of range.
public class FlightLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlightLogger.class);

    public static void main(String[] args) {
        new FlightLogger();
    }

    private JMap props;

    private GpioController gpio;
    private Signaller signaller;
    private DataLogger dataLogger;
    private final FlightSample flightRegister = new FlightSample();
    private final GpsSample gpsRegister = new GpsSample();
    private GPSSerialReader gpsReader;
    private I2CReader i2cReader;
    private ScheduledExecutorService timer;

    private FlightLogger() {
        String env = System.getProperty("env");
        if (env == null) {
            LOGGER.info("No env system property found. Defaulting to test.");
            env = "test";
        }

        LOGGER.info("Starting up {}...", env);

        // Load properties file. Do this first because the properties file is necessary for determining how
        // to signal problems in the rest of the startup.
        try {
            props = new JsonPropertiesLoader().load("manfred.json").get(env);
        }
        catch (final Exception e) {
            LOGGER.error("Error loading properties", e);
            return;
        }

        try {
            configure();

            // Watch for an exit command from the input.
            try (final Scanner in = new Scanner(System.in)) {
                LOGGER.info("Type 'exit' to stop");
                while (true) {
                    final String s = in.nextLine();
                    if ("exit".equals(s))
                        break;
                    LOGGER.info("Unknown command [{}]. Try 'exit'", s);
                }
                LOGGER.info("Exiting");
            }
        }
        catch (final Exception e) {
            LOGGER.error("Configuration error", e);

            signaller.setSignal("config", Signal.error);
            try {
                Thread.sleep(5000);
            }
            catch (final InterruptedException ie) {
                LOGGER.error("Signal interrupted", ie);
            }
        }

        unconfigure();
    }

    private void configure() throws Exception {
        LOGGER.info("Running configuration... ");

        // GPIO
        final String gpioFactoryClazz = props.get("gpioFactory");
        LOGGER.info("gpioFactoryClazz = {}", gpioFactoryClazz);
        final GpioFactory gpioFactory = (GpioFactory) Class.forName(gpioFactoryClazz).newInstance();
        gpio = gpioFactory.get();

        // Start the signaller
        signaller = SignallerFactory.createSignaller(gpio, props);

        // Start the timer.
        timer = Executors.newScheduledThreadPool(3);

        // Start the GPS reader.
        final JMap gpsProps = props.get("gps");
        final String gpsFactoryClazz = gpsProps.get("factory");
        LOGGER.info("gpsFactoryClazz = {}", gpsFactoryClazz);
        gpsReader = (GPSSerialReader) Class.forName(gpsFactoryClazz).newInstance();
        gpsReader.start(gpsRegister, gpsProps);

        // Start the nano reader
        i2cReader = new I2CReader(flightRegister, signaller, props);
        i2cReader.start();

        // Initialize the data logger
        final JMap dataLoggerProps = props.get("dataLogger");
        final String dataLoggerClazz = dataLoggerProps.get("class");
        LOGGER.info("dataLoggerClazz = {}", dataLoggerClazz);
        dataLogger = (DataLogger) Class.forName(dataLoggerClazz).newInstance();
        dataLogger.init(flightRegister, gpsRegister, timer, dataLoggerProps, signaller);

        // Show alert when configuration is done.
        signaller.alert(2000);
        LOGGER.info("Configuration complete.");
    }

    private void unconfigure() {
        LOGGER.info("Running unconfiguration... ");

        if (timer != null)
            timer.shutdown();

        if (dataLogger != null)
            dataLogger.stop();

        if (i2cReader != null) {
            i2cReader.stop();
            i2cReader.close();
        }

        if (gpsReader != null)
            Util.closeQuietly(gpsReader);

        if (gpio != null)
            gpio.shutdown();

        LOGGER.info("Unconfiguration complete.");
    }
}
