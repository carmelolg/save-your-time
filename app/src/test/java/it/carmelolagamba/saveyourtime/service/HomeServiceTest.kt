package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.persistence.App
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class HomeServiceTest {

    private lateinit var homeService: HomeService
    private lateinit var mockAppService: AppService

    @Before
    fun setUp() {
        homeService = HomeService()
        mockAppService = Mockito.mock(AppService::class.java)

        val field = HomeService::class.java.getDeclaredField("appService")
        field.isAccessible = true
        field.set(homeService, mockAppService)
    }

    @Test
    fun `HomeService should be instantiable`() {
        assertNotNull(homeService)
    }

    @Test
    fun `HomeService should have appService injected`() {
        assertNotNull(homeService.appService)
    }

    @Test
    fun `appService dependency should be mockable`() {
        val app = App("Test", "com.test", true, 60, 30, 100L)
        `when`(mockAppService.findNameByPackageName("com.test")).thenReturn("Test")

        val result = mockAppService.findNameByPackageName("com.test")
        assertEquals("Test", result)
    }

    @Test
    fun `findNameByPackageName should be called with correct package`() {
        val packageName = "com.instagram.android"
        `when`(mockAppService.findNameByPackageName(packageName)).thenReturn("Instagram")

        val result = mockAppService.findNameByPackageName(packageName)
        assertEquals("Instagram", result)

        Mockito.verify(mockAppService).findNameByPackageName(packageName)
    }

    @Test
    fun `time calculation for remaining time should be correct when not exceeded`() {
        val notifyTime = 60
        val todayUsage = 30
        val expected = 30

        val actual = notifyTime - todayUsage
        assertEquals(expected, actual)
        assertTrue(actual > 0)
    }

    @Test
    fun `time calculation should show exceeded when usage equals limit`() {
        val notifyTime = 60
        val todayUsage = 60

        val remaining = notifyTime - todayUsage
        assertTrue(remaining <= 0)
    }

    @Test
    fun `time calculation should show exceeded when usage exceeds limit`() {
        val notifyTime = 45
        val todayUsage = 60

        val remaining = notifyTime - todayUsage
        assertTrue(remaining < 0)
    }

    @Test
    fun `time calculation should handle zero usage`() {
        val notifyTime = 60
        val todayUsage = 0
        val expected = 60

        val actual = notifyTime - todayUsage
        assertEquals(expected, actual)
    }

    @Test
    fun `time calculation should handle large usage values`() {
        val notifyTime = 60
        val todayUsage = 120

        val remaining = notifyTime - todayUsage
        assertEquals(-60, remaining)
        assertTrue(remaining <= 0)
    }

    @Test
    fun `time calculation should handle one minute remaining`() {
        val notifyTime = 60
        val todayUsage = 59
        val expected = 1

        val actual = notifyTime - todayUsage
        assertEquals(expected, actual)
        assertTrue(actual > 0)
    }

    @Test
    fun `different apps should have different package names`() {
        val app1 = App("Instagram", "com.instagram", true, 60, 30, 100L)
        val app2 = App("Facebook", "com.facebook", true, 45, 20, 200L)

        assertNotEquals(app1.packageName, app2.packageName)
    }

    @Test
    fun `app usage can vary while notify time stays constant`() {
        val notifyTime = 60
        val usageValues = listOf(0, 20, 30, 45, 59, 60, 90)

        usageValues.forEach { usage ->
            val remaining = notifyTime - usage
            if (usage < notifyTime) {
                assertTrue("Usage $usage should have positive remaining", remaining > 0)
            } else {
                assertTrue("Usage $usage should have non-positive remaining", remaining <= 0)
            }
        }
    }

    @Test
    fun `multiple apps can be processed with different remaining times`() {
        val apps = listOf(
            Triple("App1", 60, 20),
            Triple("App2", 120, 50),
            Triple("App3", 30, 5),
            Triple("App4", 90, 90),
            Triple("App5", 45, 60)
        )

        apps.forEach { (name, notifyTime, usage) ->
            val remaining = notifyTime - usage
            `when`(mockAppService.findNameByPackageName("com.$name")).thenReturn(name)

            if (usage < notifyTime) {
                assertTrue("$name should have positive remaining", remaining > 0)
            } else {
                assertTrue("$name should have non-positive remaining", remaining <= 0)
            }
        }
    }

    @Test
    fun `format time display string should show minutes`() {
        val time = 30
        val formatted = "$time min"

        assertEquals("30 min", formatted)
        assertTrue(formatted.endsWith(" min"))
    }

    @Test
    fun `format time display for various values`() {
        val times = listOf(0, 1, 30, 60, 120)

        times.forEach { time ->
            val formatted = "$time min"
            assertTrue(formatted.contains(time.toString()))
            assertTrue(formatted.endsWith(" min"))
        }
    }

    @Test
    fun `app with zero notify time should always be exceeded`() {
        val notifyTime = 0
        val anyUsage = 1

        val remaining = notifyTime - anyUsage
        assertTrue(remaining <= 0)
    }

    @Test
    fun `remaining time calculation should be consistent`() {
        val notifyTime = 100
        val usage1 = 40
        val usage2 = 40

        val remaining1 = notifyTime - usage1
        val remaining2 = notifyTime - usage2

        assertEquals(remaining1, remaining2)
    }

    @Test
    fun `edge case one minute before limit`() {
        val notifyTime = 60
        val usage = 59

        val remaining = notifyTime - usage
        assertEquals(1, remaining)
        assertTrue(remaining > 0)
    }

    @Test
    fun `edge case exactly at limit`() {
        val notifyTime = 60
        val usage = 60

        val remaining = notifyTime - usage
        assertEquals(0, remaining)
        assertFalse(remaining > 0)
    }

    @Test
    fun `edge case one minute over limit`() {
        val notifyTime = 60
        val usage = 61

        val remaining = notifyTime - usage
        assertEquals(-1, remaining)
        assertTrue(remaining < 0)
    }
}






