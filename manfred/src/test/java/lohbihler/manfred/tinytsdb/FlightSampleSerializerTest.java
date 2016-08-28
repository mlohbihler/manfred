package lohbihler.manfred.tinytsdb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tinytsdb.ByteArrayBuilder;

import lohbihler.manfred.datalog.FlightSample;
import lohbihler.manfred.datalog.tinytsdb.FlightSampleSerializer;

public class FlightSampleSerializerTest {
    @Test
    public void test() {
        final FlightSample in = new FlightSample();
        in.setAccelX(1);
        in.setAccelX(2);
        in.setAccelX(3);
        in.setTemp(4);
        in.setGyroX(-1);
        in.setGyroY(-2);
        in.setGyroZ(-3);
        in.setThrottle(10);
        in.setAilerons(11);
        in.setElevator(12);
        in.setRudder(13);
        in.setUsDistance(20);
        in.setBatteryLevel(21);

        final FlightSampleSerializer ser = FlightSampleSerializer.get();
        final ByteArrayBuilder b = new ByteArrayBuilder();

        ser.toByteArray(b, in, 0);

        final FlightSample out = ser.fromByteArray(b, 0);

        assertEquals(in, out);
    }
}
