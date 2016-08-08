package lohbihler.manfred.nmea.message;

/**
 * GPS DOP and active satellites
 */
public class GPGSA extends NmeaMessage {
    public static enum OperationMode {
        M, // Manual
        A; // Automatic
    }

    public static enum FixMode {
        notAvailable, twoD, threeD;
    }

    private final OperationMode operationMode;
    private final FixMode fixMode;
    private final int[] satelliteIds;
    private final double pdop;
    private final double hdop;
    private final double vdop;

    GPGSA(String[] parts) {
        //        assert (parts.length == 17);

        operationMode = OperationMode.valueOf(parts[0]);
        fixMode = FixMode.values()[Integer.parseInt(parts[1]) - 1];

        int index = 13;
        while (parts[index].isEmpty())
            index--;

        satelliteIds = new int[index - 1];
        while (index > 1) {
            satelliteIds[index - 2] = Integer.parseInt(parts[index]);
            index--;
        }

        pdop = parseOptionalDouble(parts[14], 0);
        hdop = parseOptionalDouble(parts[15], 0);
        vdop = parseOptionalDouble(parts[16], 0);
    }

    @Override
    public String getNmeaMessageType() {
        return "GPGSA";
    }

    public OperationMode getOperationMode() {
        return operationMode;
    }

    public FixMode getFixMode() {
        return fixMode;
    }

    public int[] getSatelliteIds() {
        return satelliteIds;
    }

    public double getPdop() {
        return pdop;
    }

    public double getHdop() {
        return hdop;
    }

    public double getVdop() {
        return vdop;
    }
}
