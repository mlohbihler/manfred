package lohbihler.manfred.datalog;

import java.util.Timer;
import java.util.TimerTask;

public interface DataLogger {
    public void start(FlightSample flightRegister, GpsSample gpsRegister, Timer timer) throws Exception;

    public void stop();

    default TimerTask scheduleAtFixedRate(final Timer timer, final Runnable runnable, long rate) {
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };

        timer.scheduleAtFixedRate(tt, rate - (System.currentTimeMillis() % rate), rate);

        return tt;
    }

    default void cancelTimerTask(TimerTask tt) {
        if (tt != null)
            tt.cancel();
    }
}
