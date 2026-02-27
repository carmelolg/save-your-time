package it.carmelolagamba.saveyourtime.persistence

import org.junit.Assert.*
import org.junit.Test

class AppTest {

    @Test
    fun `App data class should store correct values`() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 1234567890L)

        assertEquals("Instagram", app.name)
        assertEquals("com.instagram.android", app.packageName)
        assertTrue(app.selected)
        assertEquals(60, app.notifyTime)
        assertEquals(30, app.todayUsage)
        assertEquals(1234567890L, app.lastUpdate)
    }

    @Test
    fun `App data class should allow mutable fields to be updated`() {
        val app = App("Instagram", "com.instagram.android", false, 30, 0, 0L)

        app.selected = true
        app.notifyTime = 120
        app.todayUsage = 45
        app.lastUpdate = 9999999L

        assertTrue(app.selected)
        assertEquals(120, app.notifyTime)
        assertEquals(45, app.todayUsage)
        assertEquals(9999999L, app.lastUpdate)
    }

    @Test
    fun `App data class equality should be based on packageName (primary key)`() {
        val app1 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        val app2 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)

        assertEquals(app1, app2)
    }

    @Test
    fun `App data class copy should work correctly`() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        val copy = app.copy(name = "Instagram Copy")

        assertEquals("Instagram Copy", copy.name)
        assertEquals(app.packageName, copy.packageName)
        assertEquals(app.selected, copy.selected)
    }

    @Test
    fun `App data class should have correct toString`() {
        val app = App("Test", "com.test", false, 0, 0, 0L)
        val str = app.toString()
        assertTrue(str.contains("Test"))
        assertTrue(str.contains("com.test"))
    }

    @Test
    fun `App data class with different values should not be equal`() {
        val app1 = App("App1", "com.app1", true, 60, 30, 100L)
        val app2 = App("App2", "com.app2", false, 30, 0, 200L)

        assertNotEquals(app1, app2)
    }

    @Test
    fun `App hashCode should be equal for equal objects`() {
        val app1 = App("Instagram", "com.instagram", true, 60, 30, 100L)
        val app2 = App("Instagram", "com.instagram", true, 60, 30, 100L)

        assertEquals(app1.hashCode(), app2.hashCode())
    }

    @Test
    fun `App with zero todayUsage`() {
        val app = App("Test", "com.test", true, 60, 0, 0L)
        assertEquals(0, app.todayUsage)
    }

    @Test
    fun `App with large notifyTime`() {
        val app = App("Test", "com.test", true, Int.MAX_VALUE, 0, 0L)
        assertEquals(Int.MAX_VALUE, app.notifyTime)
    }
}

