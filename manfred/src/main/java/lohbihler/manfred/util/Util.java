package lohbihler.manfred.util;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Damned utilities that just can't find a better home.
 */
public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    public static void closeQuietly(Closeable db) {
        try {
            if (db != null)
                db.close();
        }
        catch (final IOException e) {
            // Not entirely quiet
            LOGGER.warn("Error closing DB", e);
        }
    }
}
