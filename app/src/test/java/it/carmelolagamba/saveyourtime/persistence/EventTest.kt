package it.carmelolagamba.saveyourtime.persistence

import org.junit.Assert.*
import org.junit.Test

class EventTest {

    @Test
    fun `Event data class should store correct values`() {
        val event = Event(1, "check_notify", "com.instagram.android", 1234567890L, 30, false)

        assertEquals(1, event.id)
        assertEquals("check_notify", event.name)
        assertEquals("com.instagram.android", event.appId)
        assertEquals(1234567890L, event.insertDate)
        assertEquals(30, event.usageAtEvent)
        assertFalse(event.notified)
    }

    @Test
    fun `Event with null id should work`() {
        val event = Event(null, "test", "com.test", 0L, 0, false)
        assertNull(event.id)
    }

    @Test
    fun `Event mutable fields should be updatable`() {
        val event = Event(1, "test", "com.test", 100L, 10, false)

        event.insertDate = 200L
        event.usageAtEvent = 50
        event.notified = true

        assertEquals(200L, event.insertDate)
        assertEquals(50, event.usageAtEvent)
        assertTrue(event.notified)
    }

    @Test
    fun `Event data class equality`() {
        val event1 = Event(1, "test", "com.test", 100L, 10, false)
        val event2 = Event(1, "test", "com.test", 100L, 10, false)

        assertEquals(event1, event2)
    }

    @Test
    fun `Event data class inequality`() {
        val event1 = Event(1, "test", "com.test", 100L, 10, false)
        val event2 = Event(2, "test", "com.test", 100L, 10, false)

        assertNotEquals(event1, event2)
    }

    @Test
    fun `Event copy should work correctly`() {
        val event = Event(1, "test", "com.test", 100L, 10, false)
        val copy = event.copy(notified = true)

        assertTrue(copy.notified)
        assertEquals(event.id, copy.id)
        assertEquals(event.name, copy.name)
    }

    @Test
    fun `Event hashCode should be equal for equal objects`() {
        val event1 = Event(1, "test", "com.test", 100L, 10, true)
        val event2 = Event(1, "test", "com.test", 100L, 10, true)

        assertEquals(event1.hashCode(), event2.hashCode())
    }

    @Test
    fun `Event toString should contain relevant info`() {
        val event = Event(5, "channel", "com.app", 999L, 42, true)
        val str = event.toString()
        assertTrue(str.contains("channel"))
        assertTrue(str.contains("com.app"))
        assertTrue(str.contains("42"))
    }

    @Test
    fun `Event with zero values`() {
        val event = Event(0, "", "", 0L, 0, false)
        assertEquals(0, event.id)
        assertEquals("", event.name)
        assertEquals("", event.appId)
        assertEquals(0L, event.insertDate)
        assertEquals(0, event.usageAtEvent)
        assertFalse(event.notified)
    }

    @Test
    fun `Event with max values`() {
        val event = Event(Int.MAX_VALUE, "test", "com.test", Long.MAX_VALUE, Int.MAX_VALUE, true)
        assertEquals(Int.MAX_VALUE, event.id)
        assertEquals(Long.MAX_VALUE, event.insertDate)
        assertEquals(Int.MAX_VALUE, event.usageAtEvent)
    }
}

