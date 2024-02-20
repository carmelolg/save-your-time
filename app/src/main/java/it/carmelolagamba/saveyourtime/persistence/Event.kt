package it.carmelolagamba.saveyourtime.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author carmelolg
 * @since version 1.0
 */
@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "appId") val appId: String,
    @ColumnInfo(name = "insertDate") var insertDate: Long,
    @ColumnInfo(name = "notified") var notified: Boolean
    )
