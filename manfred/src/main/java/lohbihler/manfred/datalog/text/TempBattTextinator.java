package lohbihler.manfred.datalog.text;

import lohbihler.manfred.datalog.FlightSample;

public class TempBattTextinator extends BaseSampleTextinator<FlightSample> {
    @Override
    protected void append(StringBuilder sb, FlightSample s) {
        append(sb, 'T');
        append(sb, s.getTemp());
        append(sb, s.getBatteryLevel());
    }
}
