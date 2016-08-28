package lohbihler.manfred.tinytsdb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tinytsdb.ByteArrayBuilder;

import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.datalog.tinytsdb.GpsSampleSerializer;
import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPRMC;
import lohbihler.manfred.nmea.message.GPRMC.NavigationReceiver;
import lohbihler.manfred.nmea.message.NmeaMessage.Compass;

public class GpsSampleSerializerTest {
    @Test
    public void test() {
        final GpsSample in = new GpsSample();
        in.setGpgga(new GPGGA("asdf", 1.1, 1.2, null, 12, 20.1, 20.2, 20.3, 1234, null));
        in.setGprmc(new GPRMC("sdfg", NavigationReceiver.A, 100.1, 101.1, 102.2, 103.3, null, 200.2, Compass.E, null));

        final GpsSampleSerializer ser = GpsSampleSerializer.get();
        final ByteArrayBuilder b = new ByteArrayBuilder();

        ser.toByteArray(b, in, 0);

        final GpsSample out = ser.fromByteArray(b, 0);

        assertEquals(in, out);
    }
}
