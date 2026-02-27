package it.carmelolagamba.saveyourtime.service

import org.junit.Assert.*
import org.junit.Test

class PreferencesServiceTest {

    // Test companion object constants

    @Test
    fun `BLOCK_APP constant should have correct value`() {
        assertEquals("app_blocked", PreferencesService.BLOCK_APP)
    }

    @Test
    fun `REMINDER_EXPIRED_CHECK constant should have correct value`() {
        assertEquals("app_expired_reminder_check", PreferencesService.REMINDER_EXPIRED_CHECK)
    }

    @Test
    fun `REMINDER_EXPIRED_TIME constant should have correct value`() {
        assertEquals("app_expired_reminder_time", PreferencesService.REMINDER_EXPIRED_TIME)
    }

    @Test
    fun `BLOCK_APP should not be empty`() {
        assertTrue(PreferencesService.BLOCK_APP.isNotEmpty())
    }

    @Test
    fun `REMINDER_EXPIRED_CHECK should not be empty`() {
        assertTrue(PreferencesService.REMINDER_EXPIRED_CHECK.isNotEmpty())
    }

    @Test
    fun `REMINDER_EXPIRED_TIME should not be empty`() {
        assertTrue(PreferencesService.REMINDER_EXPIRED_TIME.isNotEmpty())
    }

    @Test
    fun `constants should be distinct`() {
        val constants = setOf(
            PreferencesService.BLOCK_APP,
            PreferencesService.REMINDER_EXPIRED_CHECK,
            PreferencesService.REMINDER_EXPIRED_TIME
        )
        assertEquals(3, constants.size)
    }
}

