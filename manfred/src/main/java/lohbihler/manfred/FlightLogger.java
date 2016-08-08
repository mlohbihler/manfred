package lohbihler.manfred;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinytsdb.TinyTSDB;
import org.tinytsdb.TinyTSDBFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.nmea.GPSSerialReader;
import lohbihler.manfred.signal.Signaller;
import lohbihler.manfred.signal.Signaller.Signal;
import lohbihler.manfred.signal.SignallerFactory;
import lohbihler.manfred.tinytsdb.FlightSample;
import lohbihler.manfred.tinytsdb.FlightSampleSerializer;
import lohbihler.manfred.tinytsdb.GpsSample;
import lohbihler.manfred.tinytsdb.GpsSampleSerializer;
import lohbihler.manfred.util.JsonPropertiesLoader;

// TODO add a battery monitor with signalling
public class FlightLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlightLogger.class);

    public static void main(String[] args) {
        new FlightLogger();
    }

    private JMap props;

    private GpioFacade gpio;
    private Signaller signaller;
    private TinyTSDB<FlightSample> flightDb;
    private TinyTSDB<GpsSample> gpsDb;
    private final FlightSample flightRegister = new FlightSample();
    private final GpsSample gpsRegister = new GpsSample();
    private GPSSerialReader gpsReader;
    private Timer timer;

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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                unconfigure();
            }
        });

        try {
            configure();

            // Watch for an exit command from the input.
            try (final Scanner in = new Scanner(System.in)) {
                while (true) {
                    final String s = in.nextLine();
                    if ("exit".equals(s))
                        System.exit(0);
                    LOGGER.info("Unknown command [{}]. Try 'exit'", s);
                }
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
    }

    private void configure() throws Exception {
        LOGGER.info("Running configuration... ");

        // Configuration
        gpio = new GpioFacade();

        // Start the signaller
        signaller = SignallerFactory.createSignaller(gpio, props);

        // Start the databases
        flightDb = TinyTSDBFactory.createDatabase(new File("flight"), FlightSampleSerializer.get());
        gpsDb = TinyTSDBFactory.createDatabase(new File("gps"), GpsSampleSerializer.get());

        // Start the GPS reader.
        final String gpsPort = props.get("gpsPort");
        if (StringUtils.isBlank(gpsPort))
            LOGGER.info("GPS port not defined. Not starting GPS logger.");
        else {
            gpsReader = new GPSSerialReader(gpsRegister, gpsPort);
            gpsReader.start();
        }

        // TODO Start the nano reader

        // Start the timer.
        timer = new Timer("Log timer");

        // Schedule the GPS logger every second.
        scheduleAtFixedRate(() -> {
            LOGGER.debug("GPS Logger");
            gpsDb.write("gps", System.currentTimeMillis(), gpsRegister);
        }, 1000);

        // Schedule the nano logger
        scheduleAtFixedRate(() -> {
            LOGGER.debug("Flight Logger");
            flightDb.write("flight", System.currentTimeMillis(), flightRegister);
        }, 10);

        // Show alert when configuration is done.
        signaller.alert(2000);
        LOGGER.info("Configuration complete.");
    }

    private void unconfigure() {
        LOGGER.info("Running unconfiguration... ");

        if (timer != null)
            timer.cancel();

        if (gpsReader != null) {
            gpsReader.stop();
            closeQuietly(gpsReader);
        }

        closeQuietly(gpsDb);
        closeQuietly(flightDb);

        gpio.shutdown();

        LOGGER.info("Unconfiguration complete.");
    }

    private void scheduleAtFixedRate(final Runnable runnable, long rate) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        }, rate - (System.currentTimeMillis() % rate), rate);
    }

    private static void closeQuietly(Closeable db) {
        try {
            if (db != null)
                db.close();
        }
        catch (final IOException e) {
            LOGGER.warn("Error closing DB", e);
        }
    }
}
