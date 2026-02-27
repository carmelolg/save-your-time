package it.carmelolagamba.saveyourtime.service.worker

import org.junit.Assert.*
import org.junit.Test

class SYTAlarmItemTest {

    @Test
    fun `SYTAlarmItem should store correct message`() {
        val item = SYTAlarmItem("test_message")
        assertEquals("test_message", item.message)
    }

    @Test
    fun `SYTAlarmItem with empty message`() {
        val item = SYTAlarmItem("")
        assertEquals("", item.message)
    }

    @Test
    fun `SYTAlarmItem equality`() {
        val item1 = SYTAlarmItem("msg")
        val item2 = SYTAlarmItem("msg")
        assertEquals(item1, item2)
    }

    @Test
    fun `SYTAlarmItem inequality`() {
        val item1 = SYTAlarmItem("msg1")
        val item2 = SYTAlarmItem("msg2")
        assertNotEquals(item1, item2)
    }

    @Test
    fun `SYTAlarmItem hashCode should be equal for equal objects`() {
        val item1 = SYTAlarmItem("msg")
        val item2 = SYTAlarmItem("msg")
        assertEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun `SYTAlarmItem hashCode should differ for different messages`() {
        val item1 = SYTAlarmItem("msg1")
        val item2 = SYTAlarmItem("msg2")
        assertNotEquals(item1.hashCode(), item2.hashCode())
    }

    @Test
    fun `SYTAlarmItem toString should contain message`() {
        val item = SYTAlarmItem("my_alarm")
        val str = item.toString()
        assertTrue(str.contains("my_alarm"))
    }

    @Test
    fun `SYTAlarmItem copy should work`() {
        val item = SYTAlarmItem("original")
        val copy = item.copy(message = "copied")
        assertEquals("copied", copy.message)
    }

    @Test
    fun `SYTAlarmItem with long message`() {
        val longMessage = "a".repeat(1000)
        val item = SYTAlarmItem(longMessage)
        assertEquals(1000, item.message.length)
    }

    @Test
    fun `SYTAlarmItem with special characters`() {
        val item = SYTAlarmItem("!@#\$%^&*()_+-=")
        assertEquals("!@#\$%^&*()_+-=", item.message)
    }
}

