package lohbihler.manfred.datalog.text;

import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.nmea.message.GPGGA;

public class GpggaTextinator extends BaseSampleTextinator<GpsSample> {
    @Override
    public boolean shouldTextinate(GpsSample s) {
        if (super.shouldTextinate(s))
            return s.getGpgga() != null;
        return false;
    }

    @Override
    protected void append(StringBuilder sb, GpsSample s) {
        final GPGGA gga = s.getGpgga();
        append(sb, 'G');
        append(sb, gga.getTime());
        append(sb, gga.getLatitude());
        append(sb, gga.getLongitude());
        append(sb, gga.getFixQuality());
        append(sb, gga.getNumberOfSatellites());
        append(sb, gga.getHorizontalDilution());
        append(sb, gga.getAltitudeMeters());
        append(sb, gga.getHeightOfGeoidMeters());
        append(sb, gga.getTimeSinceLastDGPSUpdate());
    }
}
