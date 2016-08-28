package lohbihler.manfred.datalog;

public class FlightSample {
    // Accelerometer
    private int accelX;
    private int accelY;
    private int accelZ;
    private float temp;
    private int gyroX;
    private int gyroY;
    private int gyroZ;

    // Servos
    private int throttle;
    private int ailerons;
    private int elevator;
    private int rudder;

    // Ultrasonic ranger
    private int usDistance;

    // Battery level
    private int batteryLevel;

    public int getAccelX() {
        return accelX;
    }

    public void setAccelX(int accelX) {
        this.accelX = accelX;
    }

    public int getAccelY() {
        return accelY;
    }

    public void setAccelY(int accelY) {
        this.accelY = accelY;
    }

    public int getAccelZ() {
        return accelZ;
    }

    public void setAccelZ(int accelZ) {
        this.accelZ = accelZ;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getGyroX() {
        return gyroX;
    }

    public void setGyroX(int gyroX) {
        this.gyroX = gyroX;
    }

    public int getGyroY() {
        return gyroY;
    }

    public void setGyroY(int gyroY) {
        this.gyroY = gyroY;
    }

    public int getGyroZ() {
        return gyroZ;
    }

    public void setGyroZ(int gyroZ) {
        this.gyroZ = gyroZ;
    }

    public int getThrottle() {
        return throttle;
    }

    public void setThrottle(int throttle) {
        this.throttle = throttle;
    }

    public int getAilerons() {
        return ailerons;
    }

    public void setAilerons(int ailerons) {
        this.ailerons = ailerons;
    }

    public int getElevator() {
        return elevator;
    }

    public void setElevator(int elevator) {
        this.elevator = elevator;
    }

    public int getRudder() {
        return rudder;
    }

    public void setRudder(int rudder) {
        this.rudder = rudder;
    }

    public int getUsDistance() {
        return usDistance;
    }

    public void setUsDistance(int usDistance) {
        this.usDistance = usDistance;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + accelX;
        result = prime * result + accelY;
        result = prime * result + accelZ;
        result = prime * result + ailerons;
        result = prime * result + batteryLevel;
        result = prime * result + elevator;
        result = prime * result + gyroX;
        result = prime * result + gyroY;
        result = prime * result + gyroZ;
        result = prime * result + rudder;
        result = prime * result + Float.floatToIntBits(temp);
        result = prime * result + throttle;
        result = prime * result + usDistance;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final FlightSample other = (FlightSample) obj;
        if (accelX != other.accelX)
            return false;
        if (accelY != other.accelY)
            return false;
        if (accelZ != other.accelZ)
            return false;
        if (ailerons != other.ailerons)
            return false;
        if (batteryLevel != other.batteryLevel)
            return false;
        if (elevator != other.elevator)
            return false;
        if (gyroX != other.gyroX)
            return false;
        if (gyroY != other.gyroY)
            return false;
        if (gyroZ != other.gyroZ)
            return false;
        if (rudder != other.rudder)
            return false;
        if (Float.floatToIntBits(temp) != Float.floatToIntBits(other.temp))
            return false;
        if (throttle != other.throttle)
            return false;
        if (usDistance != other.usDistance)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "FlightSample [accelX=" + accelX + ", accelY=" + accelY + ", accelZ=" + accelZ + ", temp=" + temp
                + ", gyroX=" + gyroX + ", gyroY=" + gyroY + ", gyroZ=" + gyroZ + ", throttle=" + throttle
                + ", ailerons=" + ailerons + ", elevator=" + elevator + ", rudder=" + rudder + ", usDistance="
                + usDistance + ", batteryLevel=" + batteryLevel + "]";
    }
}
