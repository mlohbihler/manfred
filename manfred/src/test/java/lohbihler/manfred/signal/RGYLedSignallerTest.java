package lohbihler.manfred.signal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import lohbihler.manfred.signal.Signaller.Signal;

public class RGYLedSignallerTest {
    private static final Pin RED_PIN = RaspiPin.GPIO_00;
    private static final Pin GREEN_PIN = RaspiPin.GPIO_01;
    private static final Pin YELLOW_PIN = RaspiPin.GPIO_02;

    private final GpioController gpioMock = mock(GpioController.class);
    private final GpioPinDigitalOutput redMock = mock(GpioPinDigitalOutput.class);
    private final GpioPinDigitalOutput greenMock = mock(GpioPinDigitalOutput.class);
    private final GpioPinDigitalOutput yellowMock = mock(GpioPinDigitalOutput.class);

    private RGYLedSignaller signaller;

    @Before
    public void before() {
        when(gpioMock.provisionDigitalOutputPin(RED_PIN, RGYLedSignaller.RED_NAME, PinState.HIGH)).thenReturn(redMock);
        when(gpioMock.provisionDigitalOutputPin(GREEN_PIN, RGYLedSignaller.GREEN_NAME, PinState.HIGH))
                .thenReturn(greenMock);
        when(gpioMock.provisionDigitalOutputPin(YELLOW_PIN, RGYLedSignaller.YELLOW_NAME, PinState.HIGH))
                .thenReturn(yellowMock);

        signaller = new RGYLedSignaller(gpioMock, RED_PIN, GREEN_PIN, YELLOW_PIN);

        // low() is called in construction, so reset.
        reset(redMock, greenMock, yellowMock);
    }

    @Test
    public void setToNull() {
        signaller.showSignal(null);
        verify(redMock, times(0)).high();
        verify(redMock, times(1)).low();
        verify(yellowMock, times(0)).high();
        verify(yellowMock, times(1)).low();
        verify(greenMock, times(0)).high();
        verify(greenMock, times(1)).low();
    }

    @Test
    public void setToOk() {
        signaller.showSignal(Signal.ok);
        verify(redMock, times(0)).high();
        verify(redMock, times(1)).low();
        verify(yellowMock, times(0)).high();
        verify(yellowMock, times(1)).low();
        verify(greenMock, times(1)).high();
        verify(greenMock, times(0)).low();
    }

    @Test
    public void setToWarning() {
        signaller.showSignal(Signal.warning);
        verify(redMock, times(0)).high();
        verify(redMock, times(1)).low();
        verify(yellowMock, times(1)).high();
        verify(yellowMock, times(0)).low();
        verify(greenMock, times(0)).high();
        verify(greenMock, times(1)).low();
    }

    @Test
    public void setToError() {
        signaller.showSignal(Signal.error);
        verify(redMock, times(1)).high();
        verify(redMock, times(0)).low();
        verify(yellowMock, times(0)).high();
        verify(yellowMock, times(1)).low();
        verify(greenMock, times(0)).high();
        verify(greenMock, times(1)).low();
    }
}
