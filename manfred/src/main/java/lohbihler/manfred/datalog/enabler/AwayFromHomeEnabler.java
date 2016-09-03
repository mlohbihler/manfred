package lohbihler.manfred.datalog.enabler;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPGGA.FixQuality;

public class AwayFromHomeEnabler extends Enabler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwayFromHomeEnabler.class);

    private ScheduledFuture<?> future;

    @Override
    protected void init(final FlightSample flightRegister, final GpsSample gpsRegister,
            final ScheduledExecutorService timer, final JMap props) {
        final long checkRate;
        if (props.containsKey("checkRate"))
            checkRate = props.getLong("checkRate");
        else
            checkRate = 3000;

        final double minLat = props.getDouble("minLat");
        final double maxLat = props.getDouble("maxLat");
        final double minLng = props.getDouble("minLng");
        final double maxLng = props.getDouble("maxLng");
        LOGGER.info("Configuration: checkRate={}, minLat={}, maxLat={}, minLng={}, maxLng={}", checkRate, minLat,
                maxLat, minLng, maxLng);

        future = timer.scheduleWithFixedDelay(() -> {
            if (gpsRegister == null || gpsRegister.getGpgga() == null) {
                LOGGER.info("No GGA record");
                return;
            }

            GPGGA gpgga = gpsRegister.getGpgga();
            if (gpgga.getFixQuality() != FixQuality.gpsFix) {
                LOGGER.info("No GPS fix. Current fix is {}", gpgga.getFixQuality());
                return;
            }

            // Check if the current coordinates are near home.
            double lat = gpgga.getLatitude();
            double lng = gpgga.getLongitude();

            if (lat < minLat || lat > maxLat || lng < minLng || lng > maxLng) {
                LOGGER.info("GPS fix is outside of configured values. Enabling logging.");

                // Enable the logger
                enable();
                // Cancel this timer.
                cancel();
            }
            else {
                LOGGER.info("GPS fix is inside of configured values. lat={}, lng={}", lat, lng);
            }
        }, checkRate, checkRate, TimeUnit.MILLISECONDS);
    }

    private void cancel() {
        future.cancel(false);
    }
}
