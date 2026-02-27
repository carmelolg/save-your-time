package it.carmelolagamba.saveyourtime.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        viewModel = HomeViewModel()
    }

    @Test
    fun `text LiveData should have initial value`() {
        val value = viewModel.text.value
        assertEquals("This is home Fragment", value)
    }

    @Test
    fun `text LiveData should not be null`() {
        assertNotNull(viewModel.text.value)
    }

    @Test
    fun `text LiveData should be observable`() {
        assertNotNull(viewModel.text)
    }
}

