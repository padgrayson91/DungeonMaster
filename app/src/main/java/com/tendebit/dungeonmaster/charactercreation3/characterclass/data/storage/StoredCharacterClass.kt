package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndDetailedCharacterClass

@Entity(tableName = "classes")
data class StoredCharacterClass(
		@PrimaryKey var id: String,
		@ColumnInfo(name = "class_name") var name: String,
		@ColumnInfo(name = "hit_die") var hitDie: Int? = null) {

	companion object {
		fun fromDndCharacterClass(dndCharacterClass: DndCharacterClass): StoredCharacterClass {
			return StoredCharacterClass(dndCharacterClass.detailsUrl, dndCharacterClass.name)
		}

		fun fromDetailedCharacterClass(characterClass: DndDetailedCharacterClass): StoredCharacterClass {
			return StoredCharacterClass(characterClass.id, characterClass.name, characterClass.hitDie)
		}
	}

	fun toDndCharacterClass(): DndCharacterClass {
		return DndCharacterClass(this.name, this.id)
	}

}
