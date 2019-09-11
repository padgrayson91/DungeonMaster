package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation3.race.DndDetailedRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

@Entity(tableName = "races")
data class StoredRace(
		@PrimaryKey val id: String,
		@ColumnInfo(name = "race_name") var name: String) {

	companion object {
		fun fromDndRace(dndRace: DndRace): StoredRace {
			return StoredRace(dndRace.detailsUrl, dndRace.name)
		}

		fun fromDetailedRace(dndDetailedRace: DndDetailedRace): StoredRace {
			return fromDndRace(dndDetailedRace.origin) // TODO: need to store additional details
		}
	}

	fun toDndRace(): DndRace {
		return DndRace(this.name, this.id)
	}

}