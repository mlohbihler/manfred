package lohbihler.manfred.datalog.tinytsdb;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.tinytsdb.TinyTSDB;
import org.tinytsdb.TinyTSDBFactory;

import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.util.Util;

public class TinyTSDBDataLogger implements DataLogger {
    private TinyTSDB<FlightSample> flightDb;
    private TinyTSDB<GpsSample> gpsDb;
    private TimerTask flightTimerTask;
    private TimerTask gpsTimerTask;

    @Override
    public void start(FlightSample flightRegister, GpsSample gpsRegister, Timer timer) {
        flightDb = TinyTSDBFactory.createDatabase(new File("flight"), FlightSampleSerializer.get());
        gpsDb = TinyTSDBFactory.createDatabase(new File("gps"), GpsSampleSerializer.get());

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
        cancelTimerTask(flightTimerTask);
        cancelTimerTask(gpsTimerTask);

        Util.closeQuietly(gpsDb);
        Util.closeQuietly(flightDb);
    }
}
