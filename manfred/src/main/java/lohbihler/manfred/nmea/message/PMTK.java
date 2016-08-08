package lohbihler.manfred.nmea.message;

import com.serotonin.messaging2.IncomingMessage;
import com.serotonin.messaging2.OutgoingMessage;
import com.serotonin.util.queue.ByteQueue;

import lohbihler.manfred.nmea.NmeaParser;

public class PMTK implements OutgoingMessage {
    private final String command;
    private final String parameters;
    private PMTK001 response;

    public PMTK(String command, String parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    @Override
    public byte[] getMessageData() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PMTK").append(command).append(",").append(parameters);
        final String message = addChecksum(sb.toString());
        return message.getBytes(NmeaParser.ASCII);
    }

    @Override
    public boolean consume(IncomingMessage incomingMessage) {
        if (incomingMessage instanceof PMTK001) {
            final PMTK001 response = (PMTK001) incomingMessage;
            if (response.getCommand().equals(command)) {
                this.response = response;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean completed() {
        return response != null;
    }

    @Override
    public void reset() {
        // no op
    }

    public PMTK001 getResponse() {
        return response;
    }

    private String addChecksum(String command) {
        final ByteQueue queue = new ByteQueue(command.getBytes(NmeaParser.ASCII));
        final int cs = NmeaParser.calculateChecksum(queue);
        String csStr = ("0" + Integer.toHexString(cs).toUpperCase());
        if (csStr.length() > 2)
            csStr = csStr.substring(1, 3);
        return "$" + command + "*" + csStr + "\r\n";
    }
}
