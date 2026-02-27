package it.carmelolagamba.saveyourtime.service.streaming

import org.junit.Assert.*
import org.junit.Test

class EventListenerTest {

    @Test
    fun `EventListener interface can be implemented`() {
        val listener = object : EventListener {
            var lastChannel: String? = null
            override fun onEvent(channel: String) {
                lastChannel = channel
            }
        }

        listener.onEvent("test_channel")
        assertEquals("test_channel", listener.lastChannel)
    }

    @Test
    fun `EventListener can receive empty channel`() {
        val listener = object : EventListener {
            var called = false
            override fun onEvent(channel: String) {
                called = true
            }
        }

        listener.onEvent("")
        assertTrue(listener.called)
    }

    @Test
    fun `EventListener can be called multiple times`() {
        val listener = object : EventListener {
            var callCount = 0
            override fun onEvent(channel: String) {
                callCount++
            }
        }

        listener.onEvent("a")
        listener.onEvent("b")
        listener.onEvent("c")
        assertEquals(3, listener.callCount)
    }

    @Test
    fun `EventListener preserves channel value`() {
        val channels = mutableListOf<String>()
        val listener = object : EventListener {
            override fun onEvent(channel: String) {
                channels.add(channel)
            }
        }

        listener.onEvent("channel_1")
        listener.onEvent("channel_2")

        assertEquals(listOf("channel_1", "channel_2"), channels)
    }

    @Test
    fun `Multiple EventListener instances are independent`() {
        var count1 = 0
        var count2 = 0

        val listener1 = object : EventListener {
            override fun onEvent(channel: String) { count1++ }
        }
        val listener2 = object : EventListener {
            override fun onEvent(channel: String) { count2++ }
        }

        listener1.onEvent("x")
        listener1.onEvent("y")
        listener2.onEvent("z")

        assertEquals(2, count1)
        assertEquals(1, count2)
    }
}

