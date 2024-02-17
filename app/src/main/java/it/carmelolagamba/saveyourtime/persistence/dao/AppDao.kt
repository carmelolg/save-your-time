package it.carmelolagamba.saveyourtime.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import it.carmelolagamba.saveyourtime.persistence.App

/**
 * @author carmelolg
 * @since version 1.0
 */
@Dao
interface AppDao {
    @Query("SELECT * FROM application")
    fun getAll(): List<App>

    @Query("SELECT * FROM application WHERE selected = 1")
    fun getAllActive(): List<App>

    @Query("SELECT * FROM application WHERE package = :packageName")
    fun getByPackageName(packageName: String): App

    @Query("SELECT * FROM application WHERE name = :name")
    fun getByName(name: String): App

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg apps: App)

    @Query("DELETE FROM application")
    fun deleteAll()

    @Update
    fun update(app: App)

}
