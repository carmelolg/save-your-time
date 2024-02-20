package it.carmelolagamba.saveyourtime.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.carmelolagamba.saveyourtime.persistence.Event

/**
 * @author carmelolg
 * @since version 1.0
 */
@Dao
interface EventDao {
    @Query("SELECT * FROM event")
    fun getAll(): List<Event>

    @Query("SELECT * FROM event WHERE id = :id")
    fun getById(id: Int): Event

    @Query("SELECT * FROM event WHERE appId = :appId")
    fun getByAppId(appId: String): List<Event>

    @Query("SELECT * FROM event WHERE insertDate >= :lastMidnight")
    fun getAllActive(lastMidnight: Long): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg events: Event)

    @Query("DELETE FROM event")
    fun deleteAll()

    @Query("DELETE FROM event WHERE insertDate < :lastMidnight")
    fun cleanDB(lastMidnight: Long)

    @Update
    fun update(event: Event)

}
