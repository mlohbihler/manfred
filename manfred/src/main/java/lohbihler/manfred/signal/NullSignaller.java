package lohbihler.manfred.signal;

public class NullSignaller extends Signaller {
    @Override
    protected void showSignal(Signal signal) {
        // no op
    }

    @Override
    protected void showAlert() {
        // no op
    }
}
