package it.carmelolagamba.saveyourtime.persistence

import org.junit.Assert.*
import org.junit.Test

class PreferencesTest {

    @Test
    fun `Preferences data class should store correct values`() {
        val preferences = Preferences(1, "app_blocked", "true")

        assertEquals(1, preferences.id)
        assertEquals("app_blocked", preferences.key)
        assertEquals("true", preferences.value)
    }

    @Test
    fun `Preferences with null id should work`() {
        val preferences = Preferences(null, "test_key", "test_value")
        assertNull(preferences.id)
    }

    @Test
    fun `Preferences value should be mutable`() {
        val preferences = Preferences(1, "test_key", "old_value")
        preferences.value = "new_value"

        assertEquals("new_value", preferences.value)
    }

    @Test
    fun `Preferences data class equality`() {
        val pref1 = Preferences(1, "key", "value")
        val pref2 = Preferences(1, "key", "value")

        assertEquals(pref1, pref2)
    }

    @Test
    fun `Preferences data class inequality`() {
        val pref1 = Preferences(1, "key1", "value1")
        val pref2 = Preferences(2, "key2", "value2")

        assertNotEquals(pref1, pref2)
    }

    @Test
    fun `Preferences copy should work correctly`() {
        val pref = Preferences(1, "key", "value")
        val copy = pref.copy(value = "updated")

        assertEquals("updated", copy.value)
        assertEquals(pref.id, copy.id)
        assertEquals(pref.key, copy.key)
    }

    @Test
    fun `Preferences hashCode should be equal for equal objects`() {
        val pref1 = Preferences(1, "key", "val")
        val pref2 = Preferences(1, "key", "val")

        assertEquals(pref1.hashCode(), pref2.hashCode())
    }

    @Test
    fun `Preferences toString should contain relevant info`() {
        val pref = Preferences(5, "my_key", "my_value")
        val str = pref.toString()
        assertTrue(str.contains("my_key"))
        assertTrue(str.contains("my_value"))
    }

    @Test
    fun `Preferences with empty strings`() {
        val pref = Preferences(1, "", "")
        assertEquals("", pref.key)
        assertEquals("", pref.value)
    }

    @Test
    fun `Preferences with boolean value`() {
        val pref = Preferences(null, "app_blocked", "true")
        assertTrue(pref.value.toBoolean())

        pref.value = "false"
        assertFalse(pref.value.toBoolean())
    }

    @Test
    fun `Preferences with numeric value`() {
        val pref = Preferences(null, "reminder_time", "30")
        assertEquals(30, pref.value.toInt())
    }
}

