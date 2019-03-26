package com.tendebit.dungeonmaster.charactercreation2.feature

class DndProficiencySelection(val proficiency: DndProficiency, val group: DndProficiencyGroup) {

	override fun toString(): String {
		return proficiency.name
	}

}
