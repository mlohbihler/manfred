package lohbihler.manfred.gpio;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

public interface I2CFactory {
    I2CBus get(int busNumber) throws IOException;

    public static class RealI2CFactory implements I2CFactory {
        @Override
        public I2CBus get(int busNumber) throws IOException {
            return com.pi4j.io.i2c.I2CFactory.getInstance(busNumber);
        }
    }

    public static class FakeyI2CFactory implements I2CFactory {
        @Override
        public I2CBus get(int busNumber) throws IOException {
            return new FakeyI2CBus();
        }

        static class FakeyI2CBus implements I2CBus {
            @Override
            public I2CDevice getDevice(int address) throws IOException {
                return new FakeyI2CDevice(address);
            }

            @Override
            public String getFileName() {
                return null;
            }

            @Override
            public int getFileDescriptor() {
                return -1;
            }

            @Override
            public void close() throws IOException {
                // no op
            }
        }

        static class FakeyI2CDevice implements I2CDevice {
            private final int address;

            public FakeyI2CDevice(int address) {
                this.address = address;
            }

            @Override
            public void write(byte b) throws IOException {
                // Hey look, a byte!
            }

            @Override
            public void write(byte[] buffer, int offset, int size) throws IOException {
                // Hey look, a bytes!
            }

            @Override
            public void write(int address, byte b) throws IOException {
                // Hey look, a byte!
            }

            @Override
            public void write(int address, byte[] buffer, int offset, int size) throws IOException {
                // Hey look, a byte!
            }

            @Override
            public int read() throws IOException {
                return -1;
            }

            @Override
            public int read(byte[] buffer, int offset, int size) throws IOException {
                return -1;
            }

            @Override
            public int read(int address) throws IOException {
                return -1;
            }

            @Override
            public int read(int address, byte[] buffer, int offset, int size) throws IOException {
                return size;
            }

            @Override
            public int read(byte[] writeBuffer, int writeOffset, int writeSize, byte[] readBuffer, int readOffset,
                    int readSize) throws IOException {
                return -1;
            }
        }
    }
}
