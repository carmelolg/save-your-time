package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.persistence.Event
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EventServiceTest {

    private lateinit var eventService: EventService

    @Before
    fun setUp() {
        eventService = EventService()
    }

    // ========== findEventByPackageName tests ==========

    @Test
    fun `findEventByPackageName should return last event for given appId`() {
        val events = listOf(
            Event(1, "check", "com.instagram.android", 100L, 10, false),
            Event(2, "check", "com.instagram.android", 200L, 20, true),
            Event(3, "check", "com.facebook.katana", 150L, 15, false)
        )

        val result = eventService.findEventByPackageName("com.instagram.android", events)

        assertNotNull(result)
        assertEquals(2, result!!.id)
        assertEquals(20, result.usageAtEvent)
    }

    @Test
    fun `findEventByPackageName should return null when no events for appId`() {
        val events = listOf(
            Event(1, "check", "com.instagram.android", 100L, 10, false)
        )

        val result = eventService.findEventByPackageName("com.nonexistent", events)
        assertNull(result)
    }

    @Test
    fun `findEventByPackageName should return null for empty list`() {
        val result = eventService.findEventByPackageName("com.test", emptyList())
        assertNull(result)
    }

    @Test
    fun `findEventByPackageName should return single event when only one exists`() {
        val events = listOf(
            Event(1, "check", "com.test", 100L, 10, false)
        )

        val result = eventService.findEventByPackageName("com.test", events)
        assertNotNull(result)
        assertEquals(1, result!!.id)
    }

    @Test
    fun `findEventByPackageName should return last event by list order`() {
        val events = listOf(
            Event(1, "check", "com.test", 100L, 10, false),
            Event(5, "check", "com.test", 500L, 50, true),
            Event(3, "check", "com.test", 300L, 30, false)
        )

        val result = eventService.findEventByPackageName("com.test", events)
        assertNotNull(result)
        assertEquals(3, result!!.id) // last in list
    }

    @Test
    fun `findEventByPackageName is case sensitive`() {
        val events = listOf(
            Event(1, "check", "com.Test", 100L, 10, false)
        )

        val result = eventService.findEventByPackageName("com.test", events)
        assertNull(result)
    }

    @Test
    fun `findEventByPackageName with mixed packages`() {
        val events = listOf(
            Event(1, "check", "com.app1", 100L, 10, false),
            Event(2, "check", "com.app2", 200L, 20, false),
            Event(3, "check", "com.app1", 300L, 30, true),
            Event(4, "check", "com.app3", 400L, 40, false)
        )

        val result1 = eventService.findEventByPackageName("com.app1", events)
        assertNotNull(result1)
        assertEquals(3, result1!!.id)

        val result2 = eventService.findEventByPackageName("com.app2", events)
        assertNotNull(result2)
        assertEquals(2, result2!!.id)

        val result3 = eventService.findEventByPackageName("com.app3", events)
        assertNotNull(result3)
        assertEquals(4, result3!!.id)
    }

    @Test
    fun `findEventByPackageName with empty appId`() {
        val events = listOf(
            Event(1, "check", "", 100L, 10, false)
        )

        val result = eventService.findEventByPackageName("", events)
        assertNotNull(result)
        assertEquals(1, result!!.id)
    }
}

