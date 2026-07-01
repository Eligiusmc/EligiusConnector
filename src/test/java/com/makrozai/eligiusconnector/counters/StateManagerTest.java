package com.makrozai.eligiusconnector.counters;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.makrozai.eligiusconnector.EligiusConnector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StateManagerTest {

    private static ServerMock server;
    private StateManager stateManager;
    private EligiusConnector pluginMock;

    @BeforeAll
    static void setUp() {
        server = MockBukkit.mock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void init() {
        pluginMock = mock(EligiusConnector.class);
        when(pluginMock.getServer()).thenReturn(server);
        when(pluginMock.getLogger()).thenReturn(Logger.getLogger("test"));
        stateManager = new StateManager(pluginMock);
    }

    @Test
    void builtInServerOnlineIsSet() {
        assertEquals("online", stateManager.getState("server_online"));
    }

    @Test
    void builtInWhitelistIsSet() {
        String whitelist = stateManager.getState("whitelist");
        assertNotNull(whitelist);
    }

    @Test
    void setStateChangesValue() {
        stateManager.setState("server_online", "offline");
        assertEquals("offline", stateManager.getState("server_online"));
    }

    @Test
    void setStateNotifiesListeners() {
        AtomicInteger callCount = new AtomicInteger(0);
        AtomicReference<String> newVal = new AtomicReference<>();

        stateManager.subscribe("server_online", (key, old, newV) -> {
            callCount.incrementAndGet();
            newVal.set(newV);
        });

        stateManager.setState("server_online", "offline");

        assertEquals(1, callCount.get());
        assertEquals("offline", newVal.get());
    }

    @Test
    void setStateNoChangeDoesNotNotify() {
        AtomicInteger callCount = new AtomicInteger(0);
        stateManager.subscribe("server_online", (key, old, newV) -> callCount.incrementAndGet());

        stateManager.setState("server_online", "online");

        assertEquals(0, callCount.get());
    }

    @Test
    void setStateNotifiesForNewKey() {
        AtomicInteger callCount = new AtomicInteger(0);
        stateManager.subscribe("test_key", (key, old, newV) -> callCount.incrementAndGet());

        stateManager.setState("test_key", "value1");

        assertEquals(1, callCount.get());
    }

    @Test
    void toggleFlipsBoolean() {
        stateManager.setState("whitelist", "true");
        stateManager.toggle("whitelist");
        assertEquals("false", stateManager.getState("whitelist"));

        stateManager.toggle("whitelist");
        assertEquals("true", stateManager.getState("whitelist"));
    }

    @Test
    void unsubscribeRemovesListener() {
        AtomicInteger callCount = new AtomicInteger(0);
        StateManager.StateListener listener = (key, old, newV) -> callCount.incrementAndGet();

        stateManager.subscribe("server_online", listener);
        stateManager.unsubscribe("server_online", listener);

        stateManager.setState("server_online", "offline");
        assertEquals(0, callCount.get());
    }

    @Test
    void getStateUnknownKeyReturnsEmpty() {
        assertEquals("", stateManager.getState("nonexistent"));
    }

    @Test
    void listenerExceptionDoesNotBreakOtherListeners() {
        AtomicInteger goodListenerCount = new AtomicInteger(0);

        stateManager.subscribe("server_online", (key, old, newV) -> {
            throw new RuntimeException("simulated failure");
        });
        stateManager.subscribe("server_online", (key, old, newV) -> goodListenerCount.incrementAndGet());

        stateManager.setState("server_online", "offline");

        assertEquals(1, goodListenerCount.get(), "Second listener should fire despite first failing");
    }

    @Test
    void resetRestoresBuiltIns() {
        stateManager.setState("custom_key", "custom_value");
        stateManager.reset();

        assertEquals("", stateManager.getState("custom_key"));
        assertEquals("online", stateManager.getState("server_online"));
    }
}
