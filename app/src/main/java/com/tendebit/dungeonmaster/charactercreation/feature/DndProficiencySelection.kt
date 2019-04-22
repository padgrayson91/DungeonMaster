package com.tendebit.dungeonmaster.charactercreation.feature

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency

class DndProficiencySelection(val proficiency: DndProficiency, val group: DndProficiencyGroup) {

	override fun toString(): String {
		return proficiency.name
	}

}
