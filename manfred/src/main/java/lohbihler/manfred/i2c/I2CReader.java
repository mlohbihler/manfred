package lohbihler.manfred.i2c;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

import lohbihler.atomicjson.JMap;
import lohbihler.manfred.gpio.I2CFactory;
import lohbihler.manfred.signal.Signaller;
import lohbihler.manfred.signal.Signaller.Signal;
import lohbihler.manfred.tinytsdb.FlightSample;

public class I2CReader implements Runnable, Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(I2CReader.class);

    private static final int I2C_BUS = I2CBus.BUS_1;
    private static final int MPU6050_ADDR = 0x68;
    private static final int NANO_ADDR = 0x70;

    // FIFO enabled
    private static final int FIFO_EN = 0x23;
    // Interrupt enabled
    private static final int INT_EN = 0x38;
    // Power management
    private static final int PWR_MGMT_2 = 0x6C;
    // More (or less) power management
    private static final int PWR_MGMT_1 = 0x6B;

    // A bunch of other stuff
    private static final int SMPLRT_DIV = 0x19;
    private static final int CONFIG = 0x1A;
    private static final int GYRO_CONFIG = 0x1B;
    private static final int ACCEL_CONFIG = 0x1C;
    private static final int WHOAMI = 0x75;

    private static final int MPU6050_READ_START = 0x3B; // ACCEL_XOUT_H;

    private static final int ACCEL_XOUT_H_OFF = 0;
    private static final int ACCEL_YOUT_H_OFF = 2;
    private static final int ACCEL_ZOUT_H_OFF = 4;
    private static final int TEMP_OUT_H_OFF = 6;
    private static final int GYRO_XOUT_H_OFF = 8;
    private static final int GYRO_YOUT_H_OFF = 10;
    private static final int GYRO_ZOUT_H_OFF = 12;

    private static final String SIGNALLER_MPU6050 = "MPU6050";
    private static final String SIGNALLER_NANO = "Nano";

    private final FlightSample flightRegister;
    private final Signaller signaller;
    private final I2CBus bus;
    private final I2CDevice mpu6050;
    private final I2CDevice nano;
    private Thread thread;
    private boolean running;
    private boolean mpu6050Problem;
    private boolean nanoProblem;

    static byte[] mpu6050Buffer = new byte[14];
    static byte[] nanoBuffer = new byte[12];

    public I2CReader(FlightSample flightRegister, Signaller signaller, JMap props) throws Exception {
        this.flightRegister = flightRegister;
        this.signaller = signaller;

        final String i2cFactoryClazz = props.get("i2cFactory");
        final I2CFactory i2cFactory = (I2CFactory) Class.forName(i2cFactoryClazz).newInstance();
        bus = i2cFactory.get(I2C_BUS);
        mpu6050 = bus.getDevice(MPU6050_ADDR);
        nano = bus.getDevice(NANO_ADDR);
    }

    synchronized public void start() throws IOException {
        if (!running) {
            // Configuration
            if (mpu6050.read(FIFO_EN) != 0) {
                LOGGER.info("Disabling FIFO.");
                mpu6050.write(FIFO_EN, (byte) 0);
            }

            if (mpu6050.read(INT_EN) != 0) {
                LOGGER.info("Disabling interrupt.");
                mpu6050.write(INT_EN, (byte) 0);
            }

            if (mpu6050.read(PWR_MGMT_2) != 0) {
                LOGGER.info("Disabling standby.");
                mpu6050.write(PWR_MGMT_2, (byte) 0);
            }

            if (isSet(mpu6050.read(PWR_MGMT_1), 6)) {
                LOGGER.info("Unit is sleeping. Setting to unsleep.");
                mpu6050.write(PWR_MGMT_1, (byte) 0);
            }

            LOGGER.info("Sample rate divider: {}", mpu6050.read(SMPLRT_DIV));
            LOGGER.info("Config: {}", +mpu6050.read(CONFIG));
            LOGGER.info("Gyro config: {}", +mpu6050.read(GYRO_CONFIG));
            LOGGER.info("Accel config: {}", +mpu6050.read(ACCEL_CONFIG));
            LOGGER.info("Power management 1: {}", +mpu6050.read(PWR_MGMT_1));
            LOGGER.info("Power management 2: {}", +mpu6050.read(PWR_MGMT_2));
            LOGGER.info("Who am i: {}", +mpu6050.read(WHOAMI));

            running = true;
            thread = new Thread(this, "I2CReader");
            thread.start();
        }
        else
            LOGGER.warn("I2C Reader already started. Ignoring start call");
    }

    public void stop() {
        if (running) {
            LOGGER.info("Stopping I2C Reader");
            running = false;
            try {
                thread.join();
            }
            catch (final InterruptedException e) {
                LOGGER.warn("Interrupted while waiting for thread to end", e);
            }
        }
        else
            LOGGER.warn("I2C Reader not running. Ignoring stop call");
    }

    @Override
    public void close() {
        try {
            bus.close();
        }
        catch (final IOException e) {
            LOGGER.warn("Error closing I2C", e);
        }
    }

    @Override
    public void run() {
        while (running) {
            readMpu6050();
            readNano();

            try {
                // Give us a sleep now huh?
                Thread.sleep(2);
            }
            catch (final InterruptedException e) {
                LOGGER.warn("Interrupted while sleeping", e);
            }
        }
    }

    private void readMpu6050() {
        try {
            final int readCount = mpu6050.read(MPU6050_READ_START, mpu6050Buffer, 0, mpu6050Buffer.length);
            if (readCount < mpu6050Buffer.length)
                throw new IOException("mpu6050Buffer not read in entirety: " + readCount);

            flightRegister.setAccelX(combine(mpu6050Buffer, ACCEL_XOUT_H_OFF));
            flightRegister.setAccelY(combine(mpu6050Buffer, ACCEL_YOUT_H_OFF));
            flightRegister.setAccelZ(combine(mpu6050Buffer, ACCEL_ZOUT_H_OFF));
            flightRegister.setTemp(readTemp(mpu6050Buffer, TEMP_OUT_H_OFF));
            flightRegister.setGyroX(combine(mpu6050Buffer, GYRO_XOUT_H_OFF));
            flightRegister.setGyroY(combine(mpu6050Buffer, GYRO_YOUT_H_OFF));
            flightRegister.setGyroZ(combine(mpu6050Buffer, GYRO_ZOUT_H_OFF));

            if (mpu6050Problem) {
                signaller.setSignal(SIGNALLER_MPU6050, Signal.ok);
                mpu6050Problem = false;
            }
        }
        catch (final IOException e) {
            if (!mpu6050Problem) {
                mpu6050Problem = true;
                LOGGER.error("MPU 6050 read error", e);
                signaller.setSignal(SIGNALLER_MPU6050, Signal.error);
            }
        }
    }

    private void readNano() {
        try {
            final int readCount = nano.read(0, nanoBuffer, 0, nanoBuffer.length);
            if (readCount < nanoBuffer.length)
                throw new IOException("nanoBuffer not read in entirety: " + readCount);

            flightRegister.setThrottle(combine(nanoBuffer, 0));
            flightRegister.setAilerons(combine(nanoBuffer, 2));
            flightRegister.setElevator(combine(nanoBuffer, 4));
            flightRegister.setRudder(combine(nanoBuffer, 6));
            flightRegister.setUsDistance(combine(nanoBuffer, 8));
            flightRegister.setBatteryLevel(combine(nanoBuffer, 10));

            if (nanoProblem) {
                signaller.setSignal(SIGNALLER_NANO, Signal.ok);
                nanoProblem = false;
            }
        }
        catch (final IOException e) {
            if (!nanoProblem) {
                nanoProblem = true;
                LOGGER.error("Nano read error", e);
                signaller.setSignal(SIGNALLER_NANO, Signal.error);
            }
        }
    }

    private static float readTemp(byte[] buf, int start) {
        return combine(buf, start) / 340F + 36.53F;
    }

    private static int combine(byte[] buf, int start) {
        return (short) ((read(buf, start) << 8) | read(buf, start + 1));
    }

    private static int read(byte[] buf, int regId) {
        return buf[regId] & 0xFF;
    }

    static boolean isSet(int value, int bit) {
        switch (bit) {
        case 0:
            return (value & 0x1) == 0x1;
        case 1:
            return (value & 0x2) == 0x2;
        case 2:
            return (value & 0x4) == 0x4;
        case 3:
            return (value & 0x8) == 0x8;
        case 4:
            return (value & 0x10) == 0x10;
        case 5:
            return (value & 0x20) == 0x20;
        case 6:
            return (value & 0x40) == 0x40;
        case 7:
            return (value & 0x80) == 0x80;
        default:
            throw new RuntimeException("Invalid bit number: " + bit);
        }
    }
}
