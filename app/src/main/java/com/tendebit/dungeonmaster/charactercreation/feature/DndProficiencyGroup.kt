package com.tendebit.dungeonmaster.charactercreation.feature

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import java.lang.IllegalStateException

class DndProficiencyGroup(val availableOptions: List<DndProficiency>, val selectedOptions: MutableList<DndProficiency>, private val totalChoiceCount: Int) {

	init {
		if (availableOptions.size < totalChoiceCount) {
			throw IllegalStateException("Not enough choices available for group. Expected at least $totalChoiceCount but had ${availableOptions.size}")
		}
	}

	fun remainingChoices() = totalChoiceCount - selectedOptions.size

}