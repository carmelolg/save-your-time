package it.carmelolagamba.saveyourtime.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author carmelolg
 * @since version 1.0
 */
@Entity(tableName = "application")
data class App(
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey @ColumnInfo(name = "package") val packageName: String,
    @ColumnInfo(name = "selected") var selected: Boolean,
    @ColumnInfo(name = "time") var notifyTime: Int,
    @ColumnInfo(name = "timeToday") var todayUsage: Int,
    @ColumnInfo(name = "lastUpdate") var lastUpdate: Long
    )