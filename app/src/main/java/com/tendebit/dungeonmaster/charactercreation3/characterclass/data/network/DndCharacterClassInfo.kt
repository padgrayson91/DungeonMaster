package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network

import com.google.gson.annotations.SerializedName
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndDetailedCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.data.network.DndProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.data.network.DndProficiencyGroupDirectory

class DndCharacterClassInfo {

	lateinit var name: String
	lateinit var url: String
	@SerializedName("hit_die")
	var hitDie: Int = 0
	@SerializedName("proficiency_choices")
	lateinit var proficiencyChoices: List<DndProficiencyGroupDirectory>
	var proficiencies: List<DndProficiencyDirectory>? = null

	override fun toString(): String {
		return ("\nInfo for class " + name + ": \n"
				+ " Hit Die: " + hitDie + "\n"
				+ " Natural Proficiencies: \n" + proficiencies + "\n"
				+ " Proficiency Choices: \n" + proficiencyChoices)
	}

	fun toDetailedCharacterClass(): DndDetailedCharacterClass {
		val proficiencyChoices = proficiencyChoices.map { it.toDndProficiencyGroup() }
		val nativeProficiencies = proficiencies?.map { it.toDndProficiency() } ?: emptyList()

		return DndDetailedCharacterClass(name, url, proficiencyChoices, hitDie, nativeProficiencies)
	}

}
