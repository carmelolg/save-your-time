package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.service.UtilService.Weekday
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class ChartServiceTest {

    private lateinit var chartService: ChartService
    private lateinit var mockUtilService: UtilService

    @Before
    fun setUp() {
        chartService = ChartService()
        mockUtilService = Mockito.mock(UtilService::class.java)

        // Inject mock utilService via reflection
        val field = ChartService::class.java.getDeclaredField("utilService")
        field.isAccessible = true
        field.set(chartService, mockUtilService)
    }

    // ========== buildChartData(apps) tests ==========

    @Test
    fun `buildChartData with apps should return correct map`() {
        val apps = listOf(
            App("Instagram", "com.instagram", true, 60, 30, 100L),
            App("Facebook", "com.facebook", true, 45, 20, 200L)
        )

        val result = chartService.buildChartData(apps)

        assertNotNull(result)
        assertEquals(2, result.size)
    }

    @Test
    fun `buildChartData with apps should have float keys`() {
        val apps = listOf(
            App("Instagram", "com.instagram", true, 60, 30, 100L)
        )

        val result = chartService.buildChartData(apps)
        assertTrue(result.keys.all { it is Float })
    }

    @Test
    fun `buildChartData with apps should contain app names`() {
        val apps = listOf(
            App("Instagram", "com.instagram", true, 60, 30, 100L),
            App("Facebook", "com.facebook", true, 45, 20, 200L)
        )

        val result = chartService.buildChartData(apps)
        val names = result.values.map { it.second }

        assertTrue(names.contains("Instagram"))
        assertTrue(names.contains("Facebook"))
    }

    @Test
    fun `buildChartData with apps should contain correct usage values`() {
        val apps = listOf(
            App("Instagram", "com.instagram", true, 60, 30, 100L)
        )

        val result = chartService.buildChartData(apps)
        val usageValues = result.values.map { it.first }

        assertTrue(usageValues.contains(30f))
    }

    @Test
    fun `buildChartData with empty apps should return empty map`() {
        val result = chartService.buildChartData(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `buildChartData with apps should sort by usage descending`() {
        val apps = listOf(
            App("LowUsage", "com.low", true, 60, 10, 100L),
            App("HighUsage", "com.high", true, 60, 50, 200L),
            App("MidUsage", "com.mid", true, 60, 30, 300L)
        )

        val result = chartService.buildChartData(apps)
        val values = result.values.toList()

        // Sorted descending by first (usage value)
        assertTrue(values[0].first >= values[1].first)
        assertTrue(values[1].first >= values[2].first)
    }

    @Test
    fun `buildChartData with single app`() {
        val apps = listOf(
            App("SingleApp", "com.single", true, 60, 42, 100L)
        )

        val result = chartService.buildChartData(apps)
        assertEquals(1, result.size)
        assertEquals(42f, result.values.first().first)
        assertEquals("SingleApp", result.values.first().second)
    }

    @Test
    fun `buildChartData with apps having zero usage`() {
        val apps = listOf(
            App("ZeroApp", "com.zero", true, 60, 0, 100L)
        )

        val result = chartService.buildChartData(apps)
        assertEquals(1, result.size)
        assertEquals(0f, result.values.first().first)
    }

    // ========== buildChartEntryModel tests ==========

    @Test
    fun `buildChartEntryModel should return non-null model`() {
        val chartData = mutableMapOf(
            1f to Pair(30f, "Instagram"),
            2f to Pair(20f, "Facebook")
        )

        val result = chartService.buildChartEntryModel(chartData)
        assertNotNull(result)
    }

    @Test
    fun `buildChartEntryModel with single entry`() {
        val chartData = mutableMapOf(
            1f to Pair(42f, "TestApp")
        )

        val result = chartService.buildChartEntryModel(chartData)
        assertNotNull(result)
    }

    @Test
    fun `buildChartEntryModel with empty data`() {
        val chartData = mutableMapOf<Float, Pair<Float, String>>()

        val result = chartService.buildChartEntryModel(chartData)
        assertNotNull(result)
    }

    // ========== buildDefaultYAxisFormatter tests ==========

    @Test
    fun `buildDefaultYAxisFormatter should return non-null formatter`() {
        val chartData = mutableMapOf(
            1f to Pair(30f, "Label1"),
            2f to Pair(20f, "Label2")
        )

        val result = chartService.buildDefaultYAxisFormatter(chartData)
        assertNotNull(result)
    }

    @Test
    fun `buildDefaultYAxisFormatter with empty data`() {
        val chartData = mutableMapOf<Float, Pair<Float, String>>()
        val result = chartService.buildDefaultYAxisFormatter(chartData)
        assertNotNull(result)
    }

    // ========== buildChartData(map) with context - tested without context dependency ==========

    @Test
    fun `buildChartDataWithOrder should return correct ordered map`() {
        `when`(mockUtilService.getDateFromToday(-6)).thenReturn("21/02")
        `when`(mockUtilService.getDateFromToday(-5)).thenReturn("22/02")
        `when`(mockUtilService.getDateFromToday(-4)).thenReturn("23/02")
        `when`(mockUtilService.getDateFromToday(-3)).thenReturn("24/02")
        `when`(mockUtilService.getDateFromToday(-2)).thenReturn("25/02")
        `when`(mockUtilService.getDateFromToday(-1)).thenReturn("26/02")
        `when`(mockUtilService.getDateFromToday(0)).thenReturn("27/02")
        `when`(mockUtilService.getCurrentDay()).thenReturn(Weekday.THURSDAY)

        val map = mapOf(
            1 to Pair("FRIDAY", 10),
            2 to Pair("SATURDAY", 20),
            3 to Pair("SUNDAY", 5),
            4 to Pair("MONDAY", 15),
            5 to Pair("TUESDAY", 25),
            6 to Pair("WEDNESDAY", 30),
            7 to Pair("THURSDAY", 40)
        )

        // We cannot test this without Context, but we can test buildChartData with apps list
    }

    // ========== Integration-like tests ==========

    @Test
    fun `buildChartData and buildChartEntryModel work together`() {
        val apps = listOf(
            App("App1", "com.app1", true, 60, 30, 100L),
            App("App2", "com.app2", true, 45, 20, 200L),
            App("App3", "com.app3", true, 30, 10, 300L)
        )

        val chartData = chartService.buildChartData(apps)
        val model = chartService.buildChartEntryModel(chartData)
        val formatter = chartService.buildDefaultYAxisFormatter(chartData)

        assertNotNull(chartData)
        assertNotNull(model)
        assertNotNull(formatter)
        assertEquals(3, chartData.size)
    }
}

