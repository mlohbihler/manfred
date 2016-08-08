package lohbihler.manfred.pi;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class PiI2CTest {
    private static final int BUS = I2CBus.BUS_1;
    private static final int MPU6050_ADDR = 0x68;
    private static final int NANO_ADDR = 0x70;

    private static final int SMPLRT_DIV = 0x19;
    private static final int CONFIG = 0x1A;
    private static final int GYRO_CONFIG = 0x1B;
    private static final int ACCEL_CONFIG = 0x1C;
    private static final int FIFO_EN = 0x23;
    private static final int INT_EN = 0x38;

    private static final int ACCEL_XOUT_H_OFF = 0;
    private static final int ACCEL_YOUT_H_OFF = 2;
    private static final int ACCEL_ZOUT_H_OFF = 4;

    private static final int TEMP_OUT_H_OFF = 6;

    private static final int GYRO_XOUT_H_OFF = 8;
    private static final int GYRO_YOUT_H_OFF = 10;
    private static final int GYRO_ZOUT_H_OFF = 12;

    private static final int PWR_MGMT_1 = 0x6B;
    private static final int PWR_MGMT_2 = 0x6C;
    private static final int WHOAMI = 0x75;

    private static final int READ_START = 0x3B; // ACCEL_XOUT_H;

    static I2CBus bus;
    static I2CDevice mpu6050;
    static I2CDevice nano;

    static byte[] mpu6050Buffer = new byte[14];
    static byte[] nanoBuffer = new byte[2];

    public static void main(String[] args) throws Exception {
        bus = I2CFactory.getInstance(BUS);
        mpu6050 = bus.getDevice(MPU6050_ADDR);
        nano = bus.getDevice(NANO_ADDR);

        // Configuration
        if (read(FIFO_EN) != 0) {
            System.out.println("Disabling FIFO.");
            write(FIFO_EN, 0);
        }

        if (read(INT_EN) != 0) {
            System.out.println("Disabling interrupt.");
            write(INT_EN, 0);
        }

        if (read(PWR_MGMT_2) != 0) {
            System.out.println("Disabling standby.");
            write(PWR_MGMT_2, 0);
        }

        if (isSet(read(PWR_MGMT_1), 6)) {
            System.out.println("Unit is sleeping. Setting to unsleep.");
            write(PWR_MGMT_1, 0);
        }

        read();
    }

    static void read() throws Exception {
        System.out.println("Sample rate divider: " + read(SMPLRT_DIV));
        System.out.println("Config: " + read(CONFIG));
        System.out.println("Gyro config: " + read(GYRO_CONFIG));
        System.out.println("Accel config: " + read(ACCEL_CONFIG));
        System.out.println("Power management 1: " + read(PWR_MGMT_1));
        System.out.println("Power management 2: " + read(PWR_MGMT_2));
        System.out.println("Who am i: " + read(WHOAMI));

        int iters = 2000;
        while (iters-- > 0) {
            int readCount = mpu6050.read(READ_START, mpu6050Buffer, 0, mpu6050Buffer.length);
            if (readCount < mpu6050Buffer.length)
                System.out.println("mpu6050Buffer not read in entirety: " + readCount);

            System.out.println("Accel:\t" //
                    + combine(mpu6050Buffer, ACCEL_XOUT_H_OFF) + "\t" //
                    + combine(mpu6050Buffer, ACCEL_YOUT_H_OFF) + "\t" //
                    + combine(mpu6050Buffer, ACCEL_ZOUT_H_OFF) + "\t" //
                    + "Gyro:\t" //
                    + combineFlatter(mpu6050Buffer, GYRO_XOUT_H_OFF) + "\t" //
                    + combineFlatter(mpu6050Buffer, GYRO_YOUT_H_OFF) + "\t" //
                    + combineFlatter(mpu6050Buffer, GYRO_ZOUT_H_OFF) + "\t" //
                    + "Temp:\t" + readTemp(mpu6050Buffer));

            //            System.out.println("Accel: " //
            //                    + combineFlatter(mpu6050Buffer, ACCEL_XOUT_H) + " " //
            //                    + combineFlatter(mpu6050Buffer, ACCEL_YOUT_H) + " " //
            //                    + combineFlatter(mpu6050Buffer, ACCEL_ZOUT_H));
            //            System.out.println("Temp: " + readTemp(mpu6050Buffer));
            //            System.out.println("Gyro: " //
            //                    + combineFlatter(mpu6050Buffer, GYRO_XOUT_H) + " " //
            //                    + combineFlatter(mpu6050Buffer, GYRO_YOUT_H) + " " //
            //                    + combineFlatter(mpu6050Buffer, GYRO_ZOUT_H));

            System.out.println("Nano:\t" + readNano() + "\t" + read(nanoBuffer, 0) + "\t" + read(nanoBuffer, 1));
            Thread.sleep(100);
        }
    }

    //    static float readAccel(byte[] buf, int start) {
    //
    //    }

    static int readNano() {
        try {
            int readCount = nano.read(0, nanoBuffer, 0, nanoBuffer.length);
            if (readCount < nanoBuffer.length)
                System.out.println("nanoBuffer not read in entirety: " + readCount);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return combine(nanoBuffer, 0);
    }

    static float readTemp(byte[] buf) {
        return combine(buf, TEMP_OUT_H_OFF) / 340F + 36.53F;
    }

    static int combineFlatter(byte[] buf, int start) {
        return combine(buf, start) / 256;
    }

    static int combine(byte[] buf, int start) {
        return (short) ((read(buf, start) << 8) | read(buf, start + 1));
    }

    static int read(byte[] buf, int regId) {
        return buf[regId] & 0xFF;
    }

    //    static float readTemp() throws IOException {
    //        return combine(read(TEMP_OUT_H), read(TEMP_OUT_L)) / 340F + 36.53F;
    //    }
    //
    //    static int combineFlatter(int high, int low) {
    //        return ((short) ((high << 8) | low)) / 256;
    //    }
    //
    //    static int combine(int high, int low) {
    //        return (short) ((high << 8) | low);
    //    }

    static int read(int regId) throws IOException {
        return mpu6050.read(regId);
    }

    static void write(int regId, int value) throws IOException {
        mpu6050.write(regId, (byte) value);
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
