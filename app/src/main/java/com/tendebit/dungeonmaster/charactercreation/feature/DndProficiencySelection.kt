package com.tendebit.dungeonmaster.charactercreation.feature

class DndProficiencySelection(val proficiency: DndProficiency, val group: DndProficiencyGroup) {

	override fun toString(): String {
		return proficiency.name
	}

}
