package lohbihler.manfred.nmea.message;

/**
 * Recommended minimum specific GPS/Transit data
 */
public class GPRMC extends NmeaMessage {
    public static enum NavigationReceiver {
        A, // OK
        V; // Warning
    }

    public static enum FaaMode {
        A, // Automatic
        M, // Manual
        D, // DGPS
        E, // Estimated
        S, // Simulated
        N; // None
    }

    private final String time;
    private final NavigationReceiver navigationReceiver;
    private final double latitude;
    private final double longitude;
    private final double groundSpeed;
    private final double course;
    private final String date;
    private final double magneticVariation;
    private final Compass magneticVariationCompass;
    private final FaaMode faaMode;

    GPRMC(String[] parts) {
        if (parts.length != 11)
            throw new RuntimeException();

        time = parts[0];
        navigationReceiver = NavigationReceiver.valueOf(parts[1]);
        latitude = parseLatitude(parts[2], parts[3]);
        longitude = parseLongitude(parts[4], parts[5]);
        groundSpeed = parseOptionalDouble(parts[6], 0);
        course = parseOptionalDouble(parts[7], 0);
        date = parts[8];
        magneticVariation = parseOptionalDouble(parts[9], 0);
        if (parts[10].isEmpty())
            magneticVariationCompass = null;
        else
            magneticVariationCompass = Compass.valueOf(parts[10]);
        faaMode = FaaMode.valueOf(parts[11]);
    }

    public GPRMC(String time, NavigationReceiver navigationReceiver, double latitude, double longitude,
            double groundSpeed, double course, String date, double magneticVariation, Compass magneticVariationCompass,
            FaaMode faaMode) {
        this.time = time;
        this.navigationReceiver = navigationReceiver;
        this.latitude = latitude;
        this.longitude = longitude;
        this.groundSpeed = groundSpeed;
        this.course = course;
        this.date = date;
        this.magneticVariation = magneticVariation;
        this.magneticVariationCompass = magneticVariationCompass;
        this.faaMode = faaMode;
    }

    @Override
    public String getNmeaMessageType() {
        return "GPRMC";
    }

    public String getTime() {
        return time;
    }

    public NavigationReceiver getNavigationReceiver() {
        return navigationReceiver;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public double getCourse() {
        return course;
    }

    public String getDate() {
        return date;
    }

    public double getMagneticVariation() {
        return magneticVariation;
    }

    public Compass getMagneticVariationCompass() {
        return magneticVariationCompass;
    }

    public FaaMode getFaaMode() {
        return faaMode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(course);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((faaMode == null) ? 0 : faaMode.hashCode());
        temp = Double.doubleToLongBits(groundSpeed);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(magneticVariation);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((magneticVariationCompass == null) ? 0 : magneticVariationCompass.hashCode());
        result = prime * result + ((navigationReceiver == null) ? 0 : navigationReceiver.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
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
        final GPRMC other = (GPRMC) obj;
        if (Double.doubleToLongBits(course) != Double.doubleToLongBits(other.course))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        }
        else if (!date.equals(other.date))
            return false;
        if (faaMode != other.faaMode)
            return false;
        if (Double.doubleToLongBits(groundSpeed) != Double.doubleToLongBits(other.groundSpeed))
            return false;
        if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
            return false;
        if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
            return false;
        if (Double.doubleToLongBits(magneticVariation) != Double.doubleToLongBits(other.magneticVariation))
            return false;
        if (magneticVariationCompass != other.magneticVariationCompass)
            return false;
        if (navigationReceiver != other.navigationReceiver)
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        }
        else if (!time.equals(other.time))
            return false;
        return true;
    }
}
