package it.carmelolagamba.saveyourtime.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.carmelolagamba.saveyourtime.persistence.dao.AppDao
import it.carmelolagamba.saveyourtime.persistence.dao.EventDao
import it.carmelolagamba.saveyourtime.persistence.dao.PreferencesDao

/**
 * @author carmelolg
 * @since version 1.0
 */
@Database(entities = [App::class, Event::class, Preferences::class], version = 1)
abstract class DBFactory : RoomDatabase() {
    abstract fun applicationDao(): AppDao

    abstract fun eventDao(): EventDao

    abstract fun preferencesDao(): PreferencesDao

    companion object {
        @Volatile
        private var Instance: DBFactory? = null

        fun getDatabase(context: Context): DBFactory {

            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, DBFactory::class.java, "db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}
