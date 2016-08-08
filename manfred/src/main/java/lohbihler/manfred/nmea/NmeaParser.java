package lohbihler.manfred.nmea;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.messaging2.IncomingMessage;
import com.serotonin.messaging2.MessageParseException;
import com.serotonin.messaging2.MessageParser;
import com.serotonin.util.queue.ByteQueue;

import lohbihler.manfred.nmea.message.NmeaMessage;

public class NmeaParser implements MessageParser {
    static final Logger LOG = LoggerFactory.getLogger(NmeaParser.class);

    private static final byte START = '$';
    private static final byte END = '\r';
    private static final byte END2 = '\n';
    private static final byte CHECKSUM_START = '*';
    private static final byte DELIMITER = ',';
    public static final Charset ASCII = Charset.forName("ASCII");

    // Reusable fields for parsing. Single-threaded use only.
    private final ByteQueue messageBuffer = new ByteQueue();
    private int pos;
    private int bytesDiscarded;
    private int cs;
    private final byte[] buffer = new byte[5];
    private String csStr;
    private int calculated;
    private int given;
    private String messageStr;

    @Override
    public IncomingMessage parseMessage(ByteQueue queue) throws MessageParseException {
        final ByteQueue copy = (ByteQueue) queue.clone();

        // Detect the start indicator
        pos = queue.indexOf(START);

        if (pos == -1)
            // Start not found. Abort.
            return null;

        if (pos > 0) {
            // Discard everything before the start indicator
            bytesDiscarded = queue.pop(pos);
            if (bytesDiscarded != pos)
                LOG.warn("Unexpected number of byte discarded: expected {}, actual {}", pos, bytesDiscarded);
        }

        // Parse a whole message using the end indicator
        pos = queue.indexOf(END);
        if (pos == -1)
            // End not found. Abort.
            return null;

        // Discard the start indicator
        queue.pop();

        // Find the checksum start indicator
        cs = queue.indexOf(CHECKSUM_START);
        if (cs == -1 || cs >= pos) {
            LOG.warn("No checksum indicator found in message: {}", queueToString(queue));
            return null;
        }

        // Transfer all but the checksum to the local buffer
        messageBuffer.clear();
        messageBuffer.push(queue, cs);

        // Discard the checksum start indicator
        queue.pop();

        // Extract the checksum value and convert to String.
        try {
            queue.pop(buffer, 0, pos - cs - 2);
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            final String s = "Failed to extract checksum from " + queueToString(queue) + ", pos=" + pos + ", cs=" + cs
                    + ", originalQueue=" + queueToString(copy);
            throw new RuntimeException(s, e);
        }
        csStr = new String(buffer, 0, pos - cs - 2, ASCII);

        // Discard the end indicator
        queue.pop();
        if (queue.size() > 0 && queue.peek(0) == END2)
            // Discard the other end indicator
            queue.pop();

        // Validate the checksum
        calculated = calculateChecksum(messageBuffer);
        given = Integer.parseInt(csStr, 16);
        if (calculated != given) {
            LOG.warn("Received bad checksum: expected [{}], given [{}], message [{}], original [{}]",
                    Integer.toHexString(calculated), Integer.toHexString(given), queueToString(messageBuffer),
                    queueToString(copy));
            return null;
        }

        // Determine the type of message received.
        pos = messageBuffer.indexOf(DELIMITER);
        final String messageType = messageBuffer.popString(pos, ASCII);

        // Drop the delimiter
        messageBuffer.pop();

        messageStr = messageBuffer.popString(messageBuffer.size(), ASCII);
        final String[] parts = messageStr.split(",", -1);

        // Delegate to the appropriate subclass to continue parsing.
        return NmeaMessage.createMessage(messageType, parts);
    }

    public static int calculateChecksum(ByteQueue queue) {
        int checksum = 0;
        for (int i = 0; i < queue.size(); i++)
            checksum ^= queue.peek(i);
        return checksum;
    }

    private String queueToString(ByteQueue queue) {
        final ByteQueue copy = (ByteQueue) queue.clone();
        return copy.popString(copy.size(), ASCII);
    }
}
