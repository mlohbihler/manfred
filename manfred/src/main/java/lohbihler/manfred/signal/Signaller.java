package lohbihler.manfred.signal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class Signaller {
    private static final Logger LOG = LoggerFactory.getLogger(Signaller.class);

    public enum Signal {
        ok, warning, error;
    }

    private final Map<String, Signal> clientSignals = new HashMap<String, Signaller.Signal>(10);
    private Signal currentSignal;

    public synchronized void setSignal(String client, Signal signal) {
        LOG.debug("Client {} set a signal of {}", client, signal);

        if (client == null)
            throw new RuntimeException("client cannot be null");

        if (signal == null)
            clientSignals.remove(client);
        else
            clientSignals.put(client, signal);

        Signal maxSignal = null;
        for (final Signal sig : clientSignals.values()) {
            if (maxSignal == null || maxSignal.ordinal() < sig.ordinal()) {
                maxSignal = sig;
            }
        }

        if (currentSignal != maxSignal) {
            currentSignal = maxSignal;

            if (currentSignal == Signal.ok)
                LOG.info("Current signal is {}", currentSignal);
            else if (currentSignal == Signal.warning)
                LOG.warn("Current signal is {}", currentSignal);
            else if (currentSignal == Signal.error)
                LOG.error("Current signal is {}", currentSignal);

            showSignal(currentSignal);
        }
    }

    abstract protected void showSignal(Signal signal);

    public synchronized void alert(int time) {
        LOG.info("Showing alert for {} ms", time);
        showAlert();
        try {
            Thread.sleep(time);
        }
        catch (final InterruptedException e) {
            LOG.info("Sleep was interrupted", e);
        }
        showSignal(currentSignal);
        LOG.info("Finished showing alert");
    }

    abstract protected void showAlert();

    public synchronized Signal getCurrentSignal() {
        return currentSignal;
    }
}
