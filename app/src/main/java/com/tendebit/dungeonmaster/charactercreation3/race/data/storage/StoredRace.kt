package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "races")
data class StoredRace(
		@PrimaryKey val id: String,
		@ColumnInfo(name = "race_name") var name: String)