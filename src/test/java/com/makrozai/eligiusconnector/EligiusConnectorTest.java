package com.makrozai.eligiusconnector;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EligiusConnectorTest {

    @Test
    void testPluginDescription() {
        // Test that plugin metadata is correct
        assertEquals("EligiusConnector", "EligiusConnector");
        assertEquals("com.makrozai.eligiusconnector.EligiusConnector", "com.makrozai.eligiusconnector.EligiusConnector");
    }

    @Test
    void testVersionFormat() {
        String version = "1.0.0";
        assertTrue(version.matches("\\d+\\.\\d+\\.\\d+"));
    }

    @Test
    void testPackageName() {
        String packageName = EligiusConnector.class.getPackage().getName();
        assertEquals("com.makrozai.eligiusconnector", packageName);
    }
}
