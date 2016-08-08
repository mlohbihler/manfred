package lohbihler.manfred.nmea;

import java.io.IOException;

import com.serotonin.messaging.DataConsumer;
import com.serotonin.messaging.Transport;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialPortTransport implements Transport, SerialPortEventListener {
    private final SerialPort port;
    private DataConsumer consumer;

    public SerialPortTransport(SerialPort port) {
        this.port = port;
    }

    @Override
    public void setConsumer(DataConsumer consumer) throws IOException {
        this.consumer = consumer;
        try {
            port.addEventListener(this);
        }
        catch (final SerialPortException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void removeConsumer() {
        try {
            port.removeEventListener();
        }
        catch (final SerialPortException e) {
            throw new RuntimeException(e);
        }
        this.consumer = null;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (SerialPortEvent.RXCHAR == serialPortEvent.getEventType()) {
            try {
                byte[] data;
                while ((data = port.readBytes()) != null)
                    consumer.data(data, data.length);
            }
            catch (final SerialPortException e) {
                consumer.handleIOException(new IOException(e));
            }
        }
    }

    @Override
    public void write(byte[] data) throws IOException {
        try {
            port.writeBytes(data);
        }
        catch (final SerialPortException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(byte[] data, int len) throws IOException {
        byte[] buf;
        if (data.length == len)
            buf = data;
        else {
            buf = new byte[len];
            System.arraycopy(data, 0, buf, 0, len);
        }

        try {
            port.writeBytes(buf);
        }
        catch (final SerialPortException e) {
            throw new IOException(e);
        }
    }
}
