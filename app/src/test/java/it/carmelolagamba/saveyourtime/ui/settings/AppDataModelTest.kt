package it.carmelolagamba.saveyourtime.ui.settings

import org.junit.Assert.*
import org.junit.Test

class AppDataModelTest {

    @Test
    fun `AppDataModel should store correct values`() {
        val model = AppDataModel(null, "Instagram", "com.instagram.android", true, 60, 30, 100L)

        assertNull(model.icon)
        assertEquals("Instagram", model.name)
        assertEquals("com.instagram.android", model.packageName)
        assertTrue(model.checked)
        assertEquals(60, model.notifyTime)
        assertEquals(30, model.todayUsage)
        assertEquals(100L, model.lastUpdate)
    }

    @Test
    fun `AppDataModel mutable fields should be updatable`() {
        val model = AppDataModel(null, "Test", "com.test", false, 30, 0, 0L)

        model.name = "Updated"
        model.checked = true
        model.notifyTime = 120
        model.todayUsage = 50
        model.lastUpdate = 9999L

        assertEquals("Updated", model.name)
        assertTrue(model.checked)
        assertEquals(120, model.notifyTime)
        assertEquals(50, model.todayUsage)
        assertEquals(9999L, model.lastUpdate)
    }

    @Test
    fun `AppDataModel with null name`() {
        val model = AppDataModel(null, null, "com.test", false, 60, 0, 0L)
        assertNull(model.name)
    }

    @Test
    fun `AppDataModel with zero values`() {
        val model = AppDataModel(null, "Test", "com.test", false, 0, 0, 0L)
        assertEquals(0, model.notifyTime)
        assertEquals(0, model.todayUsage)
        assertEquals(0L, model.lastUpdate)
    }

    @Test
    fun `AppDataModel packageName should not be empty`() {
        val model = AppDataModel(null, "Test", "com.test.app", true, 60, 30, 100L)
        assertTrue(model.packageName.isNotEmpty())
    }

    @Test
    fun `AppDataModel checked toggle`() {
        val model = AppDataModel(null, "Test", "com.test", false, 60, 30, 100L)
        assertFalse(model.checked)
        model.checked = true
        assertTrue(model.checked)
        model.checked = false
        assertFalse(model.checked)
    }

    @Test
    fun `AppDataModel icon can be null`() {
        val model = AppDataModel(null, "Test", "com.test", true, 60, 0, 0L)
        assertNull(model.icon)
    }

    @Test
    fun `AppDataModel with large notifyTime`() {
        val model = AppDataModel(null, "Test", "com.test", true, 1440, 0, 0L)
        assertEquals(1440, model.notifyTime)
    }

    @Test
    fun `AppDataModel todayUsage exceeding notifyTime`() {
        val model = AppDataModel(null, "Test", "com.test", true, 60, 90, 0L)
        assertTrue(model.todayUsage > model.notifyTime)
    }
}

