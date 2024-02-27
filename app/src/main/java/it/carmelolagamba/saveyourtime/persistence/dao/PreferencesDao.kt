package it.carmelolagamba.saveyourtime.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.carmelolagamba.saveyourtime.persistence.Preferences

/**
 * @author carmelolg
 * @since version 1.0
 */
@Dao
interface PreferencesDao {
    @Query("SELECT * FROM preferences")
    fun getAll(): List<Preferences>

    @Query("SELECT * FROM preferences WHERE key = :key")
    fun getByKey(key: String): Preferences

    @Update
    fun update(preferences: Preferences)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg preferences: Preferences)

    @Query("DELETE FROM preferences")
    fun deleteAll()
}