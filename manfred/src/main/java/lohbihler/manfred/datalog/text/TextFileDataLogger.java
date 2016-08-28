package lohbihler.manfred.datalog.text;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;

public class TextFileDataLogger implements DataLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileDataLogger.class);

    private long start;
    private PrintWriter out;
    private TimerTask flightDataTimerTask;
    private TimerTask gpggaTimerTask;
    private TimerTask gprmcTimerTask;
    private TimerTask tempBattTimerTask;
    private TimerTask flusherTimerTask;

    @Override
    public void start(FlightSample flightRegister, GpsSample gpsRegister, Timer timer) throws Exception {
        start = System.currentTimeMillis();

        String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(System.currentTimeMillis()));
        String filename = "log." + date + ".txt";
        File file = new File("data", filename);
        file.getParentFile().mkdirs();
        LOGGER.info("Writing data to {}", file);
        out = new PrintWriter(file);

        final FlightDataTextinator flightData = new FlightDataTextinator();
        final GpggaTextinator gpggaData = new GpggaTextinator();
        final GprmcTextinator gprmcData = new GprmcTextinator();
        final TempBattTextinator tempBatt = new TempBattTextinator();

        flightDataTimerTask = scheduleAtFixedRate(timer, () -> write(flightData, flightRegister), 10);
        gpggaTimerTask = scheduleAtFixedRate(timer, () -> write(gpggaData, gpsRegister), 1000);
        gprmcTimerTask = scheduleAtFixedRate(timer, () -> write(gprmcData, gpsRegister), 5000);
        tempBattTimerTask = scheduleAtFixedRate(timer, () -> write(tempBatt, flightRegister), 60000);
        flusherTimerTask = scheduleAtFixedRate(timer, () -> out.flush(), 60000);
    }

    @Override
    public void stop() {
        cancelTimerTask(flightDataTimerTask);
        cancelTimerTask(gpggaTimerTask);
        cancelTimerTask(gprmcTimerTask);
        cancelTimerTask(tempBattTimerTask);
        cancelTimerTask(flusherTimerTask);

        if (out != null) {
            out.flush();
            out.close();
        }
    }

    public <T> void write(SampleTextinator<T> textinator, T o) {
        if (textinator.shouldTextinate(o)) {
            out.println(textinator.textinate(System.currentTimeMillis() - start, o));
        }
    }
}
