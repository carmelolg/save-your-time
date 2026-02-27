package it.carmelolagamba.saveyourtime.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DBFactoryTest {

    private lateinit var database: DBFactory

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DBFactory::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun databaseShouldProvideAppDao() {
        assertNotNull(database.applicationDao())
    }

    @Test
    fun databaseShouldProvideEventDao() {
        assertNotNull(database.eventDao())
    }

    @Test
    fun databaseShouldProvidePreferencesDao() {
        assertNotNull(database.preferencesDao())
    }

    @Test
    fun allDaosShouldWorkTogether() {
        // Insert app
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        database.applicationDao().insertAll(app)

        // Insert event linked to app
        val event = Event(null, "check", "com.instagram.android", System.currentTimeMillis(), 30, false)
        database.eventDao().insertAll(event)

        // Insert preference
        val pref = Preferences(null, "app_blocked", "true")
        database.preferencesDao().insertAll(pref)

        // Verify all data
        assertEquals(1, database.applicationDao().getAll().size)
        assertEquals(1, database.eventDao().getAll().size)
        assertEquals(1, database.preferencesDao().getAll().size)
    }

    @Test
    fun deleteAllFromAllTables() {
        database.applicationDao().insertAll(App("Test", "com.test", true, 60, 0, 0L))
        database.eventDao().insertAll(Event(null, "check", "com.test", 100L, 0, false))
        database.preferencesDao().insertAll(Preferences(null, "key", "val"))

        database.applicationDao().deleteAll()
        database.eventDao().deleteAll()
        database.preferencesDao().deleteAll()

        assertTrue(database.applicationDao().getAll().isEmpty())
        assertTrue(database.eventDao().getAll().isEmpty())
        assertTrue(database.preferencesDao().getAll().isEmpty())
    }

    @Test
    fun databaseIsOpenAfterCreation() {
        assertTrue(database.isOpen)
    }

    @Test
    fun databaseClosesCorrectly() {
        database.close()
        assertFalse(database.isOpen)
        // Reopen for tearDown
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DBFactory::class.java)
            .allowMainThreadQueries()
            .build()
    }
}

