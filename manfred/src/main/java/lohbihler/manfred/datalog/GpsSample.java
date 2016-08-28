package lohbihler.manfred.datalog;

import lohbihler.manfred.nmea.message.GPGGA;
import lohbihler.manfred.nmea.message.GPRMC;

public class GpsSample {
    // GPGGA
    private GPGGA gpgga;

    // GPRMC
    private GPRMC gprmc;

    public GPGGA getGpgga() {
        return gpgga;
    }

    public void setGpgga(GPGGA gpgga) {
        this.gpgga = gpgga;
    }

    public GPRMC getGprmc() {
        return gprmc;
    }

    public void setGprmc(GPRMC gprmc) {
        this.gprmc = gprmc;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((gpgga == null) ? 0 : gpgga.hashCode());
        result = prime * result + ((gprmc == null) ? 0 : gprmc.hashCode());
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
        final GpsSample other = (GpsSample) obj;
        if (gpgga == null) {
            if (other.gpgga != null)
                return false;
        }
        else if (!gpgga.equals(other.gpgga))
            return false;
        if (gprmc == null) {
            if (other.gprmc != null)
                return false;
        }
        else if (!gprmc.equals(other.gprmc))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GpsSample [gpgga=" + gpgga + ", gprmc=" + gprmc + "]";
    }
}
