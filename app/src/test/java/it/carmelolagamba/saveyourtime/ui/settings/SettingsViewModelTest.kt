package it.carmelolagamba.saveyourtime.ui.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel()
    }

    @Test
    fun `SettingsViewModel should be instantiated`() {
        assertNotNull(viewModel)
    }

    @Test
    fun `SettingsViewModel is instance of ViewModel`() {
        assertTrue(viewModel is androidx.lifecycle.ViewModel)
    }
}

