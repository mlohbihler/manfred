package lohbihler.manfred.datalog.text;

import lohbihler.manfred.datalog.FlightSample;

public class FlightDataTextinator extends BaseSampleTextinator<FlightSample> {
    @Override
    protected void append(StringBuilder sb, FlightSample s) {
        append(sb, 'F');
        append(sb, s.getAccelX());
        append(sb, s.getAccelY());
        append(sb, s.getAccelZ());
        append(sb, s.getGyroX());
        append(sb, s.getGyroY());
        append(sb, s.getGyroZ());

        append(sb, s.getThrottle());
        append(sb, s.getAilerons());
        append(sb, s.getElevator());
        append(sb, s.getRudder());

        append(sb, s.getUsDistance());
    }
}
