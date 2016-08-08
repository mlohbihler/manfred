package lohbihler.manfred.tinytsdb;

import org.tinytsdb.ByteArrayBuilder;
import org.tinytsdb.Serializer;

import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPGGA.FixQuality;
import lohbihler.manfred.nmea.message.GPRMC;
import lohbihler.manfred.nmea.message.GPRMC.FaaMode;
import lohbihler.manfred.nmea.message.GPRMC.NavigationReceiver;
import lohbihler.manfred.nmea.message.NmeaMessage.Compass;

public class GpsSampleSerializer extends Serializer<GpsSample> {
    private static final GpsSampleSerializer instance = new GpsSampleSerializer();

    public static GpsSampleSerializer get() {
        return instance;
    }

    @Override
    public void toByteArray(ByteArrayBuilder b, GpsSample s, long ts) {
        final GPGGA gga = s.getGpgga();
        if (gga == null) {
            b.putBoolean(false);
        }
        else {
            b.putBoolean(true);
            b.putString(gga.getTime());
            b.putDouble(gga.getLatitude());
            b.putDouble(gga.getLongitude());
            putEnum(b, gga.getFixQuality());
            b.put(gga.getNumberOfSatellites());
            b.putDouble(gga.getHorizontalDilution());
            b.putDouble(gga.getAltitudeMeters());
            b.putDouble(gga.getHeightOfGeoidMeters());
            b.putInt(gga.getTimeSinceLastDGPSUpdate());
        }

        final GPRMC rmc = s.getGprmc();
        if (rmc == null) {
            b.putBoolean(false);
        }
        else {
            b.putBoolean(true);
            b.putString(rmc.getTime());
            putEnum(b, rmc.getNavigationReceiver());
            b.putDouble(rmc.getLatitude());
            b.putDouble(rmc.getLongitude());
            b.putDouble(rmc.getGroundSpeed());
            b.putDouble(rmc.getCourse());
            b.putDouble(rmc.getMagneticVariation());
            putEnum(b, rmc.getMagneticVariationCompass());
            putEnum(b, rmc.getFaaMode());
        }
    }

    private void putEnum(ByteArrayBuilder b, Enum<?> e) {
        b.putString(e == null ? null : e.name());
    }

    @Override
    public GpsSample fromByteArray(ByteArrayBuilder b, long ts) {
        final GpsSample s = new GpsSample();

        GPGGA gga = null;
        if (b.getBoolean()) {
            gga = new GPGGA(b.getString(), b.getDouble(), b.getDouble(), getEnum(b, FixQuality.class), b.get(),
                    b.getDouble(), b.getDouble(), b.getDouble(), b.getInt(), null);
        }
        s.setGpgga(gga);

        GPRMC rmc = null;
        if (b.getBoolean()) {
            rmc = new GPRMC(b.getString(), getEnum(b, NavigationReceiver.class), b.getDouble(), b.getDouble(),
                    b.getDouble(), b.getDouble(), null, b.getDouble(), getEnum(b, Compass.class),
                    getEnum(b, FaaMode.class));
        }
        s.setGprmc(rmc);

        return s;
    }

    private <T extends Enum<T>> T getEnum(ByteArrayBuilder b, Class<T> enumType) {
        final String s = b.getString();
        if (s == null)
            return null;
        return Enum.valueOf(enumType, s);
    }
}
