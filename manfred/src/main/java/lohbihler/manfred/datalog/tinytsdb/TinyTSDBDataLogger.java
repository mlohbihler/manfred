package lohbihler.manfred.datalog.tinytsdb;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.tinytsdb.TinyTSDB;
import org.tinytsdb.TinyTSDBFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.signal.Signaller;
import lohbihler.manfred.util.Util;

public class TinyTSDBDataLogger implements DataLogger {
    private FlightSample flightRegister;
    private GpsSample gpsRegister;
    private ScheduledExecutorService timer;

    private TinyTSDB<FlightSample> flightDb;
    private TinyTSDB<GpsSample> gpsDb;
    private ScheduledFuture<?> flightTimerTask;
    private ScheduledFuture<?> gpsTimerTask;

    @Override
    public void init(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer, JMap props,
            Signaller signaller) {
        this.flightRegister = flightRegister;
        this.gpsRegister = gpsRegister;
        this.timer = timer;

        flightDb = TinyTSDBFactory.createDatabase(new File("flight"), FlightSampleSerializer.get());
        gpsDb = TinyTSDBFactory.createDatabase(new File("gps"), GpsSampleSerializer.get());

        createEnabler(flightRegister, gpsRegister, timer, props, signaller);
    }

    @Override
    public void start() {
        // Schedule the GPS logger every second.
        flightTimerTask = scheduleAtFixedRate(timer, () -> {
            gpsDb.write("gps", System.currentTimeMillis(), gpsRegister);
        }, 1000);

        // Schedule the nano logger
        gpsTimerTask = scheduleAtFixedRate(timer, () -> {
            flightDb.write("flight", System.currentTimeMillis(), flightRegister);
        }, 10);
    }

    @Override
    public void stop() {
        cancelScheduledTask(flightTimerTask);
        cancelScheduledTask(gpsTimerTask);

        Util.closeQuietly(gpsDb);
        Util.closeQuietly(flightDb);
    }
}
