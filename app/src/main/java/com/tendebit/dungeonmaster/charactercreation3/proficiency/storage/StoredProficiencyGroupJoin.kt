package com.tendebit.dungeonmaster.charactercreation3.proficiency.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(tableName = "proficiency_group_join",
		primaryKeys = ["proficiencyId", "groupId"],
		foreignKeys = [
			ForeignKey(entity = StoredProficiency::class,
					parentColumns = ["id"],
					childColumns = ["proficiencyId"]),
			ForeignKey(entity = StoredProficiencyGroup::class,
					parentColumns = ["id"],
					childColumns = ["groupId"])
		])
data class StoredProficiencyGroupJoin(val proficiencyId: String,
									  @ColumnInfo(index = true) val groupId: String)