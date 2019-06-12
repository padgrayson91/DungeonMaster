package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "class_selection_join",
		primaryKeys = ["classId", "selectionId"],
		foreignKeys = [
			ForeignKey(entity = StoredCharacterClass::class,
						parentColumns = ["id"],
						childColumns = ["classId"]),
			ForeignKey(entity = StoredCharacterClassSelection::class,
					parentColumns = ["id"],
					childColumns = ["selectionId"])
		])
data class StoredSelectionCharacterClassJoin(val classId: String, val selectionId: String, val selectionState: Int)
