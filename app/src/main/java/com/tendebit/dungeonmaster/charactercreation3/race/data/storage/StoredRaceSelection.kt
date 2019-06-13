package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "race_selection")
data class StoredRaceSelection(@PrimaryKey val id: String)