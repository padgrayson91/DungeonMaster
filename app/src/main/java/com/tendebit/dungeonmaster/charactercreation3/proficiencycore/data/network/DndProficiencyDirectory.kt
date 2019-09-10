package com.tendebit.dungeonmaster.charactercreation3.proficiencycore.data.network

import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency

class DndProficiencyDirectory {

	lateinit var name: String
	lateinit var url: String

	override fun toString(): String {
		return "Entry for proficiency $name can be found at $url\n"
	}

	fun toDndProficiency() : DndProficiency = DndProficiency(name, url)

}
