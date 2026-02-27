package it.carmelolagamba.saveyourtime.persistence.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDaoTest {

    private lateinit var database: DBFactory
    private lateinit var appDao: AppDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DBFactory::class.java)
            .allowMainThreadQueries()
            .build()
        appDao = database.applicationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAll() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        appDao.insertAll(app)

        val result = appDao.getAll()
        assertEquals(1, result.size)
        assertEquals("Instagram", result[0].name)
    }

    @Test
    fun insertMultipleAndGetAll() {
        val app1 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        val app2 = App("Facebook", "com.facebook.katana", false, 45, 20, 200L)
        appDao.insertAll(app1, app2)

        val result = appDao.getAll()
        assertEquals(2, result.size)
    }

    @Test
    fun getAllActive() {
        val app1 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        val app2 = App("Facebook", "com.facebook.katana", false, 45, 20, 200L)
        val app3 = App("Twitter", "com.twitter.android", true, 30, 10, 300L)
        appDao.insertAll(app1, app2, app3)

        val result = appDao.getAllActive()
        assertEquals(2, result.size)
        assertTrue(result.all { it.selected })
    }

    @Test
    fun getAllUnchecked() {
        val app1 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        val app2 = App("Facebook", "com.facebook.katana", false, 45, 20, 200L)
        appDao.insertAll(app1, app2)

        val result = appDao.getAllUnchecked()
        assertEquals(1, result.size)
        assertFalse(result[0].selected)
    }

    @Test
    fun getByPackageName() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        appDao.insertAll(app)

        val result = appDao.getByPackageName("com.instagram.android")
        assertNotNull(result)
        assertEquals("Instagram", result.name)
    }

    @Test
    fun getByName() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        appDao.insertAll(app)

        val result = appDao.getByName("Instagram")
        assertNotNull(result)
        assertEquals("com.instagram.android", result.packageName)
    }

    @Test
    fun updateApp() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        appDao.insertAll(app)

        val fetched = appDao.getByPackageName("com.instagram.android")
        fetched.notifyTime = 120
        fetched.todayUsage = 50
        appDao.update(fetched)

        val updated = appDao.getByPackageName("com.instagram.android")
        assertEquals(120, updated.notifyTime)
        assertEquals(50, updated.todayUsage)
    }

    @Test
    fun deleteAll() {
        val app1 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        val app2 = App("Facebook", "com.facebook.katana", false, 45, 20, 200L)
        appDao.insertAll(app1, app2)

        appDao.deleteAll()
        val result = appDao.getAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun insertWithConflictReplaces() {
        val app1 = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        appDao.insertAll(app1)

        val app2 = App("Instagram Updated", "com.instagram.android", false, 120, 0, 200L)
        appDao.insertAll(app2)

        val result = appDao.getAll()
        assertEquals(1, result.size)
        assertEquals("Instagram Updated", result[0].name)
    }

    @Test
    fun getAllActiveReturnsEmptyWhenNoActiveApps() {
        val app = App("Facebook", "com.facebook.katana", false, 45, 20, 200L)
        appDao.insertAll(app)

        val result = appDao.getAllActive()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllUncheckedReturnsEmptyWhenAllActive() {
        val app = App("Instagram", "com.instagram.android", true, 60, 30, 100L)
        appDao.insertAll(app)

        val result = appDao.getAllUnchecked()
        assertTrue(result.isEmpty())
    }

    @Test
    fun updateSelectedField() {
        val app = App("Instagram", "com.instagram.android", false, 60, 30, 100L)
        appDao.insertAll(app)

        val fetched = appDao.getByPackageName("com.instagram.android")
        fetched.selected = true
        appDao.update(fetched)

        val active = appDao.getAllActive()
        assertEquals(1, active.size)
    }
}

