package lohbihler.manfred.nmea;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.serotonin.util.queue.ByteQueue;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPGGA.FixQuality;
import lohbihler.manfred.nmea.message.GPGSA;
import lohbihler.manfred.nmea.message.GPGSA.FixMode;
import lohbihler.manfred.nmea.message.GPGSA.OperationMode;
import lohbihler.manfred.nmea.message.GPRMC;
import lohbihler.manfred.nmea.message.GPRMC.FaaMode;
import lohbihler.manfred.nmea.message.GPRMC.NavigationReceiver;
import lohbihler.manfred.nmea.message.NmeaMessage;
import lohbihler.manfred.nmea.message.NmeaMessage.Compass;
import lohbihler.manfred.nmea.message.NmeaMessage.IgnoredNmeaMessage;

@RunWith(JUnitParamsRunner.class)
public class NmeaParserTest {
    private final NmeaParser parser = new NmeaParser();

    @Test
    @Parameters(method = "gpggaParameters")
    public void gpggaTest(String msg, String time, double lat, double latTolerance, double lng, double lngTolerance,
            FixQuality fixQuality, int numberOfSatellites, double horizontalDilution,
            double horizontalDilutionTolerance, double altitude, double altitudeTolerance, double heightOfGeoidMeters,
            double heightOfGeoidMetersTolerance, int timeSinceLastDGPSUpdate, String dgpsReferenceStationId)
            throws Exception {
        final ByteQueue queue = new ByteQueue();
        addMessage(queue, msg);
        final GPGGA gpgga = (GPGGA) parser.parseMessage(queue);
        assertNotNull(gpgga);
        assertEquals(time, gpgga.getTime());
        assertEquals(lat, gpgga.getLatitude(), latTolerance);
        assertEquals(lng, gpgga.getLongitude(), lngTolerance);
        assertEquals(fixQuality, gpgga.getFixQuality());
        assertEquals(numberOfSatellites, gpgga.getNumberOfSatellites());
        assertEquals(horizontalDilution, gpgga.getHorizontalDilution(), horizontalDilutionTolerance);
        assertEquals(altitude, gpgga.getAltitudeMeters(), altitudeTolerance);
        assertEquals(heightOfGeoidMeters, gpgga.getHeightOfGeoidMeters(), heightOfGeoidMetersTolerance);
        assertEquals(timeSinceLastDGPSUpdate, gpgga.getTimeSinceLastDGPSUpdate());
        assertEquals(dgpsReferenceStationId, gpgga.getDgpsReferenceStationId());
        assertEquals(queue.size(), 0);
    }

    Object[] gpggaParameters() {
        return new Object[] {
                new Object[] { "$GPGGA,225643.084,,,,,0,03,,,M,,M,,*73", "225643.084", 0, 0, 0, 0, FixQuality.invalid,
                        3, 0, 0, 0, 0, 0, 0, -1, "" }, //
                new Object[] { "$GPGGA,225646.000,4353.2773,N,07917.9680,W,1,03,2.23,185.3,M,-35.3,M,,*57",
                        "225646.000", 43.887955, 0.000001, -79.299467, 0.000001, FixQuality.gpsFix, 3, 2.23, 0, 185.3,
                        0, -35.3, 0, -1, "" } //
        };
    }

    @Test
    @Parameters(method = "gpgsaParameters")
    public void gpgsaTest(String msg, OperationMode operationMode, FixMode fixMode, int[] satelliteIds, double pdop,
            double pdopTolerance, double hdop, double hdopTolerance, double vdop, double vdopTolerance)
            throws Exception {
        final ByteQueue queue = new ByteQueue();
        addMessage(queue, msg);
        final GPGSA gpgsa = (GPGSA) parser.parseMessage(queue);
        assertNotNull(gpgsa);
        assertEquals(operationMode, gpgsa.getOperationMode());
        assertEquals(fixMode, gpgsa.getFixMode());
        assertArrayEquals(satelliteIds, gpgsa.getSatelliteIds());
        assertEquals(pdop, gpgsa.getPdop(), pdopTolerance);
        assertEquals(hdop, gpgsa.getHdop(), hdopTolerance);
        assertEquals(vdop, gpgsa.getVdop(), vdopTolerance);
        assertEquals(queue.size(), 0);
    }

    Object[] gpgsaParameters() {
        return new Object[] {
                new Object[] { "$GPGSA,A,1,,,,,,,,,,,,,,,*1E", OperationMode.A, FixMode.notAvailable, new int[0], 0, 0,
                        0, 0, 0, 0 }, //
                new Object[] { "$GPGSA,A,2,01,08,30,,,,,,,,,,2.45,2.23,1.00*08", OperationMode.A, FixMode.twoD,
                        new int[] { 1, 8, 30 }, 2.45, 0, 2.23, 0, 1, 0 }, //
                new Object[] { "$GPGSA,A,3,01,08,30,31,32,33,34,35,36,37,38,39,2.45,2.23,1.00*0B", OperationMode.A,
                        FixMode.threeD, new int[] { 1, 8, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39 }, 2.45, 0, 2.23, 0, 1,
                        0 } //
        };
    }

    @Test
    @Parameters(method = "gprmcParameters")
    public void gprmcTest(String msg, String time, NavigationReceiver navigationReceiver, double lat,
            double latTolerance, double lng, double lngTolerance, double groundSpeed, double groundSpeedTolerance,
            double course, double courseTolerance, String date, double magneticVariation,
            double magneticVariationTolerance, Compass magneticVariationCompass, FaaMode faaMode) throws Exception {
        final ByteQueue queue = new ByteQueue();
        addMessage(queue, msg);
        final GPRMC gprmc = (GPRMC) parser.parseMessage(queue);
        assertNotNull(gprmc);
        assertEquals(time, gprmc.getTime());
        assertEquals(navigationReceiver, gprmc.getNavigationReceiver());
        assertEquals(lat, gprmc.getLatitude(), latTolerance);
        assertEquals(lng, gprmc.getLongitude(), lngTolerance);
        assertEquals(groundSpeed, gprmc.getGroundSpeed(), groundSpeedTolerance);
        assertEquals(course, gprmc.getCourse(), courseTolerance);
        assertEquals(date, gprmc.getDate());
        assertEquals(magneticVariation, gprmc.getMagneticVariation(), magneticVariationTolerance);
        assertEquals(magneticVariationCompass, gprmc.getMagneticVariationCompass());
        assertEquals(faaMode, gprmc.getFaaMode());
        assertEquals(queue.size(), 0);
    }

    Object[] gprmcParameters() {
        return new Object[] {
                new Object[] { "$GPRMC,225642.085,V,,,,,0.00,0.00,140416,,,N*43", "225642.085", NavigationReceiver.V, 0,
                        0, 0, 0, 0, 0, 0, 0, "140416", 0, 0, null, FaaMode.N }, //
                new Object[] { "$GPRMC,225643.084,V,,,,,0.43,177.92,140416,,,N*4E", "225643.084", NavigationReceiver.V,
                        0, 0, 0, 0, 0.43, 0, 177.92, 0, "140416", 0, 0, null, FaaMode.N }, //
                new Object[] { "$GPRMC,225643.304,V,,,,,0.28,96.31,140416,,,N*7F", "225643.304", NavigationReceiver.V,
                        0, 0, 0, 0, 0.28, 0, 96.31, 0, "140416", 0, 0, null, FaaMode.N }, //
                new Object[] { "$GPRMC,225645.000,V,,,,,0.24,176.76,140416,,,N*4E", "225645.000", NavigationReceiver.V,
                        0, 0, 0, 0, 0.24, 0, 176.76, 0, "140416", 0, 0, null, FaaMode.N }, //
                new Object[] { "$GPRMC,225646.000,A,4353.2773,N,07917.9680,W,0.16,318.85,140416,,,A*74", "225646.000",
                        NavigationReceiver.A, 43.887955, 0.000001, -79.299466, 0.000001, 0.16, 0, 318.85, 0, "140416",
                        0, 0, null, FaaMode.A }, //
                new Object[] { "$GPRMC,225647.000,A,4353.2772,N,07917.9685,W,0.35,316.25,140416,,,A*74", "225647.000",
                        NavigationReceiver.A, 43.887953, 0.000001, -79.299475, 0.000001, 0.35, 0, 316.25, 0, "140416",
                        0, 0, null, FaaMode.A }, //
                new Object[] { "$GPRMC,225648.000,A,4353.2772,N,07917.9685,W,0.14,278.17,140416,020.3,E,A*1A",
                        "225648.000", NavigationReceiver.A, 43.887953, 0.000001, -79.299475, 0.000001, 0.14, 0, 278.17,
                        0, "140416", 20.3, 0, Compass.E, FaaMode.A }, //
        };
    }

    private void addMessage(ByteQueue queue, String message) {
        queue.push(message.getBytes(NmeaParser.ASCII));
        queue.push('\r');
        queue.push('\n');
    }

    @Test
    public void badInputTest() throws Exception {
        String input = "G,2523.28,0.93*0B\r\n";
        input += "$GPRMC,234227.000,A,4353.2955,N,07918.0004,W,1.1A,234227.000,4353.2955,N,07918.0004,W,1,05,3.28,195.9,M,-35.3,M,,*55\r\n";
        input += "$GPRMC,234226.000,A,4353.2961,N,07918.0007,W,0.13,236.66,140416,,,A*71\r\n";
        input += "$GPGV,4,2,1,178,17,08,18,0";

        final ByteQueue queue = new ByteQueue(input.getBytes(NmeaParser.ASCII));
        assertEquals(235, queue.size());
        NmeaMessage msg = (NmeaMessage) parser.parseMessage(queue);
        assertNull(msg);
        msg = (NmeaMessage) parser.parseMessage(queue);
        assertEquals(msg.getClass(), GPRMC.class);
        msg = (NmeaMessage) parser.parseMessage(queue);
        assertNull(msg);
        msg = (NmeaMessage) parser.parseMessage(queue);
        assertNull(msg);
    }

    // TODO find out why it takes around 9s to run 100 times.
    @Test
    public void runRandomDataReadTestMultipleTimes() throws Exception {
        final int iterations = 100;
        for (int i = 0; i < iterations; i++)
            randomDataReadTest();
    }

    public void randomDataReadTest() throws Exception {
        // Read a random number of bytes from the test file and provide to the parser. Remember the count of
        // messages successfully parsed, and assert that count when done.

        final Random random = new Random();
        final byte[] input = new byte[200];
        int gpggaCount = 0;
        int gpgsaCount = 0;
        int gprmcCount = 0;
        int ignoredCount = 0;
        final ByteQueue queue = new ByteQueue();
        NmeaMessage msg;

        try (InputStream in = getClass().getResourceAsStream("nmea.out")) {
            while (in.available() > 0) {
                int readCount = random.nextInt(input.length);
                readCount = in.read(input, 0, readCount);
                queue.push(input, 0, readCount);

                while ((msg = (NmeaMessage) parser.parseMessage(queue)) != null) {
                    if (msg instanceof GPGGA)
                        gpggaCount++;
                    else if (msg instanceof GPGSA)
                        gpgsaCount++;
                    else if (msg instanceof GPRMC)
                        gprmcCount++;
                    else if (msg instanceof IgnoredNmeaMessage)
                        ignoredCount++;
                }
            }
        }

        assertEquals(2896, gpggaCount);
        assertEquals(2896, gpgsaCount);
        assertEquals(2896, gprmcCount);
        assertEquals(4800, ignoredCount);
    }
}
