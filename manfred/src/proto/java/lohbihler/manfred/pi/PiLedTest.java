package lohbihler.manfred.pi;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class PiLedTest {
    static GpioController gpio;

    public static void main(String[] args) throws Exception {
        gpio = GpioFactory.getInstance();

        threeLed();
        //        tricolor();

        gpio.shutdown();
    }

    static void threeLed() throws Exception {
        final GpioPinDigitalOutput r = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "RLED", PinState.HIGH);
        r.low();
        final GpioPinDigitalOutput b = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "BLED", PinState.HIGH);
        b.low();
        final GpioPinDigitalOutput g = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "GLED", PinState.HIGH);
        g.low();

        r.setShutdownOptions(true, PinState.LOW);
        b.setShutdownOptions(true, PinState.LOW);
        g.setShutdownOptions(true, PinState.LOW);

        // Red
        r.high();
        Thread.sleep(1000);
        r.low();

        // Green
        b.high();
        Thread.sleep(1000);
        b.low();

        // Blue
        g.high();
        Thread.sleep(1000);
        g.low();

        //        r.high();
        //        b.high();
        //        Thread.sleep(2000);
        //        r.low();
        //        b.low();
        //
        //        r.high();
        //        g.high();
        //        Thread.sleep(2000);
        //        r.low();
        //        g.low();

        g.high();
        b.high();
        Thread.sleep(2000);
        g.low();
        b.low();

        //        r.high();
        //        g.high();
        //        b.high();
        //        Thread.sleep(2000);
        //        r.low();
        //        g.low();
        //        b.low();
    }

    static void tricolor() throws Exception {
        final GpioPinDigitalOutput r = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "RLED", PinState.HIGH);
        r.low();
        final GpioPinDigitalOutput b = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "BLED", PinState.HIGH);
        b.low();
        final GpioPinDigitalOutput g = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "GLED", PinState.HIGH);
        g.low();

        r.setShutdownOptions(true, PinState.LOW);
        b.setShutdownOptions(true, PinState.LOW);
        g.setShutdownOptions(true, PinState.LOW);

        // Red
        r.high();
        Thread.sleep(1000);
        r.low();

        // Green
        b.high();
        Thread.sleep(1000);
        b.low();

        // Blue
        g.high();
        Thread.sleep(1000);
        g.low();

        //        r.high();
        //        b.high();
        //        Thread.sleep(2000);
        //        r.low();
        //        b.low();
        //
        //        r.high();
        //        g.high();
        //        Thread.sleep(2000);
        //        r.low();
        //        g.low();

        g.high();
        b.high();
        Thread.sleep(2000);
        g.low();
        b.low();

        //        r.high();
        //        g.high();
        //        b.high();
        //        Thread.sleep(2000);
        //        r.low();
        //        g.low();
        //        b.low();
    }
}
