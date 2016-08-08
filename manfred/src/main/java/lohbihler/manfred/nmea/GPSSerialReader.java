package lohbihler.manfred.nmea;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.messaging2.MessageControl;

import jssc.SerialPort;
import jssc.SerialPortException;
import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPRMC;
import lohbihler.manfred.nmea.message.PMTK;
import lohbihler.manfred.nmea.message.PMTK001;
import lohbihler.manfred.tinytsdb.GpsSample;

public class GPSSerialReader implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(GPSSerialReader.class);

    private final GpsSample register;
    private final SerialPort port;
    private final MessageControl messageControl;
    private final SerialPortTransport transport;
    private boolean running;

    public GPSSerialReader(GpsSample register, String portId) throws Exception {
        this.register = register;

        port = new SerialPort(portId);
        boolean success = port.openPort();
        if (!success)
            throw new RuntimeException("Failed to open serial port");

        success = port.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        if (!success)
            throw new RuntimeException("Failed to set serial port params");

        messageControl = new MessageControl();
        transport = new SerialPortTransport(port);
    }

    public void start() throws IOException {
        if (!running) {
            LOG.info("Starting GPS logger");

            messageControl.start(transport, new NmeaParser(), request -> {
                if (request instanceof GPRMC) {
                    register.setGprmc((GPRMC) request);
                }
                else if (request instanceof GPGGA)
                    register.setGpgga((GPGGA) request);

                // The device never sends requests, so return null.
                return null;
            });

            // Update rate
            sendCommand("220", "330");

            // Position fix
            sendCommand("300", "330,0,0,0,0");

            // Messages to output. GPGGA and GPRMC only
            sendCommand("314", "0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");

            running = true;
        }
        else
            LOG.warn("GPS Logger already started. Ignoring start call");
    }

    private void sendCommand(String command, String parameters) throws IOException {
        final PMTK request = new PMTK(command, parameters);
        messageControl.send(request);
        if (request.getResponse().getResponse() != PMTK001.CommandResponse.success)
            throw new IOException("Received bad response from PMTK command: " + request.getResponse().getResponse());
    }

    public void stop() {
        if (running) {
            LOG.info("Stopping GPS logger");
            running = false;

            messageControl.close();
        }
        else
            LOG.warn("GPS Logger not running. Ignoring stop call");
    }

    @Override
    public void close() {
        // Close quietly.
        if (port != null) {
            try {
                port.closePort();
            }
            catch (final SerialPortException e) {
                LOG.warn("Error closing serial port", e);
            }
        }
    }
}
