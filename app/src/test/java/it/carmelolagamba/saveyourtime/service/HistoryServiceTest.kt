package it.carmelolagamba.saveyourtime.service

import android.content.Context
import it.carmelolagamba.saveyourtime.service.UtilService.Weekday
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class HistoryServiceTest {

    private lateinit var historyService: HistoryService
    private lateinit var mockUtilService: UtilService

    @Before
    fun setUp() {
        historyService = HistoryService()
        mockUtilService = Mockito.mock(UtilService::class.java)

        // Inject mock utilService via reflection
        val field = HistoryService::class.java.getDeclaredField("utilService")
        field.isAccessible = true
        field.set(historyService, mockUtilService)
    }

    // ========== getLast7DaysUsage tests ==========

    @Test
    fun `getLast7DaysUsage should return 7 entries`() {
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.WEDNESDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 1)).thenReturn(Weekday.TUESDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 2)).thenReturn(Weekday.MONDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 3)).thenReturn(Weekday.SUNDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 4)).thenReturn(Weekday.SATURDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 5)).thenReturn(Weekday.FRIDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 6)).thenReturn(Weekday.THURSDAY)

        val currentWeekMap = mapOf(
            "MONDAY" to 10,
            "TUESDAY" to 20,
            "WEDNESDAY" to 30,
            "THURSDAY" to 0,
            "FRIDAY" to 0,
            "SATURDAY" to 0,
            "SUNDAY" to 0
        )

        val lastWeekMap = mapOf(
            "MONDAY" to 5,
            "TUESDAY" to 15,
            "WEDNESDAY" to 25,
            "THURSDAY" to 35,
            "FRIDAY" to 45,
            "SATURDAY" to 55,
            "SUNDAY" to 65
        )

        val result = historyService.getLast7DaysUsage(
            Mockito.mock(android.content.Context::class.java),
            "com.test",
            currentWeekMap,
            lastWeekMap
        )

        assertEquals(7, result.size)
    }

    @Test
    fun `getLast7DaysUsage should contain current day at index 7`() {
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.WEDNESDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 1)).thenReturn(Weekday.TUESDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 2)).thenReturn(Weekday.MONDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 3)).thenReturn(Weekday.SUNDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 4)).thenReturn(Weekday.SATURDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 5)).thenReturn(Weekday.FRIDAY)
        `when`(mockUtilService.prev(Weekday.WEDNESDAY, 6)).thenReturn(Weekday.THURSDAY)

        val currentWeekMap = mapOf(
            "MONDAY" to 10, "TUESDAY" to 20, "WEDNESDAY" to 30,
            "THURSDAY" to 0, "FRIDAY" to 0, "SATURDAY" to 0, "SUNDAY" to 0
        )
        val lastWeekMap = mapOf(
            "MONDAY" to 5, "TUESDAY" to 15, "WEDNESDAY" to 25,
            "THURSDAY" to 35, "FRIDAY" to 45, "SATURDAY" to 55, "SUNDAY" to 65
        )

        val result = historyService.getLast7DaysUsage(
            Mockito.mock(android.content.Context::class.java),
            "com.test",
            currentWeekMap,
            lastWeekMap
        )

        assertEquals("WEDNESDAY", result[7]!!.first)
        assertEquals(30, result[7]!!.second)
    }

    @Test
    fun `getLast7DaysUsage current day MONDAY should use last week for previous days`() {
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.MONDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 1)).thenReturn(Weekday.SUNDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 2)).thenReturn(Weekday.SATURDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 3)).thenReturn(Weekday.FRIDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 4)).thenReturn(Weekday.THURSDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 5)).thenReturn(Weekday.WEDNESDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 6)).thenReturn(Weekday.TUESDAY)

        val currentWeekMap = mapOf(
            "MONDAY" to 100, "TUESDAY" to 0, "WEDNESDAY" to 0,
            "THURSDAY" to 0, "FRIDAY" to 0, "SATURDAY" to 0, "SUNDAY" to 0
        )
        val lastWeekMap = mapOf(
            "MONDAY" to 10, "TUESDAY" to 20, "WEDNESDAY" to 30,
            "THURSDAY" to 40, "FRIDAY" to 50, "SATURDAY" to 60, "SUNDAY" to 70
        )

        val result = historyService.getLast7DaysUsage(
            Mockito.mock(android.content.Context::class.java),
            "com.test",
            currentWeekMap,
            lastWeekMap
        )

        // Current day (MONDAY) at index 7
        assertEquals("MONDAY", result[7]!!.first)
        assertEquals(100, result[7]!!.second)

        // SUNDAY should come from lastWeekMap (ordinal 1 -> special case)
        assertEquals("SUNDAY", result[6]!!.first)
        assertEquals(70, result[6]!!.second)
    }

    @Test
    fun `getLast7DaysUsage indices should be 1 through 7`() {
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.FRIDAY)
        `when`(mockUtilService.prev(Weekday.FRIDAY, 1)).thenReturn(Weekday.THURSDAY)
        `when`(mockUtilService.prev(Weekday.FRIDAY, 2)).thenReturn(Weekday.WEDNESDAY)
        `when`(mockUtilService.prev(Weekday.FRIDAY, 3)).thenReturn(Weekday.TUESDAY)
        `when`(mockUtilService.prev(Weekday.FRIDAY, 4)).thenReturn(Weekday.MONDAY)
        `when`(mockUtilService.prev(Weekday.FRIDAY, 5)).thenReturn(Weekday.SUNDAY)
        `when`(mockUtilService.prev(Weekday.FRIDAY, 6)).thenReturn(Weekday.SATURDAY)

        val currentWeekMap = mapOf(
            "MONDAY" to 10, "TUESDAY" to 20, "WEDNESDAY" to 30,
            "THURSDAY" to 40, "FRIDAY" to 50, "SATURDAY" to 0, "SUNDAY" to 0
        )
        val lastWeekMap = mapOf(
            "MONDAY" to 1, "TUESDAY" to 2, "WEDNESDAY" to 3,
            "THURSDAY" to 4, "FRIDAY" to 5, "SATURDAY" to 6, "SUNDAY" to 7
        )

        val result = historyService.getLast7DaysUsage(
            Mockito.mock(android.content.Context::class.java),
            "com.test",
            currentWeekMap,
            lastWeekMap
        )

        for (i in 1..7) {
            assertNotNull("Key $i should exist", result[i])
        }
    }

    @Test
    fun `getLast7DaysUsage all values should be pairs`() {
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.TUESDAY)
        `when`(mockUtilService.prev(Weekday.TUESDAY, 1)).thenReturn(Weekday.MONDAY)
        `when`(mockUtilService.prev(Weekday.TUESDAY, 2)).thenReturn(Weekday.SUNDAY)
        `when`(mockUtilService.prev(Weekday.TUESDAY, 3)).thenReturn(Weekday.SATURDAY)
        `when`(mockUtilService.prev(Weekday.TUESDAY, 4)).thenReturn(Weekday.FRIDAY)
        `when`(mockUtilService.prev(Weekday.TUESDAY, 5)).thenReturn(Weekday.THURSDAY)
        `when`(mockUtilService.prev(Weekday.TUESDAY, 6)).thenReturn(Weekday.WEDNESDAY)

        val currentWeekMap = mapOf(
            "MONDAY" to 10, "TUESDAY" to 20, "WEDNESDAY" to 0,
            "THURSDAY" to 0, "FRIDAY" to 0, "SATURDAY" to 0, "SUNDAY" to 0
        )
        val lastWeekMap = mapOf(
            "MONDAY" to 1, "TUESDAY" to 2, "WEDNESDAY" to 3,
            "THURSDAY" to 4, "FRIDAY" to 5, "SATURDAY" to 6, "SUNDAY" to 7
        )

        val result = historyService.getLast7DaysUsage(
            Mockito.mock(android.content.Context::class.java),
            "com.test",
            currentWeekMap,
            lastWeekMap
        )

        result.values.forEach { pair ->
            assertNotNull(pair.first)
            assertNotNull(pair.second)
            assertTrue(pair.first.isNotEmpty())
        }
    }

    @Test
    fun `getLast7DaysUsage with zero usage everywhere`() {
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.MONDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 1)).thenReturn(Weekday.SUNDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 2)).thenReturn(Weekday.SATURDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 3)).thenReturn(Weekday.FRIDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 4)).thenReturn(Weekday.THURSDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 5)).thenReturn(Weekday.WEDNESDAY)
        `when`(mockUtilService.prev(Weekday.MONDAY, 6)).thenReturn(Weekday.TUESDAY)

        val zeroMap = mapOf(
            "MONDAY" to 0, "TUESDAY" to 0, "WEDNESDAY" to 0,
            "THURSDAY" to 0, "FRIDAY" to 0, "SATURDAY" to 0, "SUNDAY" to 0
        )

        val result = historyService.getLast7DaysUsage(
            Mockito.mock(android.content.Context::class.java),
            "com.test",
            zeroMap,
            zeroMap
        )

        assertEquals(7, result.size)
        result.values.forEach { pair ->
            assertEquals(0, pair.second)
        }
    }

    // ========== getYesterdayUsage tests ==========

    @Test
    fun `getYesterdayUsage should return usage from mocked utilService`() {
        val mockContext = Mockito.mock(Context::class.java)
        `when`(mockUtilService.yesterdayMidnightMillis()).thenReturn(1000L)
        `when`(mockUtilService.todayMidnightMillis()).thenReturn(86401000L)
        `when`(
            mockUtilService.getUsageInMinutesByPackage(mockContext, "com.test", 1000L, 86401000L)
        ).thenReturn(45)

        val result = historyService.getYesterdayUsage(mockContext, "com.test")

        assertEquals(45, result)
    }

    @Test
    fun `getYesterdayUsage should return zero when no usage`() {
        val mockContext = Mockito.mock(Context::class.java)
        `when`(mockUtilService.yesterdayMidnightMillis()).thenReturn(1000L)
        `when`(mockUtilService.todayMidnightMillis()).thenReturn(86401000L)
        `when`(
            mockUtilService.getUsageInMinutesByPackage(mockContext, "com.test", 1000L, 86401000L)
        ).thenReturn(0)

        val result = historyService.getYesterdayUsage(mockContext, "com.test")

        assertEquals(0, result)
    }

    @Test
    fun `getYesterdayUsage should use yesterday and today midnight as range`() {
        val mockContext = Mockito.mock(Context::class.java)
        val yesterdayMillis = 1000L
        val todayMillis = 86401000L
        `when`(mockUtilService.yesterdayMidnightMillis()).thenReturn(yesterdayMillis)
        `when`(mockUtilService.todayMidnightMillis()).thenReturn(todayMillis)
        `when`(
            mockUtilService.getUsageInMinutesByPackage(
                mockContext, "com.package", yesterdayMillis, todayMillis
            )
        ).thenReturn(120)

        val result = historyService.getYesterdayUsage(mockContext, "com.package")

        assertEquals(120, result)
        Mockito.verify(mockUtilService).yesterdayMidnightMillis()
        Mockito.verify(mockUtilService).todayMidnightMillis()
        Mockito.verify(mockUtilService).getUsageInMinutesByPackage(
            mockContext, "com.package", yesterdayMillis, todayMillis
        )
    }

    // ========== getWeeklyDetails tests ==========

    @Test
    fun `getWeeklyDetails should return map with 7 day entries`() {
        val mockContext = Mockito.mock(Context::class.java)
        `when`(mockUtilService.getMidnight(Mockito.any())).thenReturn(1000L)
        `when`(
            mockUtilService.getUsageInMinutesByPackage(
                Mockito.any(Context::class.java), anyString(), anyLong(), anyLong()
            )
        ).thenReturn(10)

        val result = historyService.getWeeklyDetails(mockContext, "com.test")

        assertEquals(7, result.size)
    }

    @Test
    fun `getWeeklyDetails should contain all weekday keys`() {
        val mockContext = Mockito.mock(Context::class.java)
        `when`(mockUtilService.getMidnight(Mockito.any())).thenReturn(1000L)
        `when`(
            mockUtilService.getUsageInMinutesByPackage(
                Mockito.any(Context::class.java), anyString(), anyLong(), anyLong()
            )
        ).thenReturn(0)

        val result = historyService.getWeeklyDetails(mockContext, "com.test")

        assertTrue(result.containsKey("MONDAY"))
        assertTrue(result.containsKey("TUESDAY"))
        assertTrue(result.containsKey("WEDNESDAY"))
        assertTrue(result.containsKey("THURSDAY"))
        assertTrue(result.containsKey("FRIDAY"))
        assertTrue(result.containsKey("SATURDAY"))
        assertTrue(result.containsKey("SUNDAY"))
    }

    @Test
    fun `getWeeklyDetails should return correct usage values`() {
        val mockContext = Mockito.mock(Context::class.java)
        `when`(mockUtilService.getMidnight(Mockito.any())).thenReturn(1000L)
        `when`(
            mockUtilService.getUsageInMinutesByPackage(
                Mockito.any(Context::class.java), anyString(), anyLong(), anyLong()
            )
        ).thenReturn(30)

        val result = historyService.getWeeklyDetails(mockContext, "com.test")

        result.values.forEach { usage ->
            assertEquals(30, usage)
        }
    }
}

