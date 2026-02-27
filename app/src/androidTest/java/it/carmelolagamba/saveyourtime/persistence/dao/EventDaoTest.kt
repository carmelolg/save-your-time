package it.carmelolagamba.saveyourtime.persistence.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import it.carmelolagamba.saveyourtime.persistence.Event
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    private lateinit var database: DBFactory
    private lateinit var eventDao: EventDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DBFactory::class.java)
            .allowMainThreadQueries()
            .build()
        eventDao = database.eventDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAll() {
        val event = Event(null, "check_notify", "com.instagram.android", System.currentTimeMillis(), 30, false)
        eventDao.insertAll(event)

        val result = eventDao.getAll()
        assertEquals(1, result.size)
        assertEquals("check_notify", result[0].name)
    }

    @Test
    fun insertMultipleAndGetAll() {
        val event1 = Event(null, "check1", "com.app1", 100L, 10, false)
        val event2 = Event(null, "check2", "com.app2", 200L, 20, true)
        eventDao.insertAll(event1, event2)

        val result = eventDao.getAll()
        assertEquals(2, result.size)
    }

    @Test
    fun getById() {
        val event = Event(null, "check", "com.test", 100L, 10, false)
        eventDao.insertAll(event)

        val all = eventDao.getAll()
        val id = all[0].id!!
        val result = eventDao.getById(id)
        assertNotNull(result)
        assertEquals("check", result.name)
    }

    @Test
    fun getByAppId() {
        val event1 = Event(null, "check1", "com.app1", 100L, 10, false)
        val event2 = Event(null, "check2", "com.app1", 200L, 20, true)
        val event3 = Event(null, "check3", "com.app2", 300L, 30, false)
        eventDao.insertAll(event1, event2, event3)

        val result = eventDao.getByAppId("com.app1")
        assertEquals(2, result.size)
    }

    @Test
    fun getAllActive() {
        val now = System.currentTimeMillis()
        val event1 = Event(null, "old", "com.app1", now - 200000L, 10, false)
        val event2 = Event(null, "new", "com.app2", now, 20, true)
        eventDao.insertAll(event1, event2)

        val result = eventDao.getAllActive(now - 100000L)
        assertEquals(1, result.size)
        assertEquals("new", result[0].name)
    }

    @Test
    fun updateEvent() {
        val event = Event(null, "check", "com.test", 100L, 10, false)
        eventDao.insertAll(event)

        val fetched = eventDao.getAll()[0]
        fetched.notified = true
        fetched.usageAtEvent = 50
        eventDao.update(fetched)

        val updated = eventDao.getById(fetched.id!!)
        assertTrue(updated.notified)
        assertEquals(50, updated.usageAtEvent)
    }

    @Test
    fun deleteAll() {
        val event1 = Event(null, "check1", "com.app1", 100L, 10, false)
        val event2 = Event(null, "check2", "com.app2", 200L, 20, true)
        eventDao.insertAll(event1, event2)

        eventDao.deleteAll()
        val result = eventDao.getAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun cleanDB() {
        val now = System.currentTimeMillis()
        val event1 = Event(null, "old", "com.app1", now - 200000L, 10, false)
        val event2 = Event(null, "new", "com.app2", now, 20, true)
        eventDao.insertAll(event1, event2)

        eventDao.cleanDB(now - 100000L)
        val result = eventDao.getAll()
        assertEquals(1, result.size)
        assertEquals("new", result[0].name)
    }

    @Test
    fun getByAppIdReturnsEmptyForNonExistent() {
        val result = eventDao.getByAppId("com.nonexistent")
        assertTrue(result.isEmpty())
    }

    @Test
    fun autoGenerateId() {
        val event1 = Event(null, "check1", "com.app1", 100L, 10, false)
        val event2 = Event(null, "check2", "com.app2", 200L, 20, true)
        eventDao.insertAll(event1, event2)

        val all = eventDao.getAll()
        assertNotNull(all[0].id)
        assertNotNull(all[1].id)
        assertNotEquals(all[0].id, all[1].id)
    }
}

