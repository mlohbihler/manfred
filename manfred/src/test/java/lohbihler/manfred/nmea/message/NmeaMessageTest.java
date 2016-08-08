package lohbihler.manfred.nmea.message;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import lohbihler.manfred.nmea.message.NmeaMessage.Compass;

public class NmeaMessageTest {
    @Test
    public void latitudeParseTest() {
        double dec = NmeaMessage.parseLatitude("4353.2773", Compass.N);
        assertEquals(43.887955, dec, 0.000001);
    }

    @Test
    public void longitudeParseTest() {
        double dec = NmeaMessage.parseLongitude("07917.9680", Compass.W);
        assertEquals(-79.299467, dec, 0.000001);
    }
}
