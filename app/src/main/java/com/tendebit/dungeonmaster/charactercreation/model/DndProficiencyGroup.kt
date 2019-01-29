package com.tendebit.dungeonmaster.charactercreation.model

import java.lang.IllegalStateException

class DndProficiencyGroup(val availableOptions: List<DndProficiency>, val selectedOptions: MutableList<DndProficiency>, private val totalChoiceCount: Int) {

	init {
		if (availableOptions.size < totalChoiceCount) {
			throw IllegalStateException("Not enough choices available for group. Expected at least $totalChoiceCount but had ${availableOptions.size}")
		}
	}

	fun remainingChoices() = totalChoiceCount - selectedOptions.size

}