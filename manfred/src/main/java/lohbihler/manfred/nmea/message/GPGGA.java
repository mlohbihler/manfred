package lohbihler.manfred.nmea.message;

import static java.lang.StrictMath.PI;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;

/**
 * Global Positioning System Fix Data
 *
 * @author Matthew
 */
public class GPGGA extends NmeaMessage {
    private static final double EARTH_RADIUS_METERS = 6371000;
    private static final double HALF_PI = PI / 2;

    public static enum FixQuality {
        invalid, gpsFix, dgpsFix, ppsFix, realTimeKinematic, floatRTK, estimated, manualInputMode, simulationMode;
    }

    private static final String METERS = "M";

    private final String time;
    private final double latitude;
    private final double longitude;
    private final FixQuality fixQuality;
    private final int numberOfSatellites;
    private final double horizontalDilution;
    private final double altitudeMeters;
    private final double heightOfGeoidMeters;
    private final int timeSinceLastDGPSUpdate;
    private final String dgpsReferenceStationId;

    GPGGA(String[] parts) {
        if (parts.length != 14)
            throw new RuntimeException();

        time = parts[0];
        latitude = parseLatitude(parts[1], parts[2]);
        longitude = parseLongitude(parts[3], parts[4]);
        fixQuality = FixQuality.values()[Integer.parseInt(parts[5])];
        numberOfSatellites = Integer.parseInt(parts[6]);
        horizontalDilution = parseOptionalDouble(parts[7], 0);

        if (!parts[9].equals(METERS))
            throw new RuntimeException();
        altitudeMeters = parseOptionalDouble(parts[8], 0);

        if (!parts[11].equals(METERS))
            throw new RuntimeException();
        heightOfGeoidMeters = parseOptionalDouble(parts[10], 0);

        timeSinceLastDGPSUpdate = parseOptionalInt(parts[12], -1);
        dgpsReferenceStationId = parts[13];
    }

    public GPGGA(String time, double latitude, double longitude, FixQuality fixQuality, int numberOfSatellites,
            double horizontalDilution, double altitudeMeters, double heightOfGeoidMeters, int timeSinceLastDGPSUpdate,
            String dgpsReferenceStationId) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fixQuality = fixQuality;
        this.numberOfSatellites = numberOfSatellites;
        this.horizontalDilution = horizontalDilution;
        this.altitudeMeters = altitudeMeters;
        this.heightOfGeoidMeters = heightOfGeoidMeters;
        this.timeSinceLastDGPSUpdate = timeSinceLastDGPSUpdate;
        this.dgpsReferenceStationId = dgpsReferenceStationId;
    }

    @Override
    public String getNmeaMessageType() {
        return "GPGGA";
    }

    public String getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public FixQuality getFixQuality() {
        return fixQuality;
    }

    public int getNumberOfSatellites() {
        return numberOfSatellites;
    }

    public double getHorizontalDilution() {
        return horizontalDilution;
    }

    public double getAltitudeMeters() {
        return altitudeMeters;
    }

    public double getHeightOfGeoidMeters() {
        return heightOfGeoidMeters;
    }

    public int getTimeSinceLastDGPSUpdate() {
        return timeSinceLastDGPSUpdate;
    }

    public String getDgpsReferenceStationId() {
        return dgpsReferenceStationId;
    }

    /**
     * Calculate the displacement between two GGA readings.
     *
     * @param that
     *            the other GGA reading
     * @return the displacement in meters.
     */
    public double displacementMeters(GPGGA that) {
        return displacementMeters(getAltitudeMeters(), getLatitude(), getLongitude(), that.getAltitudeMeters(),
                that.getLatitude(), that.getLongitude());
    }

    /**
     * http://answers.google.com/answers/threadview?id=326655
     *
     * @param alt0
     *            altitude 0 in meters above sea level
     * @param lat0
     *            latitude 0 in degrees
     * @param lng0
     *            longitude 0 in degrees
     * @param alt1
     *            altitude 1 in meters above sea level
     * @param lat1
     *            latitude 1 in degrees
     * @param lng1
     *            longitude 1 in degrees
     *
     * @return the displacement in meters.
     */
    public static double displacementMeters(double alt0, double lat0, double lng0, double alt1, double lat1,
            double lng1) {
        final double r0 = EARTH_RADIUS_METERS + alt0;
        lng0 *= PI / 180;
        lat0 *= PI / 180;
        final double x0 = r0 * cos(lng0) * sin(HALF_PI - lat0);
        final double y0 = r0 * sin(lng0) * sin(HALF_PI - lat0);
        final double z0 = r0 * cos(HALF_PI - lat0);

        lng1 *= PI / 180;
        lat1 *= PI / 180;
        final double r1 = EARTH_RADIUS_METERS + alt0;
        final double x1 = r1 * cos(lng1) * sin(HALF_PI - lat1);
        final double y1 = r1 * sin(lng1) * sin(HALF_PI - lat1);
        final double z1 = r1 * cos(HALF_PI - lat1);

        final double dsq = (x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0) + (z1 - z0) * (z1 - z0);
        return sqrt(dsq);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(altitudeMeters);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((dgpsReferenceStationId == null) ? 0 : dgpsReferenceStationId.hashCode());
        result = prime * result + ((fixQuality == null) ? 0 : fixQuality.hashCode());
        temp = Double.doubleToLongBits(heightOfGeoidMeters);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(horizontalDilution);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + numberOfSatellites;
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + timeSinceLastDGPSUpdate;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GPGGA other = (GPGGA) obj;
        if (Double.doubleToLongBits(altitudeMeters) != Double.doubleToLongBits(other.altitudeMeters))
            return false;
        if (dgpsReferenceStationId == null) {
            if (other.dgpsReferenceStationId != null)
                return false;
        }
        else if (!dgpsReferenceStationId.equals(other.dgpsReferenceStationId))
            return false;
        if (fixQuality != other.fixQuality)
            return false;
        if (Double.doubleToLongBits(heightOfGeoidMeters) != Double.doubleToLongBits(other.heightOfGeoidMeters))
            return false;
        if (Double.doubleToLongBits(horizontalDilution) != Double.doubleToLongBits(other.horizontalDilution))
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (numberOfSatellites != other.numberOfSatellites)
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        }
        else if (!time.equals(other.time))
            return false;
        if (timeSinceLastDGPSUpdate != other.timeSinceLastDGPSUpdate)
            return false;
        return true;
    }
}
