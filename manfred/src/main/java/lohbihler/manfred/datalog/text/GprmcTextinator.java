package lohbihler.manfred.datalog.text;

import lohbihler.manfred.datalog.GpsSample;
import lohbihler.manfred.nmea.message.GPRMC;

public class GprmcTextinator extends BaseSampleTextinator<GpsSample> {
    @Override
    public boolean shouldTextinate(GpsSample s) {
        if (super.shouldTextinate(s))
            return s.getGprmc() != null;
        return false;
    }

    @Override
    protected void append(StringBuilder sb, GpsSample s) {
        final GPRMC rmc = s.getGprmc();
        append(sb, 'R');
        append(sb, rmc.getTime());
        append(sb, rmc.getNavigationReceiver());
        append(sb, rmc.getLatitude());
        append(sb, rmc.getLongitude());
        append(sb, rmc.getGroundSpeed());
        append(sb, rmc.getCourse());
        append(sb, rmc.getMagneticVariation());
        append(sb, rmc.getMagneticVariationCompass());
        append(sb, rmc.getFaaMode());
    }
}
