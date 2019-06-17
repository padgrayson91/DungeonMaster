package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "proficiency_selection_join",
		primaryKeys = ["selectionId", "groupId"],
		foreignKeys = [
			ForeignKey(entity = StoredProficiencySelection::class,
					parentColumns = ["id"],
					childColumns = ["selectionId"]),
			ForeignKey(entity = StoredProficiencyGroup::class,
					parentColumns = ["id"],
					childColumns = ["groupId"])
		]
	   )
data class StoredProficiencySelectionJoin(@ColumnInfo(index = true) val groupId: String, @ColumnInfo(index = true) val selectionId: String, val sortOrder: Int)