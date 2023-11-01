package it.carmelolagamba.saveyourtime.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "application")
data class App (
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey @ColumnInfo(name = "package") val packageName: String,
    @ColumnInfo(name = "selected") val selected: Boolean
) {

    override fun toString(): String {
        return "NAME: $name LASTNAME: $packageName SELECTED $selected"
    }

}