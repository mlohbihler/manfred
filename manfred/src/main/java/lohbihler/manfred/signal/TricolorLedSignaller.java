package lohbihler.manfred.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class TricolorLedSignaller extends Signaller {
    private static final Logger LOG = LoggerFactory.getLogger(TricolorLedSignaller.class);

    private final GpioPinDigitalOutput red;
    private final GpioPinDigitalOutput green;
    private final GpioPinDigitalOutput blue;

    public TricolorLedSignaller(GpioController gpio, Pin r, Pin y, Pin g) {
        LOG.info("Setting up Tri-color LED Signaller... ");
        red = setupLed(gpio, r, "RedSignalLED");
        green = setupLed(gpio, g, "GreenSignalLED");
        blue = setupLed(gpio, y, "BlueSignalLED");
        LOG.info("Finished setting up Tri-color LED Signaller... ");
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

        // A potential improvement to this is to only turn an LED off if it is not the
        // current signal. The problem with the implementation below is that if, e.g.
        // warning is the current signal, and warning is the new signal, the blue LED
        // will blink, however briefly.

        // First set all LEDs off.
        red.low();
        green.low();
        blue.low();

        switch (signal) {
        case ok:
            green.high();
            break;
        case warning:
            red.high();
            green.high();
            break;
        case error:
            red.high();
            break;
        }
    }

    @Override
    protected void showAlert() {
        red.high();
        green.high();
        blue.high();
    }
}
