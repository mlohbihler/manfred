package lohbihler.manfred.datalog.text;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.DataLogger;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.signal.Signaller;

public class TextFileDataLogger implements DataLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileDataLogger.class);

    private FlightSample flightRegister;
    private GpsSample gpsRegister;
    private ScheduledExecutorService timer;
    private File file;

    private long start;
    private PrintWriter out;
    private ScheduledFuture<?> flightDataTimerTask;
    private ScheduledFuture<?> gpggaTimerTask;
    private ScheduledFuture<?> gprmcTimerTask;
    private ScheduledFuture<?> tempBattTimerTask;
    private ScheduledFuture<?> flusherTimerTask;

    @Override
    public void init(FlightSample flightRegister, GpsSample gpsRegister, ScheduledExecutorService timer, JMap props,
            Signaller signaller) {
        this.flightRegister = flightRegister;
        this.gpsRegister = gpsRegister;
        this.timer = timer;

        String date = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(System.currentTimeMillis()));
        String directory = props.get("directory");
        String filename = "log." + date + ".txt";
        file = new File(directory, filename);
        file.getParentFile().mkdirs();

        createEnabler(flightRegister, gpsRegister, timer, props, signaller);
    }

    @Override
    public void start() throws Exception {
        LOGGER.info("Writing data to {}", file);
        out = new PrintWriter(file);

        start = System.currentTimeMillis();

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
        cancelScheduledTask(flightDataTimerTask);
        cancelScheduledTask(gpggaTimerTask);
        cancelScheduledTask(gprmcTimerTask);
        cancelScheduledTask(tempBattTimerTask);
        cancelScheduledTask(flusherTimerTask);

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
