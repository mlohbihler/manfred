package lohbihler.manfred;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

/**
 * Provides a facade into the GPIO library, only creating it when required, and shutting it down when told.
 */
public class GpioFacade {
    private GpioController gpio;

    public GpioController get() {
        if (gpio == null)
            gpio = GpioFactory.getInstance();
        return gpio;
    }

    public void shutdown() {
        if (gpio != null)
            gpio.shutdown();
    }
}
