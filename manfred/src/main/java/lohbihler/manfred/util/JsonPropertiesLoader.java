package lohbihler.manfred.util;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lohbihler.atomicjson.JMap;
import lohbihler.atomicjson.JsonReader;
import lohbihler.manfred.FlightLogger;

public class JsonPropertiesLoader {
    private static final Logger LOG = LoggerFactory.getLogger(JsonPropertiesLoader.class);

    public JMap load(String resourcePath) throws Exception {
        LOG.info("Loading properties...");

        JMap props;

        try (final InputStream propsIn = FlightLogger.class.getResourceAsStream(resourcePath)) {
            if (propsIn == null)
                throw new Exception("Could not find properties file");

            final JsonReader jsonIn = new JsonReader(new InputStreamReader(propsIn));
            props = jsonIn.read();
        }
        catch (final Exception e) {
            throw new Exception("Error loading properties", e);
        }

        LOG.info("Finished loading properties");

        return props;
    }
}
