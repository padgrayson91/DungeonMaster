package com.tendebit.dungeonmaster.charactercreation3.proficiency.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "proficiency_selection_to_proficiency_join",
		primaryKeys = ["selectionId", "proficiencyId", "groupId"],
		foreignKeys = [
			ForeignKey(entity = StoredProficiencyGroup::class,
					parentColumns = ["id"],
					childColumns = ["groupId"]),
			ForeignKey(entity = StoredProficiencySelection::class,
					parentColumns = ["id"],
					childColumns = ["selectionId"]),
			ForeignKey(entity = StoredProficiency::class,
					parentColumns = ["id"],
					childColumns = ["proficiencyId"])])
data class StoredProficiencySelectionToProficiencyJoin(@ColumnInfo(index = true) val selectionId: String, @ColumnInfo(index = true) val groupId: String,
													   @ColumnInfo(index = true) val proficiencyId: String, val selectionState: Int)