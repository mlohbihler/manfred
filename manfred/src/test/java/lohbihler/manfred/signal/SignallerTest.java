package lohbihler.manfred.signal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import lohbihler.manfred.signal.Signaller.Signal;

public class SignallerTest {
    private Signaller signaller;

    @Before
    public void before() {
        signaller = Mockito.spy(new Signaller() {
            @Override
            protected void showSignal(Signal signal) {
                // no op
            }

            @Override
            protected void showAlert() {
                // TODO Auto-generated method stub

            }
        });
    }

    @Test
    public void test() {
        signaller.setSignal("a", Signal.ok);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(1)).showSignal(Signal.ok);
        verify(signaller, times(0)).showSignal(Signal.warning);
        verify(signaller, times(0)).showSignal(Signal.error);

        signaller.setSignal("a", Signal.error);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(1)).showSignal(Signal.ok);
        verify(signaller, times(0)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);

        signaller.setSignal("b", Signal.warning);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(1)).showSignal(Signal.ok);
        verify(signaller, times(0)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);

        signaller.setSignal("b", Signal.error);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(1)).showSignal(Signal.ok);
        verify(signaller, times(0)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);

        signaller.setSignal("a", Signal.warning);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(1)).showSignal(Signal.ok);
        verify(signaller, times(0)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);

        signaller.setSignal("b", Signal.ok);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(1)).showSignal(Signal.ok);
        verify(signaller, times(1)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);

        signaller.setSignal("a", null);
        verify(signaller, times(0)).showSignal(null);
        verify(signaller, times(2)).showSignal(Signal.ok);
        verify(signaller, times(1)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);

        signaller.setSignal("b", null);
        verify(signaller, times(1)).showSignal(null);
        verify(signaller, times(2)).showSignal(Signal.ok);
        verify(signaller, times(1)).showSignal(Signal.warning);
        verify(signaller, times(1)).showSignal(Signal.error);
    }
}
