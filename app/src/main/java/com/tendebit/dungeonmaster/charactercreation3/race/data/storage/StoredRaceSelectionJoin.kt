package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "race_selection_join",
		primaryKeys = ["raceId", "selectionId"],
		foreignKeys = [
			ForeignKey(entity = StoredRace::class,
					parentColumns = ["id"],
					childColumns = ["raceId"]),
			ForeignKey(entity = StoredRaceSelection::class,
					parentColumns = ["id"],
					childColumns = ["selectionId"])
		])
data class StoredRaceSelectionJoin(val raceId: String,
								   @ColumnInfo(index = true) val selectionId: String, val selectionState: Int)