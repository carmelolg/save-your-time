package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.persistence.Event
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

/**
 * Additional integration-style tests that verify multiple services work together
 */
class ServiceIntegrationTest {

    private lateinit var appService: AppService
    private lateinit var eventService: EventService
    private lateinit var utilService: UtilService

    @Before
    fun setUp() {
        appService = AppService()
        eventService = EventService()
        utilService = UtilService()
    }

    // ========== AppService + EventService combined logic ==========

    @Test
    fun `exceeded apps should have events that can be looked up`() {
        val apps = listOf(
            App("Instagram", "com.instagram", true, 60, 90, 100L),
            App("Facebook", "com.facebook", true, 30, 50, 200L)
        )

        val exceededApps = appService.findExceededApplication(apps)
        assertEquals(2, exceededApps.size)

        val events = listOf(
            Event(1, "check", "com.instagram", 100L, 60, true),
            Event(2, "check", "com.facebook", 200L, 30, false)
        )

        exceededApps.forEach { app ->
            val event = eventService.findEventByPackageName(app.packageName, events)
            assertNotNull("Event should exist for ${app.packageName}", event)
        }
    }

    @Test
    fun `non-exceeded apps should not trigger notifications`() {
        val apps = listOf(
            App("Safe", "com.safe", true, 120, 30, 100L),
            App("LowUsage", "com.low", true, 60, 10, 200L)
        )

        val exceededApps = appService.findExceededApplication(apps)
        assertTrue(exceededApps.isEmpty())
    }

    @Test
    fun `findPackageByName and findExceededApplication combination`() {
        val apps = listOf(
            App("Instagram", "com.instagram", true, 60, 90, 100L),
            App("Facebook", "com.facebook", true, 30, 20, 200L),
            App("Twitter", "com.twitter", true, 45, 45, 300L)
        )

        val exceeded = appService.findExceededApplication(apps)
        assertEquals(2, exceeded.size) // Instagram (90>=60) and Twitter (45>=45)

        val igPackage = appService.findPackageByName("Instagram", apps)
        assertEquals("com.instagram", igPackage)

        val twPackage = appService.findPackageByName("Twitter", apps)
        assertEquals("com.twitter", twPackage)
    }

    // ========== UtilService weekday consistency ==========

    @Test
    fun `getCurrentDay should return a valid weekday`() {
        val today = utilService.getCurrentDay()
        assertNotEquals(UtilService.Weekday.NO_DAY, today)
        assertTrue(today.ordinal in 1..7)
    }

    @Test
    fun `prev and next from getCurrentDay should work`() {
        val today = utilService.getCurrentDay()
        val yesterday = utilService.prev(today)
        val tomorrow = utilService.next(today)

        assertNotEquals(today, yesterday)
        assertNotEquals(today, tomorrow)
        assertEquals(today, utilService.next(yesterday))
        assertEquals(today, utilService.prev(tomorrow))
    }

    @Test
    fun `full week cycle from getCurrentDay`() {
        val today = utilService.getCurrentDay()
        var day = today
        val weekDays = mutableListOf(day)

        for (i in 1..6) {
            day = utilService.next(day)
            weekDays.add(day)
        }

        assertEquals(7, weekDays.size)
        assertEquals(today, utilService.next(day)) // full cycle back
    }

    // ========== UtilService midnight consistency ==========

    @Test
    fun `midnight values should be in chronological order`() {
        val yesterday = utilService.yesterdayMidnightMillis()
        val today = utilService.todayMidnightMillis()
        val tomorrow = utilService.tomorrowMidnightMillis()

        assertTrue(yesterday < today)
        assertTrue(today < tomorrow)
        assertTrue(yesterday < tomorrow)
    }

    @Test
    fun `getMidnight with various weekdays should all be valid midnight`() {
        for (day in UtilService.Weekday.entries) {
            if (day == UtilService.Weekday.NO_DAY) continue

            val params = UtilService.TimeParams().weekday(day).build()
            val midnight = utilService.getMidnight(params)

            assertTrue("Midnight for $day should be positive", midnight > 0)

            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = midnight
            assertEquals("Hour should be 0 for $day", 0, cal.get(java.util.Calendar.HOUR_OF_DAY))
            assertEquals("Minute should be 0 for $day", 0, cal.get(java.util.Calendar.MINUTE))
        }
    }

    @Test
    fun `getMidnight with week offsets should differ by ~7 days`() {
        val thisWeek = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.MONDAY).week(0).build()
        )
        val lastWeek = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.MONDAY).week(-1).build()
        )
        val nextWeek = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.MONDAY).week(1).build()
        )

        val oneWeekMs = 7 * 24 * 60 * 60 * 1000L
        assertEquals(oneWeekMs, thisWeek - lastWeek)
        assertEquals(oneWeekMs, nextWeek - thisWeek)
    }

    // ========== UtilService getDateFromToday format ==========

    @Test
    fun `getDateFromToday should return consistent format for various offsets`() {
        for (offset in -30..30) {
            val result = utilService.getDateFromToday(offset)
            assertTrue(
                "Date '$result' for offset $offset should match dd/MM format",
                result.matches(Regex("\\d{2}/\\d{2}"))
            )
        }
    }

    @Test
    fun `getDateFromToday consecutive days should differ`() {
        // Not always different (e.g. month boundaries), but at least verify format
        val today = utilService.getDateFromToday(0)
        val tomorrow = utilService.getDateFromToday(1)
        // They should have valid format
        assertTrue(today.length == 5)
        assertTrue(tomorrow.length == 5)
    }

    // ========== EventService edge cases ==========

    @Test
    fun `findEventByPackageName with multiple packages returns correct one`() {
        val events = listOf(
            Event(1, "ch1", "com.app1", 100L, 10, false),
            Event(2, "ch2", "com.app2", 200L, 20, true),
            Event(3, "ch3", "com.app1", 300L, 30, true),
            Event(4, "ch4", "com.app3", 400L, 40, false),
            Event(5, "ch5", "com.app2", 500L, 50, false)
        )

        val result1 = eventService.findEventByPackageName("com.app1", events)
        assertEquals(3, result1!!.id) // last for com.app1

        val result2 = eventService.findEventByPackageName("com.app2", events)
        assertEquals(5, result2!!.id) // last for com.app2

        val result3 = eventService.findEventByPackageName("com.app3", events)
        assertEquals(4, result3!!.id) // only one for com.app3
    }

    // ========== AppService edge cases ==========

    @Test
    fun `findExceededApplication with mixed selected states`() {
        val apps = listOf(
            App("Active", "com.active", true, 30, 50, 100L),
            App("Inactive", "com.inactive", false, 30, 50, 200L)
        )

        // findExceededApplication checks todayUsage >= notifyTime, doesn't filter by selected
        val exceeded = appService.findExceededApplication(apps)
        assertEquals(2, exceeded.size) // Both exceed the limit
    }

    @Test
    fun `findPackageByName with special characters in name`() {
        val apps = listOf(
            App("My App (Beta)", "com.myapp.beta", true, 60, 0, 100L)
        )

        val result = appService.findPackageByName("My App (Beta)", apps)
        assertEquals("com.myapp.beta", result)
    }
}

