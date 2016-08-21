package lohbihler.manfred.nmea.message;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.messaging2.IncomingMessage;

/**
 * Message structures can be found here: http://aprs.gids.nl/nmea/
 *
 * @author Matthew
 */
abstract public class NmeaMessage implements IncomingMessage {
    static final Logger LOG = LoggerFactory.getLogger(NmeaMessage.class);

    public static final IgnoredNmeaMessage IGNORED = new IgnoredNmeaMessage();

    /**
     * Placeholder class that is returned when the NMEA message is ignored. This is
     * required to be able to tell the difference between when there is not enough
     * bytes for a complete message, and when the bytes were not used.
     */
    public static class IgnoredNmeaMessage extends NmeaMessage {
        IgnoredNmeaMessage() {
        }

        @Override
        public String getNmeaMessageType() {
            return "(ignored)";
        }
    }

    public static enum Compass {
        N(1), S(-1), E(1), W(-1);

        public final int sign;

        private Compass(final int sign) {
            this.sign = sign;
        }
    }

    public static NmeaMessage createMessage(String type, String[] parts) {
        try {
            if ("GPGGA".equals(type))
                return new GPGGA(parts);
            if ("GPGSA".equals(type))
                return new GPGSA(parts);
            if ("GPGSV".equals(type))
                return IGNORED;
            if ("GPRMC".equals(type))
                return new GPRMC(parts);
            if ("GPVTG".equals(type))
                return IGNORED;
            if ("PMTK001".equals(type))
                return new PMTK001(parts);

            LOG.warn("Unhandled message type {}, parts {}", type, Arrays.toString(parts));
        }
        catch (final Exception e) {
            LOG.warn("Error handling message with type " + type + ", parts " + Arrays.toString(parts), e);
        }

        return null;
    }

    abstract public String getNmeaMessageType();

    public static double parseLatitude(String lat, String compassStr) {
        if (lat.isEmpty() && compassStr.isEmpty())
            return 0;

        return parseLatitude(lat, Compass.valueOf(compassStr));
    }

    public static double parseLatitude(String lat, Compass compass) {
        double decimal = Integer.parseInt(lat.substring(0, 2));
        decimal += Double.parseDouble(lat.substring(2)) / 60;
        return decimal * compass.sign;
    }

    public static double parseLongitude(String lng, String compassStr) {
        if (lng.isEmpty() && compassStr.isEmpty())
            return 0;

        return parseLongitude(lng, Compass.valueOf(compassStr));
    }

    public static double parseLongitude(String lng, Compass compass) {
        double decimal = Integer.parseInt(lng.substring(0, 3));
        decimal += Double.parseDouble(lng.substring(3)) / 60;
        return decimal * compass.sign;
    }

    public static int parseOptionalInt(String s, int def) {
        if (s.isEmpty())
            return def;
        return Integer.parseInt(s);
    }

    public static double parseOptionalDouble(String s, double def) {
        if (s.isEmpty())
            return def;
        return Double.parseDouble(s);
    }
}
