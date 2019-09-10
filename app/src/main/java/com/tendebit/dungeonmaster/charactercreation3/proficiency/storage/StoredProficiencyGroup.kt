package com.tendebit.dungeonmaster.charactercreation3.proficiency.storage

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencyGroup

@Entity(tableName = "proficiency_groups")
data class StoredProficiencyGroup(@PrimaryKey val id: String,
								 val choiceCount: Int) {

	companion object {

		fun fromDndGroup(group: DndProficiencyGroup): StoredProficiencyGroup {
			val itemIds = group.options.filter { it.item != null }.map { it.item!!.identifier }.hashCode()
			val id = "$itemIds+_choices_${group.choiceCount}"
			return StoredProficiencyGroup(id, group.choiceCount)
		}

	}

}