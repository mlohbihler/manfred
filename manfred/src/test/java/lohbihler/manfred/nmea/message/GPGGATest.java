package lohbihler.manfred.nmea.message;

import org.junit.Assert;
import org.junit.Test;

public class GPGGATest {
    @Test
    public void displacementTest() {
        double alt0 = 192;
        double lat0 = 41.756192;
        double lng0 = -87.967360;

        double alt1 = 198;
        double lat1 = 41.758701;
        double lng1 = -87.973307;

        Assert.assertEquals(566.738, GPGGA.displacementMeters(alt0, lat0, lng0, alt1, lat1, lng1), 0.0005);
    }
}
