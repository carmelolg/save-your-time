package it.carmelolagamba.saveyourtime.persistence.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import it.carmelolagamba.saveyourtime.persistence.Preferences
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesDaoTest {

    private lateinit var database: DBFactory
    private lateinit var preferencesDao: PreferencesDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, DBFactory::class.java)
            .allowMainThreadQueries()
            .build()
        preferencesDao = database.preferencesDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAll() {
        val pref = Preferences(null, "app_blocked", "true")
        preferencesDao.insertAll(pref)

        val result = preferencesDao.getAll()
        assertEquals(1, result.size)
        assertEquals("app_blocked", result[0].key)
        assertEquals("true", result[0].value)
    }

    @Test
    fun insertMultipleAndGetAll() {
        val pref1 = Preferences(null, "key1", "value1")
        val pref2 = Preferences(null, "key2", "value2")
        preferencesDao.insertAll(pref1, pref2)

        val result = preferencesDao.getAll()
        assertEquals(2, result.size)
    }

    @Test
    fun getByKey() {
        val pref = Preferences(null, "app_blocked", "true")
        preferencesDao.insertAll(pref)

        val result = preferencesDao.getByKey("app_blocked")
        assertNotNull(result)
        assertEquals("true", result.value)
    }

    @Test
    fun updatePreference() {
        val pref = Preferences(null, "app_blocked", "true")
        preferencesDao.insertAll(pref)

        val fetched = preferencesDao.getByKey("app_blocked")
        fetched.value = "false"
        preferencesDao.update(fetched)

        val updated = preferencesDao.getByKey("app_blocked")
        assertEquals("false", updated.value)
    }

    @Test
    fun deleteAll() {
        val pref1 = Preferences(null, "key1", "value1")
        val pref2 = Preferences(null, "key2", "value2")
        preferencesDao.insertAll(pref1, pref2)

        preferencesDao.deleteAll()
        val result = preferencesDao.getAll()
        assertTrue(result.isEmpty())
    }

    @Test
    fun autoGenerateId() {
        val pref1 = Preferences(null, "key1", "val1")
        val pref2 = Preferences(null, "key2", "val2")
        preferencesDao.insertAll(pref1, pref2)

        val all = preferencesDao.getAll()
        assertNotNull(all[0].id)
        assertNotNull(all[1].id)
        assertNotEquals(all[0].id, all[1].id)
    }

    @Test
    fun updateDoesNotAffectOtherRecords() {
        val pref1 = Preferences(null, "key1", "val1")
        val pref2 = Preferences(null, "key2", "val2")
        preferencesDao.insertAll(pref1, pref2)

        val fetched = preferencesDao.getByKey("key1")
        fetched.value = "updated"
        preferencesDao.update(fetched)

        val other = preferencesDao.getByKey("key2")
        assertEquals("val2", other.value)
    }

    @Test
    fun insertWithConflictReplaces() {
        val pref = Preferences(null, "key1", "val1")
        preferencesDao.insertAll(pref)

        val fetched = preferencesDao.getAll()[0]
        val replacement = Preferences(fetched.id, "key1", "replaced")
        preferencesDao.insertAll(replacement)

        val all = preferencesDao.getAll()
        assertEquals(1, all.size)
        assertEquals("replaced", all[0].value)
    }
}

