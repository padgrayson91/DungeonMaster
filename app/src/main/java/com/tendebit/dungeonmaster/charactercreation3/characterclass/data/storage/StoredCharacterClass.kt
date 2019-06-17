package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass

@Entity(tableName = "classes")
data class StoredCharacterClass(
		@PrimaryKey var id: String,
		@ColumnInfo(name = "class_name") var name: String) {

	companion object {
		fun fromDndCharacterClass(dndCharacterClass: DndCharacterClass): StoredCharacterClass {
			return StoredCharacterClass(dndCharacterClass.detailsUrl, dndCharacterClass.name)
		}
	}

	fun toDndCharacterClass(): DndCharacterClass {
		return DndCharacterClass(this.name, this.id)
	}

}
