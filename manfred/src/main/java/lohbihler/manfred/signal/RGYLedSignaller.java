package lohbihler.manfred.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class RGYLedSignaller extends Signaller {
    public static final String RED_NAME = "RedSignalLED";
    public static final String GREEN_NAME = "GreenSignalLED";
    public static final String YELLOW_NAME = "YellowSignalLED";

    private static final Logger LOG = LoggerFactory.getLogger(RGYLedSignaller.class);

    private final GpioPinDigitalOutput red;
    private final GpioPinDigitalOutput green;
    private final GpioPinDigitalOutput yellow;

    public RGYLedSignaller(GpioController gpio, Pin r, Pin g, Pin y) {
        LOG.info("Setting up RGYLedSignaller... ");
        red = setupLed(gpio, r, RED_NAME);
        green = setupLed(gpio, g, GREEN_NAME);
        yellow = setupLed(gpio, y, YELLOW_NAME);
        LOG.info("Finished setting up RGYLedSignaller");
    }

    private GpioPinDigitalOutput setupLed(GpioController gpio, Pin pin, String name) {
        final GpioPinDigitalOutput o = gpio.provisionDigitalOutputPin(pin, name, PinState.HIGH);
        o.low();
        o.setShutdownOptions(true, PinState.LOW);
        return o;
    }

    @Override
    protected void showSignal(Signal signal) {
        LOG.debug("Setting signal to {}", signal);

        if (signal == null) {
            // Turn everything off.
            red.low();
            yellow.low();
            green.low();
        }
        else if (signal == Signal.ok) {
            red.low();
            yellow.low();
            green.high();
        }
        else if (signal == Signal.warning) {
            red.low();
            yellow.high();
            green.low();
        }
        else if (signal == Signal.error) {
            red.high();
            yellow.low();
            green.low();
        }
        else {
            throw new RuntimeException("Unknown signal type: " + signal);
        }
    }

    @Override
    protected void showAlert() {
        red.high();
        yellow.high();
        green.high();
    }
}
