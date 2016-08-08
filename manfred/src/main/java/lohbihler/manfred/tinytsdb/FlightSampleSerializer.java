package lohbihler.manfred.tinytsdb;

import org.tinytsdb.ByteArrayBuilder;
import org.tinytsdb.Serializer;

public class FlightSampleSerializer extends Serializer<FlightSample> {
    private static final FlightSampleSerializer instance = new FlightSampleSerializer();

    public static FlightSampleSerializer get() {
        return instance;
    }

    @Override
    public void toByteArray(ByteArrayBuilder b, FlightSample s, long ts) {
        b.putShort((short) s.getAccelX());
        b.putShort((short) s.getAccelY());
        b.putShort((short) s.getAccelZ());
        b.putShort((short) s.getTemp());
        b.putShort((short) s.getGyroX());
        b.putShort((short) s.getGyroY());
        b.putShort((short) s.getGyroZ());

        b.putShort((short) s.getThrottle());
        b.putShort((short) s.getAilerons());
        b.putShort((short) s.getElevator());
        b.putShort((short) s.getRudder());

        b.putShort((short) s.getUsDistance());

        b.putShort((short) s.getBatteryLevel());
    }

    @Override
    public FlightSample fromByteArray(ByteArrayBuilder b, long ts) {
        final FlightSample s = new FlightSample();

        s.setAccelX(b.getShort());
        s.setAccelY(b.getShort());
        s.setAccelZ(b.getShort());
        s.setTemp(b.getShort());
        s.setGyroX(b.getShort());
        s.setGyroY(b.getShort());
        s.setGyroZ(b.getShort());

        s.setThrottle(b.getShort());
        s.setAilerons(b.getShort());
        s.setElevator(b.getShort());
        s.setRudder(b.getShort());

        s.setUsDistance(b.getShort());

        s.setBatteryLevel(b.getShort());

        return s;
    }
}
