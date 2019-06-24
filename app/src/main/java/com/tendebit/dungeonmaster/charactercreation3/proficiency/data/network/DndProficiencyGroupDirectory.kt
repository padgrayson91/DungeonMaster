package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmastercore.model.state.Normal

class DndProficiencyGroupDirectory {

	@SerializedName("choose")
	var choiceCount: Int = 0

	@SerializedName("from")
	lateinit var proficiencyOptions: List<DndProficiencyDirectory>

	override fun toString(): String {
		return ("Character may choose " + choiceCount + " from:\n"
				+ proficiencyOptions.toString())
	}

	fun toDndProficiencyGroup(): DndProficiencyGroup = DndProficiencyGroup(proficiencyOptions.map { Normal(it.toDndProficiency()) }, choiceCount)

}
