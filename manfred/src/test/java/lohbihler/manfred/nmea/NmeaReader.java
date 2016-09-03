package lohbihler.manfred.nmea;

import java.io.InputStream;

import com.serotonin.util.queue.ByteQueue;

import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPRMC;
import lohbihler.manfred.nmea.message.NmeaMessage;

public class NmeaReader {
    public static void main(String[] args) throws Exception {
        final String filename = "nmea.out";
        //        final String filename = "nmea2.out";
        //        final String filename = "nmea3.out";

        final ByteQueue queue = new ByteQueue();
        final NmeaParser parser = new NmeaParser();

        try (InputStream in = NmeaReader.class.getResourceAsStream(filename)) {
            GPGGA last = null;
            while (in.available() > 0) {
                queue.push(in.read());
                final NmeaMessage msg = (NmeaMessage) parser.parseMessage(queue);
                if (msg instanceof GPGGA) {
                    GPGGA gpgga = (GPGGA) msg;
                    //                    System.out.println(
                    //                            gpgga.getLatitude() + "," + gpgga.getLongitude() + ", " + gpgga.getAltitudeMeters());
                    System.out.println("                [" + gpgga.getLatitude() + "," + gpgga.getLongitude() + "],");
                    //                    System.out.println(gpgga.getAltitudeMeters());
                    //                    if (last != null) {
                    //                        System.out.println(last.displacementMeters(gpgga));
                    //                    }

                    last = gpgga;
                }
                if (msg instanceof GPRMC) {
                    GPRMC gprmc = (GPRMC) msg;
                    //                    System.out.println(gprmc.getGroundSpeed());
                }
            }
        }

        System.out.println(queue);
    }
}
