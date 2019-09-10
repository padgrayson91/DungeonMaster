package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency

@Entity(tableName = "proficiencies")
data class StoredProficiency(@PrimaryKey val id: String,
							 @ColumnInfo(name = "proficiency_name") val name: String) {

	companion object {
		fun fromDndProficiency(dndProficiency: DndProficiency): StoredProficiency {
			return StoredProficiency(dndProficiency.identifier, dndProficiency.name)
		}
	}

	fun toDndProficiency(): DndProficiency {
		return DndProficiency(this.name, this.id)
	}

}
