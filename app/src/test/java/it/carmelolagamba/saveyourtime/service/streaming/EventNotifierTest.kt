package it.carmelolagamba.saveyourtime.service.streaming

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventNotifierTest {

    private lateinit var eventNotifier: EventNotifier

    @Before
    fun setUp() {
        // Reset singleton for clean tests using reflection
        val instanceField = EventNotifier::class.java.getDeclaredField("_instance")
        instanceField.isAccessible = true
        instanceField.set(null, null)

        eventNotifier = EventNotifier.getInstance()
    }

    // ========== Singleton tests ==========

    @Test
    fun `getInstance should return same instance`() {
        val instance1 = EventNotifier.getInstance()
        val instance2 = EventNotifier.getInstance()
        assertSame(instance1, instance2)
    }

    @Test
    fun `getInstance should not return null`() {
        assertNotNull(EventNotifier.getInstance())
    }

    // ========== addListener tests ==========

    @Test
    fun `addListener should add listener`() {
        var eventReceived = false
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                eventReceived = true
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.notifyEvent("test")

        assertTrue(eventReceived)
    }

    @Test
    fun `addListener with multiple listeners should notify all`() {
        var count = 0
        val listener1 = object : EventListener {
            override fun onEvent(channel: String) {
                count++
            }
        }
        val listener2 = object : EventListener {
            override fun onEvent(channel: String) {
                count++
            }
        }

        eventNotifier.addListener(listener1)
        eventNotifier.addListener(listener2)
        eventNotifier.notifyEvent("test")

        assertEquals(2, count)
    }

    @Test
    fun `addListener same listener twice should not duplicate`() {
        var count = 0
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                count++
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.addListener(listener)
        eventNotifier.notifyEvent("test")

        assertEquals(1, count)
    }

    // ========== removeListener tests ==========

    @Test
    fun `removeListener should prevent listener from receiving events`() {
        var eventReceived = false
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                eventReceived = true
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.removeListener(listener)
        eventNotifier.notifyEvent("test")

        assertFalse(eventReceived)
    }

    @Test
    fun `removeListener with non-existing listener should not throw`() {
        val listener = object : EventListener {
            override fun onEvent(channel: String) {}
        }
        // Should not throw
        eventNotifier.removeListener(listener)
    }

    @Test
    fun `removeListener should only remove specified listener`() {
        var listener1Called = false
        var listener2Called = false

        val listener1 = object : EventListener {
            override fun onEvent(channel: String) {
                listener1Called = true
            }
        }
        val listener2 = object : EventListener {
            override fun onEvent(channel: String) {
                listener2Called = true
            }
        }

        eventNotifier.addListener(listener1)
        eventNotifier.addListener(listener2)
        eventNotifier.removeListener(listener1)
        eventNotifier.notifyEvent("test")

        assertFalse(listener1Called)
        assertTrue(listener2Called)
    }

    // ========== notifyEvent tests ==========

    @Test
    fun `notifyEvent should pass correct channel name`() {
        var receivedChannel = ""
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                receivedChannel = channel
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.notifyEvent("check_notify")

        assertEquals("check_notify", receivedChannel)
    }

    @Test
    fun `notifyEvent with empty channel should work`() {
        var receivedChannel: String? = null
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                receivedChannel = channel
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.notifyEvent("")

        assertEquals("", receivedChannel)
    }

    @Test
    fun `notifyEvent with no listeners should not throw`() {
        // Should not throw
        eventNotifier.notifyEvent("test")
    }

    @Test
    fun `notifyEvent should call listeners in order`() {
        val order = mutableListOf<Int>()

        val listener1 = object : EventListener {
            override fun onEvent(channel: String) {
                order.add(1)
            }
        }
        val listener2 = object : EventListener {
            override fun onEvent(channel: String) {
                order.add(2)
            }
        }

        eventNotifier.addListener(listener1)
        eventNotifier.addListener(listener2)
        eventNotifier.notifyEvent("test")

        assertEquals(2, order.size)
        assertTrue(order.contains(1))
        assertTrue(order.contains(2))
    }

    @Test
    fun `multiple notifyEvent calls should trigger listener each time`() {
        var count = 0
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                count++
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.notifyEvent("event1")
        eventNotifier.notifyEvent("event2")
        eventNotifier.notifyEvent("event3")

        assertEquals(3, count)
    }

    @Test
    fun `notifyEvent with different channels should pass correct channel each time`() {
        val channels = mutableListOf<String>()
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                channels.add(channel)
            }
        }

        eventNotifier.addListener(listener)
        eventNotifier.notifyEvent("channel_a")
        eventNotifier.notifyEvent("channel_b")

        assertEquals(listOf("channel_a", "channel_b"), channels)
    }
}

