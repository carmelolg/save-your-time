package it.carmelolagamba.saveyourtime.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [App::class], version = 1)
abstract class DBFactory : RoomDatabase() {
    abstract fun applicationDao(): AppDao

    companion object {
        @Volatile
        private var Instance: DBFactory? = null

        fun getDatabase(context: Context): it.carmelolagamba.saveyourtime.persistence.DBFactory {
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
