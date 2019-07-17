package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network

import com.google.gson.annotations.SerializedName

@Suppress("UNUSED")
class DndCharacterClassDetail {

	lateinit var name: String
	lateinit var url: String
	@SerializedName("hit_die")
	private val hitDie: Int = 0
	@SerializedName("proficiency_choices")
	lateinit var proficiencyChoices: List<DndProficiencyGroupDirectory>
	val proficiencies: List<DndProficiencyDirectory>? = null

	override fun toString(): String {
		return ("\nInfo for class " + name + ": \n"
				+ " Hit Die: " + hitDie + "\n"
				+ " Natural Proficiencies: \n" + proficiencies + "\n"
				+ " Proficiency Choices: \n" + proficiencyChoices)
	}

}
