package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network

import com.google.gson.annotations.SerializedName

@Suppress("UNUSED")
class DndRaceDetails {

	lateinit var name: String
	lateinit var url: String
	@SerializedName("starting_proficiency_options")
	var proficiencyChoices: DndProficiencyGroupDirectory? = null
	@SerializedName("starting_proficiencies")
	lateinit var proficiencies: List<DndProficiencyDirectory>

	override fun toString(): String {
		return ("\nInfo for class " + name + ": \n"
				+ " Natural Proficiencies: \n" + proficiencies + "\n"
				+ " Proficiency Choices: \n" + proficiencyChoices)
	}

}