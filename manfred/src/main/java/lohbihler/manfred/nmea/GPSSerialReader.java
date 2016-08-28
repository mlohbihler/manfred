package lohbihler.manfred.nmea;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.log.IOLog;
import com.serotonin.messaging2.MessageControl;

import jssc.SerialPort;
import jssc.SerialPortException;
import lohbihler.atomicjson.JMap;
import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPGGA.FixQuality;
import lohbihler.manfred.nmea.message.GPRMC;
import lohbihler.manfred.nmea.message.GPRMC.FaaMode;
import lohbihler.manfred.nmea.message.NmeaMessage.Compass;
import lohbihler.manfred.nmea.message.PMTK;
import lohbihler.manfred.nmea.message.PMTK001;

// TODO signal in case of trouble.
public interface GPSSerialReader extends Closeable {
    public void start(GpsSample register, JMap props) throws Exception;

    public class RealGPSSerialReader implements GPSSerialReader {
        private static final Logger LOGGER = LoggerFactory.getLogger(GPSSerialReader.class);

        private SerialPort port;
        private MessageControl messageControl;
        private SerialPortTransport transport;
        private boolean running;

        @Override
        public void start(final GpsSample register, final JMap props) throws Exception {
            port = new SerialPort(props.get("port"));
            boolean success = port.openPort();
            if (!success)
                throw new RuntimeException("Failed to open serial port");

            success = port.setParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            if (!success)
                throw new RuntimeException("Failed to set serial port params");

            messageControl = new MessageControl();
            messageControl.setExceptionHandler(e -> LOGGER.error("GPS read error", e));

            final String ioLog = props.get("ioLog");
            if (!StringUtils.isEmpty(ioLog))
                messageControl.setIoLog(new IOLog(ioLog));

            transport = new SerialPortTransport(port);

            if (!running) {
                LOGGER.info("Starting GPS logger");

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
                LOGGER.warn("GPS Logger already started. Ignoring start call");
        }

        private void sendCommand(String command, String parameters) throws IOException {
            final PMTK request = new PMTK(command, parameters);
            messageControl.send(request);
            if (request.getResponse().getResponse() != PMTK001.CommandResponse.success)
                throw new IOException(
                        "Received bad response from PMTK command: " + request.getResponse().getResponse());
        }

        @Override
        public void close() {
            if (running) {
                LOGGER.info("Stopping GPS logger");
                running = false;
                messageControl.close();
            }
            else
                LOGGER.warn("GPS Logger not running. Ignoring stop call");

            // Close quietly.
            if (port != null) {
                try {
                    port.closePort();
                }
                catch (final SerialPortException e) {
                    LOGGER.warn("Error closing serial port", e);
                }
            }
        }
    }

    public class FakeyGPSSerialReader implements GPSSerialReader {
        @Override
        public void start(final GpsSample register, final JMap props) throws Exception {
            register.setGpgga(new GPGGA("time", 1.1, 2.2, FixQuality.gpsFix, 2, 3.3, 4.4, 5.5, 6, "stat"));
            register.setGprmc(new GPRMC("rtime", null, 11.1, 22.2, 33.3, 44.4, null, 55.5, Compass.E, FaaMode.M));
        }

        @Override
        public void close() throws IOException {
            // no op
        }
    }
}
