package lohbihler.manfred.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import lohbihler.atomicjson.JMap;

public class SignallerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SignallerFactory.class);

    public static Signaller createSignaller(GpioController gpio, JMap props) {
        final Signaller signaller;

        if (!props.containsKey("signaller")) {
            LOG.info("No signaller configuration. Returning null signaller");
            signaller = new NullSignaller();
        }
        else {
            final JMap signallerProps = props.get("signaller");

            if (signallerProps == null) {
                LOG.info("Creating null signaller");
                signaller = new NullSignaller();
            }
            else {
                LOG.info("Signaller configuration: {}", signallerProps);
                final String type = signallerProps.get("type");
                if ("rgy".equals(type)) {
                    LOG.info("Creating RGY signaller");
                    signaller = new RGYLedSignaller(gpio, getPin(signallerProps, "red"),
                            getPin(signallerProps, "green"), getPin(signallerProps, "yellow"));
                }
                else if ("tricolor".equals(type)) {
                    LOG.info("Creating tricolor signaller");
                    signaller = new TricolorLedSignaller(gpio, getPin(signallerProps, "red"),
                            getPin(signallerProps, "green"), getPin(signallerProps, "yellow"));
                }
                else
                    throw new RuntimeException("Unknown signaller type: " + type);
            }
        }

        return signaller;
    }

    private static Pin getPin(JMap signallerProps, String pinId) {
        return RaspiPin.getPinByName(signallerProps.get(pinId));
    }
}
