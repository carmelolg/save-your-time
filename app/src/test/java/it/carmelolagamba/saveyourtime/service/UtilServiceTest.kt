package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.service.UtilService.Weekday
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UtilServiceTest {

    private lateinit var utilService: UtilService

    @Before
    fun setUp() {
        utilService = UtilService()
    }

    // ========== Weekday prev() tests ==========

    @Test
    fun `prev of MONDAY should return SUNDAY`() {
        assertEquals(Weekday.SUNDAY, utilService.prev(Weekday.MONDAY))
    }

    @Test
    fun `prev of SUNDAY should return SATURDAY`() {
        assertEquals(Weekday.SATURDAY, utilService.prev(Weekday.SUNDAY))
    }

    @Test
    fun `prev of TUESDAY should return MONDAY`() {
        assertEquals(Weekday.MONDAY, utilService.prev(Weekday.TUESDAY))
    }

    @Test
    fun `prev of WEDNESDAY should return TUESDAY`() {
        assertEquals(Weekday.TUESDAY, utilService.prev(Weekday.WEDNESDAY))
    }

    @Test
    fun `prev of THURSDAY should return WEDNESDAY`() {
        assertEquals(Weekday.WEDNESDAY, utilService.prev(Weekday.THURSDAY))
    }

    @Test
    fun `prev of FRIDAY should return THURSDAY`() {
        assertEquals(Weekday.THURSDAY, utilService.prev(Weekday.FRIDAY))
    }

    @Test
    fun `prev of SATURDAY should return FRIDAY`() {
        assertEquals(Weekday.FRIDAY, utilService.prev(Weekday.SATURDAY))
    }

    // ========== Weekday next() tests ==========

    @Test
    fun `next of SATURDAY should return SUNDAY`() {
        assertEquals(Weekday.SUNDAY, utilService.next(Weekday.SATURDAY))
    }

    @Test
    fun `next of SUNDAY should return MONDAY`() {
        assertEquals(Weekday.MONDAY, utilService.next(Weekday.SUNDAY))
    }

    @Test
    fun `next of MONDAY should return TUESDAY`() {
        assertEquals(Weekday.TUESDAY, utilService.next(Weekday.MONDAY))
    }

    @Test
    fun `next of TUESDAY should return WEDNESDAY`() {
        assertEquals(Weekday.WEDNESDAY, utilService.next(Weekday.TUESDAY))
    }

    @Test
    fun `next of WEDNESDAY should return THURSDAY`() {
        assertEquals(Weekday.THURSDAY, utilService.next(Weekday.WEDNESDAY))
    }

    @Test
    fun `next of THURSDAY should return FRIDAY`() {
        assertEquals(Weekday.FRIDAY, utilService.next(Weekday.THURSDAY))
    }

    @Test
    fun `next of FRIDAY should return SATURDAY`() {
        assertEquals(Weekday.SATURDAY, utilService.next(Weekday.FRIDAY))
    }

    // ========== prev with offset tests ==========

    @Test
    fun `prev with offset 0 should return same day`() {
        assertEquals(Weekday.WEDNESDAY, utilService.prev(Weekday.WEDNESDAY, 0))
    }

    @Test
    fun `prev with offset 1 should return previous day`() {
        assertEquals(Weekday.TUESDAY, utilService.prev(Weekday.WEDNESDAY, 1))
    }

    @Test
    fun `prev with offset 2 should return 2 days back`() {
        assertEquals(Weekday.MONDAY, utilService.prev(Weekday.WEDNESDAY, 2))
    }

    @Test
    fun `prev with offset 3 from WEDNESDAY should return SUNDAY`() {
        assertEquals(Weekday.SUNDAY, utilService.prev(Weekday.WEDNESDAY, 3))
    }

    @Test
    fun `prev with offset 7 should return same day (full circle)`() {
        assertEquals(Weekday.MONDAY, utilService.prev(Weekday.MONDAY, 7))
    }

    @Test
    fun `prev with offset wrapping around week`() {
        assertEquals(Weekday.FRIDAY, utilService.prev(Weekday.MONDAY, 3))
    }

    // ========== next with offset tests ==========

    @Test
    fun `next with offset 0 should return same day`() {
        assertEquals(Weekday.WEDNESDAY, utilService.next(Weekday.WEDNESDAY, 0))
    }

    @Test
    fun `next with offset 1 should return next day`() {
        assertEquals(Weekday.THURSDAY, utilService.next(Weekday.WEDNESDAY, 1))
    }

    @Test
    fun `next with offset 2 should return 2 days forward`() {
        assertEquals(Weekday.FRIDAY, utilService.next(Weekday.WEDNESDAY, 2))
    }

    @Test
    fun `next with offset 7 should return same day (full circle)`() {
        assertEquals(Weekday.MONDAY, utilService.next(Weekday.MONDAY, 7))
    }

    @Test
    fun `next with offset wrapping around week`() {
        assertEquals(Weekday.TUESDAY, utilService.next(Weekday.SATURDAY, 3))
    }

    @Test
    fun `next with offset 4 from FRIDAY should return TUESDAY`() {
        assertEquals(Weekday.TUESDAY, utilService.next(Weekday.FRIDAY, 4))
    }

    // ========== todayMidnightMillis tests ==========

    @Test
    fun `todayMidnightMillis should return midnight of today`() {
        val midnight = utilService.todayMidnightMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = midnight

        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun `todayMidnightMillis should be less than or equal to current time`() {
        val midnight = utilService.todayMidnightMillis()
        assertTrue(midnight <= System.currentTimeMillis())
    }

    // ========== tomorrowMidnightMillis tests ==========

    @Test
    fun `tomorrowMidnightMillis should return midnight of tomorrow`() {
        val tomorrow = utilService.tomorrowMidnightMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = tomorrow

        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun `tomorrowMidnightMillis should be exactly 24h after todayMidnightMillis`() {
        val today = utilService.todayMidnightMillis()
        val tomorrow = utilService.tomorrowMidnightMillis()
        assertEquals(24 * 60 * 60 * 1000L, tomorrow - today)
    }

    @Test
    fun `tomorrowMidnightMillis should be greater than current time`() {
        val tomorrow = utilService.tomorrowMidnightMillis()
        assertTrue(tomorrow > System.currentTimeMillis())
    }

    // ========== yesterdayMidnightMillis tests ==========

    @Test
    fun `yesterdayMidnightMillis should return midnight of yesterday`() {
        val yesterday = utilService.yesterdayMidnightMillis()
        val cal = Calendar.getInstance()
        cal.timeInMillis = yesterday

        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(Calendar.MINUTE))
        assertEquals(0, cal.get(Calendar.SECOND))
        assertEquals(0, cal.get(Calendar.MILLISECOND))
    }

    @Test
    fun `yesterdayMidnightMillis should be exactly 24h before todayMidnightMillis`() {
        val yesterday = utilService.yesterdayMidnightMillis()
        val today = utilService.todayMidnightMillis()
        assertEquals(24 * 60 * 60 * 1000L, today - yesterday)
    }

    @Test
    fun `yesterdayMidnightMillis should be less than current time`() {
        val yesterday = utilService.yesterdayMidnightMillis()
        assertTrue(yesterday < System.currentTimeMillis())
    }

    // ========== getMidnight tests ==========

    @Test
    fun `getMidnight with null params should return today midnight`() {
        val midnight = utilService.getMidnight()
        assertEquals(utilService.todayMidnightMillis(), midnight)
    }

    @Test
    fun `getMidnight with tomorrow param should return tomorrow midnight`() {
        val params = UtilService.TimeParams().tomorrow(true).build()
        val midnight = utilService.getMidnight(params)
        assertEquals(utilService.tomorrowMidnightMillis(), midnight)
    }

    @Test
    fun `getMidnight with week offset 0 should return this week`() {
        val params = UtilService.TimeParams().week(0).build()
        val midnight = utilService.getMidnight(params)
        assertEquals(utilService.todayMidnightMillis(), midnight)
    }

    @Test
    fun `getMidnight with weekday MONDAY should return Monday midnight`() {
        val params = UtilService.TimeParams().weekday(Weekday.MONDAY).build()
        val midnight = utilService.getMidnight(params)
        val cal = Calendar.getInstance()
        cal.timeInMillis = midnight

        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK))
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun `getMidnight with weekday FRIDAY should return Friday midnight`() {
        val params = UtilService.TimeParams().weekday(Weekday.FRIDAY).build()
        val midnight = utilService.getMidnight(params)
        val cal = Calendar.getInstance()
        cal.timeInMillis = midnight

        assertEquals(Calendar.FRIDAY, cal.get(Calendar.DAY_OF_WEEK))
    }

    @Test
    fun `getMidnight with week -1 should return last week`() {
        val params = UtilService.TimeParams().week(-1).build()
        val midnight = utilService.getMidnight(params)
        val today = utilService.todayMidnightMillis()
        // Last week should be approximately 7 days before
        assertTrue(today - midnight >= 6 * 24 * 60 * 60 * 1000L)
        assertTrue(today - midnight <= 8 * 24 * 60 * 60 * 1000L)
    }

    @Test
    fun `getMidnight with week +1 should return next week`() {
        val params = UtilService.TimeParams().week(1).build()
        val midnight = utilService.getMidnight(params)
        val today = utilService.todayMidnightMillis()
        // Next week should be approximately 7 days after
        assertTrue(midnight - today >= 6 * 24 * 60 * 60 * 1000L)
        assertTrue(midnight - today <= 8 * 24 * 60 * 60 * 1000L)
    }

    @Test
    fun `getMidnight with NO_DAY weekday should not change day`() {
        val params = UtilService.TimeParams().weekday(Weekday.NO_DAY).build()
        val midnight = utilService.getMidnight(params)
        assertEquals(utilService.todayMidnightMillis(), midnight)
    }

    @Test
    fun `getMidnight with combined weekday and week params`() {
        val params = UtilService.TimeParams()
            .weekday(Weekday.MONDAY)
            .week(-1)
            .build()
        val midnight = utilService.getMidnight(params)
        val cal = Calendar.getInstance()
        cal.timeInMillis = midnight

        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK))
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
    }

    // ========== TimeParams builder tests ==========

    @Test
    fun `TimeParams default values`() {
        val params = UtilService.TimeParams()
        assertEquals(0, params.week)
        assertFalse(params.tomorrow)
        assertEquals(Weekday.NO_DAY, params.weekday)
    }

    @Test
    fun `TimeParams builder chain`() {
        val params = UtilService.TimeParams()
            .week(2)
            .tomorrow(true)
            .weekday(Weekday.FRIDAY)
            .build()

        assertEquals(2, params.week)
        assertTrue(params.tomorrow)
        assertEquals(Weekday.FRIDAY, params.weekday)
    }

    @Test
    fun `TimeParams week setter`() {
        val params = UtilService.TimeParams().week(-3).build()
        assertEquals(-3, params.week)
    }

    @Test
    fun `TimeParams tomorrow setter`() {
        val params = UtilService.TimeParams().tomorrow(true).build()
        assertTrue(params.tomorrow)
    }

    @Test
    fun `TimeParams weekday setter`() {
        val params = UtilService.TimeParams().weekday(Weekday.SATURDAY).build()
        assertEquals(Weekday.SATURDAY, params.weekday)
    }

    // ========== getDateFromToday tests ==========

    @Test
    fun `getDateFromToday with offset 0 should return today date`() {
        val result = utilService.getDateFromToday(0)
        val expected = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Calendar.getInstance().time)
        assertEquals(expected, result)
    }

    @Test
    fun `getDateFromToday with offset -1 should return yesterday date`() {
        val result = utilService.getDateFromToday(-1)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val expected = SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.time)
        assertEquals(expected, result)
    }

    @Test
    fun `getDateFromToday with offset 1 should return tomorrow date`() {
        val result = utilService.getDateFromToday(1)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val expected = SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.time)
        assertEquals(expected, result)
    }

    @Test
    fun `getDateFromToday with offset -7 should return a week ago date`() {
        val result = utilService.getDateFromToday(-7)
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -7)
        val expected = SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.time)
        assertEquals(expected, result)
    }

    @Test
    fun `getDateFromToday format should match dd slash MM`() {
        val result = utilService.getDateFromToday(0)
        assertTrue(result.matches(Regex("\\d{2}/\\d{2}")))
    }

    // ========== Weekday enum tests ==========

    @Test
    fun `Weekday enum should have 8 values including NO_DAY`() {
        assertEquals(8, Weekday.entries.size)
    }

    @Test
    fun `Weekday ordinals should be correct`() {
        assertEquals(0, Weekday.NO_DAY.ordinal)
        assertEquals(1, Weekday.SUNDAY.ordinal)
        assertEquals(2, Weekday.MONDAY.ordinal)
        assertEquals(3, Weekday.TUESDAY.ordinal)
        assertEquals(4, Weekday.WEDNESDAY.ordinal)
        assertEquals(5, Weekday.THURSDAY.ordinal)
        assertEquals(6, Weekday.FRIDAY.ordinal)
        assertEquals(7, Weekday.SATURDAY.ordinal)
    }

    @Test
    fun `Weekday valueOf should work correctly`() {
        assertEquals(Weekday.MONDAY, Weekday.valueOf("MONDAY"))
        assertEquals(Weekday.FRIDAY, Weekday.valueOf("FRIDAY"))
    }

    // ========== isNearMidnight tests ==========

    @Test
    fun `isNearMidnight should return boolean`() {
        // This test just verifies the method runs without error
        val result = utilService.isNearMidnight()
        assertNotNull(result)
    }

    // ========== Edge case tests ==========

    @Test
    fun `prev and next are inverse operations`() {
        for (day in Weekday.entries) {
            if (day == Weekday.NO_DAY) continue
            assertEquals(day, utilService.next(utilService.prev(day)))
            assertEquals(day, utilService.prev(utilService.next(day)))
        }
    }

    @Test
    fun `cycling through 7 prev should return to same day`() {
        for (day in Weekday.entries) {
            if (day == Weekday.NO_DAY) continue
            assertEquals(day, utilService.prev(day, 7))
        }
    }

    @Test
    fun `cycling through 7 next should return to same day`() {
        for (day in Weekday.entries) {
            if (day == Weekday.NO_DAY) continue
            assertEquals(day, utilService.next(day, 7))
        }
    }

    @Test
    fun `prev 14 should be same as prev 0`() {
        for (day in Weekday.entries) {
            if (day == Weekday.NO_DAY) continue
            assertEquals(day, utilService.prev(day, 14))
        }
    }

    @Test
    fun `next 14 should be same as next 0`() {
        for (day in Weekday.entries) {
            if (day == Weekday.NO_DAY) continue
            assertEquals(day, utilService.next(day, 14))
        }
    }
}

