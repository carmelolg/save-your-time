package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.persistence.App
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AppServiceTest {

    private lateinit var appService: AppService

    @Before
    fun setUp() {
        appService = AppService()
    }

    // ========== findPackageByName tests ==========

    @Test
    fun `findPackageByName should return package when name exists`() {
        val apps = listOf(
            App("Instagram", "com.instagram.android", true, 60, 30, 100L),
            App("Facebook", "com.facebook.katana", true, 45, 20, 200L)
        )

        val result = appService.findPackageByName("Instagram", apps)
        assertEquals("com.instagram.android", result)
    }

    @Test
    fun `findPackageByName should return null when name does not exist`() {
        val apps = listOf(
            App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        )

        val result = appService.findPackageByName("NonExistent", apps)
        assertNull(result)
    }

    @Test
    fun `findPackageByName should return null for empty list`() {
        val result = appService.findPackageByName("Test", emptyList())
        assertNull(result)
    }

    @Test
    fun `findPackageByName should return first match when multiple exist`() {
        val apps = listOf(
            App("TestApp", "com.test1", true, 60, 30, 100L),
            App("TestApp", "com.test2", true, 45, 20, 200L)
        )

        val result = appService.findPackageByName("TestApp", apps)
        assertEquals("com.test1", result)
    }

    @Test
    fun `findPackageByName is case sensitive`() {
        val apps = listOf(
            App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        )

        val result = appService.findPackageByName("instagram", apps)
        assertNull(result)
    }

    // ========== findExceededApplication tests ==========

    @Test
    fun `findExceededApplication should return apps that exceeded time`() {
        val apps = listOf(
            App("App1", "com.app1", true, 60, 70, 100L),  // exceeded
            App("App2", "com.app2", true, 45, 20, 200L),  // not exceeded
            App("App3", "com.app3", true, 30, 30, 300L)   // exactly at limit - exceeded
        )

        val result = appService.findExceededApplication(apps)
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "App1" })
        assertTrue(result.any { it.name == "App3" })
    }

    @Test
    fun `findExceededApplication should return empty when no app exceeded`() {
        val apps = listOf(
            App("App1", "com.app1", true, 60, 30, 100L),
            App("App2", "com.app2", true, 45, 20, 200L)
        )

        val result = appService.findExceededApplication(apps)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findExceededApplication should return empty for empty list`() {
        val result = appService.findExceededApplication(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findExceededApplication should return all when all exceeded`() {
        val apps = listOf(
            App("App1", "com.app1", true, 10, 20, 100L),
            App("App2", "com.app2", true, 5, 10, 200L)
        )

        val result = appService.findExceededApplication(apps)
        assertEquals(2, result.size)
    }

    @Test
    fun `findExceededApplication should include apps at exactly the limit`() {
        val apps = listOf(
            App("App1", "com.app1", true, 60, 60, 100L)
        )

        val result = appService.findExceededApplication(apps)
        assertEquals(1, result.size)
        assertEquals("App1", result[0].name)
    }

    @Test
    fun `findExceededApplication should not include apps just below limit`() {
        val apps = listOf(
            App("App1", "com.app1", true, 60, 59, 100L)
        )

        val result = appService.findExceededApplication(apps)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `findExceededApplication with zero limit and zero usage`() {
        val apps = listOf(
            App("App1", "com.app1", true, 0, 0, 100L)
        )

        val result = appService.findExceededApplication(apps)
        assertEquals(1, result.size) // 0 >= 0 is true
    }

    @Test
    fun `findExceededApplication with large values`() {
        val apps = listOf(
            App("App1", "com.app1", true, 1000, 1001, 100L)
        )

        val result = appService.findExceededApplication(apps)
        assertEquals(1, result.size)
    }
}

