package lohbihler.manfred.nmea;

import org.apache.commons.lang3.StringUtils;

import com.serotonin.util.queue.ByteQueue;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SimpleLogger {
    public static void main(String[] args) throws Exception {
        final SerialPort port = new SerialPort("COM3");
        boolean success = port.openPort();
        if (!success)
            throw new RuntimeException("Failed to open serial port");

        success = port.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (!success)
            throw new RuntimeException("Failed to set serial port params");

        // Update rate. 330 appears to be about the minimum this can be.
        writeCommand(port, "PMTK220,330", "PMTK001,220,3");

        // Position fix. 330 appears to be about the minimum this can be.
        writeCommand(port, "PMTK300,330,0,0,0,0", "PMTK001,300,3");

        // Messages to output. GPRMC and GPGGA only
        writeCommand(port, "PMTK314,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", "PMTK001,314,3");

        //        try (PrintWriter out = new PrintWriter("nmea.out")) {
        //            while (true) {
        //                String s = port.readString();
        //                if (s != null)
        //                    out.write(s);
        //            }
        //        }

        while (true) {
            final String s = port.readString();
            if (s != null)
                System.out.print(s);
            Thread.sleep(1);
        }
    }

    public static String addChecksum(String command) {
        final ByteQueue queue = new ByteQueue(command.getBytes(NmeaParser.ASCII));
        final int cs = NmeaParser.calculateChecksum(queue);
        String csStr = ("0" + Integer.toHexString(cs).toUpperCase());
        if (csStr.length() > 2)
            csStr = csStr.substring(1, 3);
        return "$" + command + "*" + csStr + "\r\n";
    }

    private static void writeCommand(SerialPort port, String command, String expectedResponse) throws Exception {
        // Add the checksum
        System.out.println("Write command: " + command);
        command = addChecksum(command);
        port.writeString(command);

        expectedResponse = addChecksum(expectedResponse);
        String actualResponse = null;
        while ((actualResponse = readLine(port, 1000)) != null) {
            if (StringUtils.contains(actualResponse, expectedResponse)) {
                System.out.println("Yay, got the response!");
                return;
            }
            System.out.println("Got string: " + actualResponse);
        }

        throw new Exception("GPS configuration failure. Expected response '" + expectedResponse + "'");
    }

    /**
     * TODO this doesn't work because the response we want may have been partially loaded and only a part of
     * it is at the end of the buffer, and so will be missed. The buffer has to be read properly line by line.
     *
     * Read a line from the port, blocking until it is read or we timeout.
     * NOTE: this method will happily read more than it needs from the input
     * stream. It should only be used synchronously to write a command, read
     * a response, repeat. Don't write multiple commands and expect this to
     * read the responses properly.
     *
     * @throws SerialPortException
     */
    private static String readLine(SerialPort port, int timeout) throws SerialPortException {
        final long deadline = System.currentTimeMillis() + timeout;

        final StringBuilder sb = new StringBuilder(100);
        String s;
        while (true) {
            s = port.readString();
            if (s != null) {
                sb.append(s);

                final int pos = sb.indexOf("\r\n");
                if (pos != -1) {
                    //                    sb.setLength(pos);
                    return sb.toString();
                }
            }
            else {
                try {
                    Thread.sleep(20);
                }
                catch (final InterruptedException e) {
                    break;
                }
            }

            if (deadline < System.currentTimeMillis())
                break;
            else
                System.out.println("Left until timeout: " + (System.currentTimeMillis() - deadline));
        }

        return null;
    }
}
