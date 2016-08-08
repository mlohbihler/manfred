package lohbihler.manfred.pi;

import com.pi4j.gpio.extension.ads.ADS1015GpioProvider;
import com.pi4j.gpio.extension.ads.ADS1015Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import com.pi4j.io.i2c.I2CBus;

public class PiServoTest {
    public static void main(String[] args) throws Exception {
        final GpioController gpio = GpioFactory.getInstance();

        final ADS1015GpioProvider gpioProvider = new ADS1015GpioProvider(I2CBus.BUS_1,
                ADS1015GpioProvider.ADS1015_ADDRESS_0x48);

        final GpioPinAnalogInput ai1 = gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A0);
        GpioPinAnalogInput myInputs[] = {
                gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A0, "MyAnalogInput-A0"),
                gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A1, "MyAnalogInput-A1"),
                gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A2, "MyAnalogInput-A2"),
                gpio.provisionAnalogInputPin(gpioProvider, ADS1015Pin.INPUT_A3, "MyAnalogInput-A3"), };

        ai1.addListener(new GpioPinListenerAnalog() {
            @Override
            public void handleGpioPinAnalogValueChangeEvent(GpioPinAnalogValueChangeEvent event) {
                System.out.println(event.getValue());
            }
        });

        //        final GpioPinDigitalInput di1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01);
        //
        //        di1.addListener(new GpioPinListenerDigital() {
        //            long start = 0;
        //
        //            @Override
        //            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        //                // when button is pressed, speed up the blink rate on LED #2
        //                if (event.getState().isHigh()) {
        //                    start = System.nanoTime();
        //                }
        //                else {
        //                    long duration = (System.nanoTime() - start) / 1000;
        //                    System.out.println("Pulse in of " + duration + " us");
        //                }
        //            }
        //        });

        while (true) {
            Thread.sleep(500);
        }
    }
}
